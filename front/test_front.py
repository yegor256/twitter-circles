#!/usr/bin/python
# coding=utf-8
"""
Copyright (c) 2009-2014, Curiost.com
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met: 1) Redistributions of source code must retain the above
copyright notice, this list of conditions and the following
disclaimer. 2) Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided
with the distribution. 3) Neither the name of the curiost.com nor
the names of its contributors may be used to endorse or promote
products derived from this software without specific prior written
permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.

To run this script just do:

$ py.test test_front.py
"""

import front
import sqlite3
import os
import unittest
import bottle
import bottle_sqlite
import webtest


os.chdir(os.path.dirname(os.path.realpath(__file__)))
db_path = '../target/sqlite.db'
if not os.path.isfile(db_path):
    raise BaseException('Sqlite database file is absent: %s' % db_path)


class TestMethods(unittest.TestCase):
    def setUp(self):
        self.connection = sqlite3.connect(db_path)
        self.db = self.connection.cursor()
        self.db.execute(
            """
            INSERT OR IGNORE INTO circle (city, tag) VALUES ('10,10,10mi', 'a')
            """
        )
        self.fix_circle = self.db.execute(
            "SELECT id FROM circle LIMIT 1"
        ).fetchone()[0]
        self.db.execute(
            """
            INSERT OR IGNORE INTO rank (circle, user) VALUES (?, 'someone')
            """,
            (self.fix_circle, )
        )
        self.fix_rank = self.db.execute(
            "SELECT id FROM rank WHERE circle = ? LIMIT 1", (self.fix_circle, )
        ).fetchone()[0]

    def tearDown(self):
        self.connection.commit()
        self.db.close()

    def test_index(self):
        """
        Index can render data from Sqlite DB.
        """
        front.index(self.db)

    def test_circle(self):
        """
        Circle page can render data from Sqlite DB.
        """
        front.circle(self.db, self.fix_circle)

    def test_delete(self):
        """
        Index can delete data from Sqlite DB.
        """
        with self.assertRaises(bottle.HTTPResponse):
            front.delete(self.db, self.fix_circle)

    def test_spam(self):
        """
        Index can mark rank as spam.
        """
        with self.assertRaises(bottle.HTTPResponse):
            front.spam(self.db, self.fix_circle, self.fix_rank)

    def test_static_xsl(self):
        """
        Static XSL page can be rendered.
        """
        front.xsl('layout.xsl')


class TestWeb(unittest.TestCase):
    def setUp(self):
        self.app = webtest.TestApp(front.app)
        self.plugin = bottle_sqlite.SQLitePlugin(dbfile=db_path)
        front.app.install(self.plugin)
        connection = sqlite3.connect(db_path)
        db = connection.cursor()
        db.execute(
            """
            INSERT OR IGNORE INTO circle (city, tag) VALUES ('10,10,10mi', 'a')
            """
        )
        self.fix_circle = db.execute(
            "SELECT id FROM circle LIMIT 1"
        ).fetchone()[0]
        db.close()
        connection.commit()

    def tearDown(self):
        front.app.uninstall(self.plugin)

    def test_index(self):
        """
        Test index page in full integration cycle.
        """
        assert self.app.get('/').status == '200 OK'

    def test_circle(self):
        """
        Test circle page in full integration cycle.
        """
        assert self.app.get('/circle/%d' % self.fix_circle).status == '200 OK'

    def test_xsl(self):
        """
        Test XSL static pages.
        """
        assert self.app.get('/xsl/layout.xsl').status == '200 OK'


if __name__ == '__main__':
    unittest.main()
