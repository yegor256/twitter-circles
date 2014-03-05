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
import com.jcabi.aspects.Tv;
import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Utc;
import com.jcabi.jdbc.VoidHandler;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.time.DateUtils;

/**
 * SQL Buffer.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "source", "circle" })
final class SqlBuffer implements Buffer {

    private final transient SqlSource source;

    private final transient int circle;

    /**
     * Ctor.
     */
    SqlBuffer(final SqlSource src, final int crc) {
        this.source = src;
        this.circle = crc;
    }

    @Override
    public void push(final Iterable<Tweet> tweets) throws IOException {
        for (final Tweet tweet : tweets) {
            try {
                this.push(tweet);
            } catch (SQLException ex) {
                throw new IOException(ex);
            }
        }
    }

    @Override
    public Date recent() throws IOException {
        try {
            return new JdbcSession(this.source.get())
                .sql("SELECT date FROM buffer WHERE circle = ? ORDER BY date DESC LIMIT 1")
                .set(this.circle)
                .select(
                    new JdbcSession.Handler<Date>() {
                        @Override
                        public Date handle(final ResultSet rset,
                            final Statement stmt) throws SQLException {
                            rset.next();
                            return Utc.getTimestamp(rset, 1);
                        }
                    }
                );
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public Iterable<Tweet> pull() throws IOException {
        final Date threshold = DateUtils.addDays(new Date(), -Tv.TEN);
        final Collection<Tweet> tweets;
        try {
            tweets = new JdbcSession(this.source.get())
                .sql("SELECT user, date FROM buffer WHERE circle = ? AND date < ?")
                .set(this.circle)
                .set(new Utc(threshold))
                .select(
                    new JdbcSession.Handler<Collection<Tweet>>() {
                        @Override
                        public Collection<Tweet> handle(final ResultSet rset,
                            final Statement stmt) throws SQLException {
                            return SqlBuffer.fetch(rset);
                        }
                    }
                );
            new JdbcSession(this.source.get())
                .sql("DELETE FROM buffer WHERE city = ? AND tag = ? AND date < ?")
                .execute();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
        return tweets;
    }

    /**
     * Push one tweet.
     * @param tweet The tweet
     * @throws SQLException If fails
     */
    private void push(final Tweet tweet) throws SQLException {
        new JdbcSession(this.source.get())
            .sql("INSERT INTO buffer (circle, user, date) VALUES (?, ?, ?)")
            .set(this.circle)
            .set(tweet.user())
            .set(new Utc(tweet.date()))
            .insert(new VoidHandler());
    }

    /**
     * Fetch tweets from ResultSet.
     * @param rset Result set
     * @return Collection of them
     * @throws SQLException If fails
     */
    private static Collection<Tweet> fetch(final ResultSet rset)
        throws SQLException {
        final Collection<Tweet> tweets = new LinkedList<Tweet>();
        while (rset.next()) {
            tweets.add(
                new Tweet.Simple(
                    rset.getString(0),
                    Utc.getTimestamp(rset, 1)
                )
            );
        }
        return tweets;
    }

}
