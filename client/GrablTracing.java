package grabl.tracing.client;

import grabl.tracing.protocol.TracingProto;
import grabl.tracing.protocol.TracingServiceGrpc;
import grabl.tracing.protocol.TracingServiceGrpc.TracingServiceBlockingStub;
import grabl.tracing.protocol.TracingServiceGrpc.TracingServiceStub;
import io.grpc.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static grabl.tracing.util.ProtobufUUIDUtil.fromBuf;

public class GrablTracing implements AutoCloseable {
    private final ManagedChannel channel;
    private final TracingServiceBlockingStub tracingServiceBlockingStub;
    private final TracingServiceStub tracingServiceStub;

    private final TraceStream stream;

    public GrablTracing(String grablUri, String username, String apiKey) {
        //TODO authenticate with apiKey
        channel = ManagedChannelBuilder.forTarget(grablUri)
                .useTransportSecurity()
                .intercept(new GrablTokenAuthClientInterceptor(username, apiKey))
                .build();
        tracingServiceBlockingStub = TracingServiceGrpc.newBlockingStub(channel);
        tracingServiceStub = TracingServiceGrpc.newStub(channel);
        stream = new TraceStream(tracingServiceStub);
    }

    public Trace trace(UUID rootId, UUID parentId, String name) {
        return new Trace(rootId, parentId, name);
    }

    public Analysis analysis(String owner, String repo, String commit) {
        return new Analysis(owner, repo, commit);
    }

    @Override
    public void close() throws Exception {
        stream.close();
        channel.shutdown();
        channel.awaitTermination(10, TimeUnit.SECONDS);
        if (!channel.isTerminated()) {
            channel.shutdownNow();
        }
    }

    public class Analysis {

        private final UUID analysisId;

        private Analysis(String owner, String repo, String commit) {
            TracingProto.Analysis.Req req = TracingProto.Analysis.Req.newBuilder()
                    .setOwner(owner)
                    .setRepo(repo)
                    .setCommit(commit)
                    .build();
            TracingProto.Analysis.Res res = tracingServiceBlockingStub.create(req);
            analysisId = fromBuf(res.getAnalysisId());
        }

        public Trace trace(String name, String tracker, int iteration) {
            return new Trace(analysisId, name, tracker, iteration);
        }
    }

    public class Trace {
        private final UUID id;
        private final UUID rootId;

        private Trace(UUID analysisId, String name, String tracker, int iteration) {
            id = UUID.randomUUID();
            rootId = id;
            stream.traceRootStart(id, analysisId, name, tracker, iteration, System.currentTimeMillis());
        }

        private Trace(UUID rootId, UUID parentId, String name) {
            this.rootId = rootId;
            id = UUID.randomUUID();
            stream.traceChildStart(rootId, id, parentId, name, System.currentTimeMillis());
        }

        public Trace trace(String name) {
            return new Trace(rootId, id, name);
        }

        public Trace data(String data) {
            stream.traceData(rootId, id, data);
            return this;
        }

        public Trace labels(String... labels) {
            stream.traceLabels(rootId, id, labels);
            return this;
        }

        public Trace end() {
            stream.traceEnd(rootId, id, System.currentTimeMillis());
            return this;
        }
    }
}
