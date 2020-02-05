package grabl.tracing.util;

import com.google.protobuf.ByteString;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ProtobufUUIDUtil {
    public static UUID fromBuf(ByteString uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16).put(uuid.asReadOnlyByteBuffer());
        buffer.rewind();
        long msb = buffer.getLong();
        long lsb = buffer.getLong();
        return new UUID(msb, lsb);
    }

    public static ByteString toBuf(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        buffer.rewind();
        return ByteString.copyFrom(buffer);
    }
}
