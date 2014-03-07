/**
 * Copyright (c) 2009-2014, Curiost.com
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

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration case for {@link SqlBuffer}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
public final class SqlBufferITCase {

    /**
     * SQL source.
     * @checkstyle VisibilityModifierCheck (4 lines)
     */
    @Rule
    public final transient SqlRule sql = new SqlRule();

    /**
     * SqlBuffer can push tweets and pull them back.
     * @throws Exception If some problem inside
     */
    @Test
    public void pushesTweetsAndPulls() throws Exception {
        final Buffer buffer = new SqlBuffer(
            this.sql.source(), 1
        );
        final int age = 100;
        buffer.push(
            new Tweet.Simple(1L, "jeff", DateUtils.addDays(new Date(), -age))
        );
        buffer.push(
            new Tweet.Simple(2L, "peter", DateUtils.addDays(new Date(), -age))
        );
        MatcherAssert.assertThat(
            buffer.pull(),
            Matchers.<Tweet>iterableWithSize(2)
        );
        MatcherAssert.assertThat(
            buffer.pull(),
            Matchers.emptyIterable()
        );
    }

    /**
     * SqlBuffer can get recent date.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesRecentId() throws Exception {
        final Buffer buffer = new SqlBuffer(
            this.sql.source(), 1
        );
        buffer.push(new Tweet.Simple(1L, "bunny", new Date()));
        buffer.push(new Tweet.Simple(2L, "willy", new Date()));
        MatcherAssert.assertThat(
            buffer.latest(),
            Matchers.is(2L)
        );
    }

}
