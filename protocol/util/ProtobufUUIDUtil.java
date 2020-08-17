/*
 * Copyright (C) 2020 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package grabl.tracing.protocol.util;

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
public class ProtobufUUIDUtil {
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
