import java.util.UUID;

public class GrablTracing {
    public GrablTracing(String grablUri, String apiKey) {
        // Establish connection with GrablTracing server
    }

    public Analysis analysis(String owner, String repo, String commit) {
        return new Analysis(owner, repo, commit);
    }

    public class Analysis {
        TracingSession session;

        private Analysis(String owner, String repo, String commit) {
            // Request new analysis from server
            session = new TracingSession();
        }

        public Trace trace(String name, String tracker, int iteration, String... labels) {
            return new TraceImpl(name, tracker, iteration, labels);
        }

        private class TraceImpl implements Trace, EndedTrace {
            private final UUID id;

            private TraceImpl(String name, String tracker, int iteration, String... labels) {
                id = UUID.randomUUID();
                session.traceRootStart(id, name, tracker, iteration, System.currentTimeMillis(), labels);
            }

            private TraceImpl(UUID parentId, String name, String... labels) {
                id = UUID.randomUUID();
                session.traceChildStart(id, parentId, name, System.currentTimeMillis(), labels);
            }

            @Override
            public Trace trace(String name, String... labels) {
                return new TraceImpl(id, name, labels);
            }

            @Override
            public EndedTrace end(String... labels) {
                session.traceEnd(id, System.currentTimeMillis(), labels);
                return this;
            }

            @Override
            public void data(String data) {
                session.traceData(id, data);
            }
        }
    }

    public interface Trace {
        Trace trace(String name, String... labels);
        EndedTrace end(String... labels);
    }

    public interface EndedTrace {
        void data(String data);
    }
}
