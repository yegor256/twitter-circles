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
import webtest


app = webtest.TestApp(front.app)


class TestMethods(unittest.TestCase):
    def setUp(self):
        path = 'target/sqlite.db'
        if not os.path.isfile(path):
            raise 'Sqlite database file is absent: %s' % path
        self.connection = sqlite3.connect(path)
        self.db = self.connection.cursor()

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
        front.circle(self.db, 1)

    def test_delete(self):
        """
        Index can delete data from Sqlite DB.
        """
        with self.assertRaises(bottle.HTTPResponse):
            front.delete(self.db, 1)

    def test_spam(self):
        """
        Index can mark rank as spam.
        """
        with self.assertRaises(bottle.HTTPResponse):
            front.spam(self.db, 1, 1)

    def test_static_xsl(self):
        """
        Static XSL page can be rendered.
        """
        front.xsl('layout.xsl')


class TestWeb(unittest.TestCase):
    def test_index_web(self):
        """
        Test index page in full integration cycle.
        """
        assert app.get('/').status == '200 OK'

    def test_circle_web(self):
        """
        Test circle page in full integration cycle.
        """
        assert app.get('/circle/1').status == '200 OK'
