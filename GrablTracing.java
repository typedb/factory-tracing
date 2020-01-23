package grakn.grabl_tracing;

import grakn.grabl_tracing.protocol.TracingProto;
import grakn.grabl_tracing.protocol.TracingServiceGrpc;
import grakn.grabl_tracing.protocol.TracingServiceGrpc.TracingServiceBlockingStub;
import grakn.grabl_tracing.protocol.TracingServiceGrpc.TracingServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GrablTracing implements AutoCloseable {
    private final ManagedChannel channel;
    private final TracingServiceBlockingStub tracingServiceBlockingStub;
    private final TracingServiceStub tracingServiceStub;

    public GrablTracing(String grablUri, String apiKey) {
        //TODO authenticate with apiKey
        channel = ManagedChannelBuilder.forTarget(grablUri).usePlaintext().build();
        tracingServiceBlockingStub = TracingServiceGrpc.newBlockingStub(channel);
        tracingServiceStub = TracingServiceGrpc.newStub(channel);
    }

    public Analysis analysis(String owner, String repo, String commit) {
        return new Analysis(owner, repo, commit);
    }

    @Override
    public void close() throws Exception {
        channel.shutdown();
        channel.awaitTermination(10, TimeUnit.SECONDS);
        if (!channel.isTerminated()) {
            channel.shutdownNow();
        }
    }

    public class Analysis implements AutoCloseable {
        private TracingSession session;

        private Analysis(String owner, String repo, String commit) {
            TracingProto.Analysis.Req req = TracingProto.Analysis.Req.newBuilder()
                    .setOwner(owner)
                    .setRepo(repo)
                    .setCommit(commit)
                    .build();
            TracingProto.Analysis.Res res = tracingServiceBlockingStub.create(req);
            String analysisId = res.getAnalysisId();
            session = new TracingSession(tracingServiceStub, analysisId);
        }

        public Trace trace(String name, String tracker, int iteration) {
            return new TraceImpl(name, tracker, iteration);
        }

        @Override
        public void close() throws Exception {
            session.close();
        }

        private class TraceImpl implements Trace {
            private final UUID id;

            private TraceImpl(String name, String tracker, int iteration) {
                id = UUID.randomUUID();
                session.traceRootStart(id, name, tracker, iteration, System.currentTimeMillis());
            }

            private TraceImpl(UUID parentId, String name) {
                id = UUID.randomUUID();
                session.traceChildStart(id, parentId, name, System.currentTimeMillis());
            }

            @Override
            public Trace trace(String name) {
                return new TraceImpl(id, name);
            }

            @Override
            public Trace data(String data) {
                session.traceData(id, data);
                return this;
            }

            @Override
            public Trace labels(String... labels) {
                session.traceLabels(id, labels);
                return this;
            }

            @Override
            public Trace end() {
                session.traceEnd(id, System.currentTimeMillis());
                return this;
            }

        }
    }

    public interface Trace {
        Trace trace(String name);
        Trace data(String data);
        Trace labels(String... labels);
        Trace end();
    }
}
