import java.util.UUID;

class TracingSession {
    void traceRootStart(UUID traceId, String name, String tracker, int iteration, long startMillis, String... labels) {

    }

    void traceChildStart(UUID traceID, UUID parentId, String name, long startMillis, String... labels) {

    }

    void traceEnd(UUID traceId, long endMillis, String... labels) {

    }

    void traceData(UUID traceId, String data) {

    }
}
