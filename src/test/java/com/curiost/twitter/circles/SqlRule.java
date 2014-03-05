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

import com.jolbox.bonecp.BoneCPDataSource;
import javax.sql.DataSource;
import org.junit.Assume;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Integration case for {@link SqlCircles}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
final class SqlRule implements TestRule {

    /**
     * Sql source.
     */
    private static final SqlSource SRC = SqlRule.make();

    /**
     * URL.
     */
    private static final String URL = SqlRule.jdbc();

    @Override
    public Statement apply(final Statement statement,
        final Description description) {
        return statement;
    }

    /**
     * Get SQL source.
     * @return Source
     */
    public SqlSource source() {
        Assume.assumeNotNull(SqlRule.URL);
        return SqlRule.SRC;
    }

    /**
     * Make SQL source.
     * @return Source
     */
    private static SqlSource make() {
        final BoneCPDataSource src = new BoneCPDataSource();
        src.setDriverClass("org.sqlite.JDBC");
        src.setJdbcUrl(SqlRule.jdbc());
        return new SqlSource() {
            @Override
            public DataSource get() {
                return src;
            }
        };
    }

    /**
     * Make JDBC URL.
     * @return Url
     */
    private static String jdbc() {
        return System.getProperty("failsafe.sqlite.jdbc");
    }

}
