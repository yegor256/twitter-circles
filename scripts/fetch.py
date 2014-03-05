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
"""

__author__ = "Yegor Bugayenko"
__copyright__ = "Copyright 2009-2014, Curiost.com"
__license__ = "BSD"
__version__ = "0.1.0"
__maintainer__ = "Yegor Bugayenko"
__email__ = "yegor@tpc2.com"

import argparse
from circles.queue import Queue
from circles.twitter import Twitter
from circles.users import Users


def main():
    """
    Entry point.
    """
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "city",
        help="Twitter geo position, e.g. 37.78,-122.39,10mi"
    )
    parser.add_argument("tag", help="Hash tag")
    args = parser.parse_args()
    queue = Queue(args.city, args.tag)
    queue.push(Twitter().tweets(args.city, args.tag, queue.since()))
    users = Users()
    users.rank(queue.pull())
    for user in users.top(args.city, args.tag):
        print(user)


if __name__ == '__main__':
    main()
