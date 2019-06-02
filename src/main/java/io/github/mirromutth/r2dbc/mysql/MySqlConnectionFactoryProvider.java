/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mirromutth.r2dbc.mysql;

import io.github.mirromutth.r2dbc.mysql.constant.ZeroDate;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ConnectionFactoryProvider;
import io.r2dbc.spi.Option;

import static io.github.mirromutth.r2dbc.mysql.util.AssertUtils.requireNonNull;
import static io.r2dbc.spi.ConnectionFactoryOptions.CONNECT_TIMEOUT;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.SSL;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

/**
 * An implementation of {@link ConnectionFactoryProvider} for creating {@link MySqlConnectionFactory}s.
 */
public final class MySqlConnectionFactoryProvider implements ConnectionFactoryProvider {

    public static final String MYSQL_DRIVER = "mysql";

    /**
     * This option indicates special handling when MySQL server returning "zero date" (aka. "0000-00-00 00:00:00")
     */
    public static final Option<String> ZERO_DATE = Option.valueOf("zeroDate");

    @Override
    public ConnectionFactory create(ConnectionFactoryOptions options) {
        requireNonNull(options, "connectionFactoryOptions must not be null");

        MySqlConnectConfiguration.Builder builder = MySqlConnectConfiguration.builder();

        String zeroDate = options.getValue(ZERO_DATE);
        if (zeroDate != null) {
            builder.zeroDate(ZeroDate.valueOf(zeroDate.toUpperCase()));
        }

        Integer port = options.getValue(PORT);
        if (port != null) {
            builder.port(port);
        }

        CharSequence password = options.getValue(PASSWORD);
        if (password != null) {
            builder.password(password.toString());
        }

        Boolean ssl = options.getValue(SSL);
        if (ssl != null && ssl) {
            builder.enableSsl();
        }

        MySqlConnectConfiguration configuration = builder.host(options.getRequiredValue(HOST))
            .username(options.getRequiredValue(USER))
            .connectTimeout(options.getValue(CONNECT_TIMEOUT))
            .database(options.getValue(DATABASE))
            .build();

        return new MySqlConnectionFactory(configuration);
    }

    @Override
    public boolean supports(ConnectionFactoryOptions options) {
        requireNonNull(options, "connectionFactoryOptions must not be null");

        if (!MYSQL_DRIVER.equals(options.getValue(DRIVER))) {
            return false;
        }

        String zeroDate = options.getValue(ZERO_DATE);
        if (zeroDate != null && !isValidZeroDate(zeroDate)) {
            return false;
        }

        return options.hasOption(HOST) && options.hasOption(USER);
    }

    @Override
    public String getDriver() {
        return MYSQL_DRIVER;
    }

    private static boolean isValidZeroDate(String value) {
        for (ZeroDate zeroDate : ZeroDate.values()) {
            if (zeroDate.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
