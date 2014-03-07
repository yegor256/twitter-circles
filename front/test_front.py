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
import pytest
import bottle


path = 'target/sqlite.db'
if not os.path.isfile(path):
    raise 'Sqlite database file is absent: %s' % path
conn = sqlite3.connect(path)
db = conn.cursor()


def test_index():
    """
    Index can render data from Sqlite DB.
    """
    front.index(db)


def test_circle():
    """
    Circle page can render data from Sqlite DB.
    """
    front.circle(db, 1)


def test_delete():
    """
    Index can delete data from Sqlite DB.
    """
    with pytest.raises(bottle.HTTPResponse):
        front.delete(db, 1)


def test_static_xsl():
    """
    Static XSL page can be rendered.
    """
    front.xsl('layout.xsl')

