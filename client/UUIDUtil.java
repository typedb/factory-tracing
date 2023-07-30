/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.factory.tracing.client;

import com.google.protobuf.ByteString;

import java.nio.ByteBuffer;
import java.util.UUID;


/**
 * A utility class for converting between {@link UUID} and a protobuf byte array containing the 4 most significant bytes
 * followed by the 4 least significant bytes. This packs the UUID more efficiently than using a {@link String}.
 *
 * The ordering of the bytes follows Java's {@link ByteBuffer} convention. {@link ByteString}s made using this class
 * will always correctly be deserialized regardless of system endianness since Java defines its own endianness and
 * protobuf preserves it.
 */
public class UUIDUtil {
    private enum EmptyUUID {
        INSTANCE;

        private UUID emptyUuid = new UUID(0, 0);
    }

    /**
     * Converts a protobuf {@link ByteString} into a Java {@link UUID}.
     *
     * @param uuid A protobuf UUID (MSB, LSB)
     * @return The equivalent Java UUID.
     */
    public static UUID fromBuf(ByteString uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return EmptyUUID.INSTANCE.emptyUuid;
        }
        ByteBuffer buffer = ByteBuffer.allocate(16).put(uuid.asReadOnlyByteBuffer());
        buffer.rewind();
        long msb = buffer.getLong();
        long lsb = buffer.getLong();
        return new UUID(msb, lsb);
    }

    /**
     * Converts a Java {@link UUID} into a protobuf {@link ByteString}.
     *
     * @param uuid A Java {@link UUID}
     * @return The equivalent protobuf UUID (MSB, LSB)
     */
    public static ByteString toBuf(UUID uuid) {
        if (uuid == null) {
            return ByteString.EMPTY;
        }
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        buffer.rewind();
        return ByteString.copyFrom(buffer);
    }
}
