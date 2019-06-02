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

package io.github.mirromutth.r2dbc.mysql.message.client;

import io.github.mirromutth.r2dbc.mysql.internal.MySqlSession;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * The request message tells the MySQL client to exit.
 */
public final class ExitMessage extends AbstractClientMessage {

    private static final ExitMessage INSTANCE = new ExitMessage();

    private ExitMessage() {
    }

    public static ExitMessage getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isSequenceIdReset() {
        return true;
    }

    @Override
    protected ByteBuf encodeSingle(ByteBufAllocator bufAllocator, MySqlSession session) {
        return bufAllocator.buffer(1, 1).writeByte(0x01);
    }

    @Override
    public String toString() {
        return "ExitMessage{}";
    }
}