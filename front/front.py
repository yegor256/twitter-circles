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

$ python front.py
"""

import bottle
import socket
import bottle_sqlite
import argparse


app = bottle.Bottle()


@app.get('/', apply=[bottle.view('tpl/index.xml.tpl')])
def index(db):
    """
    Show full list of available circles.
    :param db: Database
    :return: HTML
    """
    bottle.response.set_header('Content-Type', 'text/xml')
    return dict(
        circles=db.execute(
            """
            SELECT c.id, city, tag, COUNT(t.id) AS sum
            FROM circle AS c
            JOIN tweet AS t ON c.id = t.circle
            GROUP BY c.id
            ORDER BY t.date
            """
        ).fetchall()
    )


@app.get('/circle/<number:int>', apply=[bottle.view('tpl/circle.xml.tpl')])
def circle(db, number):
    """
    Show one circle page.
    :param db: Database
    :param number: Number of the circle
    """
    crc = db.execute(
        """
        SELECT * FROM circle
        WHERE id = ?
        """,
        (number,)
    ).fetchone()
    if crc is None:
        bottle.abort(404, "Circle %d not found" % number)
    bottle.response.set_header('Content-Type', 'text/xml')
    return dict(
        ranks=db.execute(
            """
            SELECT rank.id, user, SUM(value) AS value FROM rank
            LEFT JOIN spam ON spam.rank = rank.id
            WHERE circle = ? AND spam.id IS NULL
            GROUP BY rank.id
            ORDER BY value DESC
            LIMIT 100
            """,
            (number,)
        ).fetchall(),
        circle=crc
    )


@app.get('/delete/<number:int>')
def delete(db, number):
    """
    Delete given circle.
    :param db: Database
    :param number: Number of the circle
    """
    db.execute(
        """
        DELETE FROM circle
        WHERE id = ?
        """,
        (number,)
    ).fetchall()
    bottle.redirect("/")


@app.get('/spam/<crc:int>/<number:int>')
def spam(db, crc, number):
    """
    Mark given rank as spam.
    :param db: Database
    :param crc: Circle ID
    :param number: Number of the rank
    """
    db.execute(
        """
        INSERT OR IGNORE INTO spam (rank)
        VALUES (?)
        """,
        (number,)
    ).fetchall()
    bottle.redirect("/circle/%s" % crc)


@app.get('/xsl/<path:path>')
def xsl(path):
    """
    Show static XSL file.
    :param path: File path
    :return: XSL content
    """
    bottle.response.set_header('Content-Type', 'application/xsl')
    return bottle.static_file(path, root='xsl')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("db")
    args = parser.parse_args()
    app.install(bottle_sqlite.SQLitePlugin(dbfile=args.db))
    bottle.run(app=app, host=socket.gethostname(), port=8081)
