/**
 * Copyright (c) 2012-2013, Curiost.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the curiost.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.curiost.twitter.circles;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.jdbc.JdbcSession;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * SQL Ranks.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "source", "circle" })
final class SqlRanks implements Ranks {

    private final transient SqlSource source;

    private final transient int circle;

    /**
     * Ctor.
     */
    SqlRanks(final SqlSource src, final int crc) {
        this.source = src;
        this.circle = crc;
    }

    @Override
    public void add(final String user, final int value) {
        new JdbcSession(this.source.get())
            .sql("INSERT OR UPDATE rank ()");
    }

    @Override
    public Iterable<String> top() throws IOException {
        try {
            return new JdbcSession(this.source.get())
                .sql("SELECT user FROM rank WHERE circle = ? ORDER BY value DESC")
                .set(this.circle)
                .select(
                    new JdbcSession.Handler<Collection<String>>() {
                        @Override
                        public Collection<String> handle(final ResultSet rset,
                            final Statement stmt) throws SQLException {
                            final Collection<String> names =
                                new LinkedList<String>();
                            while (rset.next()) {
                                names.add(rset.getString(1));
                            }
                            return names;
                        }
                    }
                );
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }
}
