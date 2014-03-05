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

import com.google.common.collect.Iterables;
import com.jcabi.aspects.Tv;
import com.jcabi.log.Logger;
import com.jolbox.bonecp.BoneCPDataSource;
import java.util.logging.Level;
import javax.sql.DataSource;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Entry point.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
public final class Main {

    /**
     * Entry point.
     * @param args Command line args
     * @throws Exception If fails
     */
    public static void main(final String... args) throws Exception {
        final OptionParser parser = Main.parser();
        final OptionSet opts = parser.parse(args);
        if (opts.has("help")) {
            parser.printHelpOn(Logger.stream(Level.INFO, Main.class));
        } else {
            final String city = opts.valueOf("city").toString();
            final String tag = opts.valueOf("tag").toString();
            final SqlSource sql = new SqlSource() {
                @Override
                public DataSource get() {
                    final BoneCPDataSource src = new BoneCPDataSource();
                    src.setDriverClass("org.sqlite.JDBC");
                    src.setJdbcUrl(opts.valueOf("jdbc").toString());
                    return src;
                }
            };
            final int circle = new SqlCircles(sql).find(city, tag);
            final Buffer buffer = new SqlBuffer(sql, circle);
            final Iterable<String> users = Iterables.limit(
                new Search(
                    new SimpleTweets(city, tag, buffer.recent()),
                    buffer,
                    new SqlRanks(sql, circle)
                ).users(),
                Tv.HUNDRED
            );
            for (final String user : users) {
                Logger.info(Main.class, "twitter user: %s", user);
            }
        }
    }

    /**
     * Build a parser.
     * @return Parser
     */
    private static OptionParser parser() {
        final OptionParser parser = new OptionParser();
        parser.accepts("help", "Show detailed instructions").forHelp();
        parser.accepts("city", "Twitter geolocation, e.g. '13.4,-15.5,3mi'")
            .withRequiredArg().ofType(String.class);;
        parser.accepts("tag", "Twitter hash tag")
            .withRequiredArg().ofType(String.class);
        parser.accepts("jdbc", "JDBC URL in 'jdbc:sqlite:/file/name' format")
            .withRequiredArg().ofType(String.class);
        return parser;
    }

}
