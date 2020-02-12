package grabl.tracing.client;

import java.util.UUID;

public interface GrablTracing extends AutoCloseable {
    Trace trace(UUID rootId, UUID parentId, String name);
    Analysis analysis(String owner, String repo, String commit);

    interface Analysis {
        Trace trace(String name, String tracker, int iteration);
    }

    interface Trace {
        Trace trace(String name);
        Trace data(String data);
        Trace labels(String... labels);
        Trace end();
    }
}
