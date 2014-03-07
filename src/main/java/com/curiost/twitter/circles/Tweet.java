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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Tweet.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
interface Tweet {

    /**
     * Unique ID.
     * @return ID
     */
    long number();

    /**
     * User.
     * @return User name
     */
    @NotNull
    String user();

    /**
     * When it happened.
     * @return Date
     */
    @NotNull
    Date date();

    /**
     * Simple tweet.
     */
    @Immutable
    @Loggable(Loggable.DEBUG)
    @ToString
    @EqualsAndHashCode
    final class Simple implements Tweet {
        /**
         * Number.
         */
        private final transient long num;
        /**
         * Name.
         */
        private final transient String name;
        /**
         * When happened.
         */
        private final transient long when;
        /**
         * Ctor.
         * @param number Number of it
         * @param user User name
         * @param date Date of tweet
         */
        Simple(final long number, final String user, final Date date) {
            this.num = number;
            this.name = user;
            this.when = date.getTime();
        }
        @Override
        public long number() {
            return this.num;
        }
        @Override
        public String user() {
            return this.name;
        }
        @Override
        public Date date() {
            return new Date(this.when);
        }
    }

}
