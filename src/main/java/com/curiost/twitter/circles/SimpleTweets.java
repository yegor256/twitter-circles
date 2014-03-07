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
import com.jcabi.aspects.Tv;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Simple tweets.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "city", "tag", "since" })
final class SimpleTweets implements Tweets {

    /**
     * Pattern for the city.
     */
    private static final Pattern PTN = Pattern.compile(
        "(-?\\d+(?:\\.\\d+)?),(-?\\d+(?:\\.\\d+)?),(\\d+)(mi|km)"
    );

    /**
     * Twitter key.
     */
    private final transient String oauth;

    /**
     * City.
     */
    private final transient String city;

    /**
     * Tag.
     */
    private final transient String tag;

    /**
     * Last seen.
     */
    private final transient long since;

    /**
     * Ctor.
     * @param key Twitter key
     * @param cty City
     * @param hash Tag
     * @param latest Latest tweet seen
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    SimpleTweets(final String key, final String cty,
        final String hash, final long latest) {
        this.oauth = key;
        this.city = cty;
        this.tag = hash;
        this.since = latest;
    }

    @Override
    public Iterable<Tweet> fetch() throws IOException {
        return new Iterable<Tweet>() {
            @Override
            public Iterator<Tweet> iterator() {
                return new SimpleTweets.Row(SimpleTweets.this.query());
            }
        };
    }

    /**
     * Make a query.
     * @return Query
     */
    private Query query() {
        final Matcher matcher = SimpleTweets.PTN.matcher(this.city);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                String.format("invalid city format: %s", this.city)
            );
        }
        final Query query = new Query(this.tag);
        query.setGeoCode(
            new GeoLocation(
                Double.parseDouble(matcher.group(1)),
                Double.parseDouble(matcher.group(2))
            ),
            Double.parseDouble(matcher.group(Tv.THREE)),
            matcher.group(Tv.FOUR)
        );
        if (this.since != 0) {
            query.setSinceId(this.since);
        }
        return query;
    }

    /**
     * Make twitter client.
     * @return Twitter client
     */
    private Twitter twitter() {
        return new TwitterFactory(
            new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(this.oauth)
                .setOAuthConsumerSecret(
                    "aZS6RIOJSwHkxHJI1TwTgJZPuDObHZ6LDDDDXoioHA"
                )
                .setOAuthAccessToken(
                    "225097272-uO2hpD41EHYfp76fvg1x5LcRsTRSDU2LNcROJEzE"
                )
                .setOAuthAccessTokenSecret(
                    "QnnE41YL0d6rTYsjAnfPbbq3PaVvDzlZm6Ngvf4MtCQmr"
                )
                .build()
        ).getInstance();
    }

    /**
     * Endless row of tweets.
     */
    private final class Row implements Iterator<Tweet> {
        /**
         * Items.
         */
        private final transient Queue<Status> items = new LinkedList<Status>();
        /**
         * Recent query.
         */
        private transient Query query;
        /**
         * Ctor.
         * @param qry Query
         */
        Row(final Query qry) {
            this.query = qry;
        }
        @Override
        public boolean hasNext() {
            if (this.items.isEmpty() && this.query != null) {
                final QueryResult result;
                try {
                    result = SimpleTweets.this.twitter().search(this.query);
                } catch (TwitterException ex) {
                    throw new IllegalArgumentException(ex);
                }
                this.query = result.nextQuery();
                this.items.addAll(result.getTweets());
            }
            return !this.items.isEmpty();
        }
        @Override
        public Tweet next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("no more tweets");
            }
            final Status status = this.items.poll();
            return new Tweet.Simple(
                status.getId(),
                status.getUser().getScreenName(),
                status.getCreatedAt()
            );
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException("#remove()");
        }
    }

}
