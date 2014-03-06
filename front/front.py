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
from bottle_sqlite import SQLitePlugin
import argparse


parser = argparse.ArgumentParser()
parser.add_argument("db")
args = parser.parse_args()
bottle.install(SQLitePlugin(dbfile=args.db))


@bottle.route('/', apply=[bottle.view('tpl/index.xml.tpl')])
def index(db):
    """
    Show full list of available circles.
    :param db: Database
    :return: HTML
    """
    cur = db.execute('SELECT id, city, tag FROM circle')
    bottle.response.set_header('Content-Type', 'text/xml')
    return dict(circles=cur.fetchall())


@bottle.route('/circle/<number:int>', apply=[bottle.view('tpl/circle.xml.tpl')])
def circle(db, number):
    """
    Show one circle page.
    :param db: Database
    :param number: Number of the circle
    """
    cur = db.execute(
        'SELECT user, value FROM rank WHERE circle = ? ORDER BY value DESC',
        (number,)
    )
    bottle.response.set_header('Content-Type', 'text/xml')
    return dict(ranks=cur.fetchall())


@bottle.route('/xsl/<path:path>')
def xsl(path):
    """
    Show static XSL file.
    :param path: File path
    :return: XSL content
    """
    bottle.response.set_header('Content-Type', 'application/xsl')
    return bottle.static_file(path, root='xsl')


if __name__ == '__main__':
    bottle.run(host=socket.gethostname(), port=8081)
