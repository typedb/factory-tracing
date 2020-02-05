package grabl.tracing.client;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import grabl.tracing.protocol.TracingProto.Trace;
import grabl.tracing.protocol.TracingServiceGrpc.TracingServiceStub;
import io.grpc.stub.StreamObserver;

import static grabl.tracing.util.ProtobufUUIDUtil.toBuf;

/**
 * The GRPC client layer for the tracing session.
 *
 * This client is currently set up not to attempt to recover from errors, it will buffer any errors it receives from the
 * async listener Thread and throw them on the TracingSession user Thread when the next method is called. This has the
 * potential to be confusing, since a server-side issue caused by the client might not be related to the point when the
 * RuntimeException appears to be thrown. The hope is that the (suppressed) exceptions passed on will still be useful.
 *
 * For this setup to work, it is vital that all methods either call {@link #ensureConnection()} or check for errors
 * themselves and call {@link #throwErrors(String message)} directly.
 */
class TraceStream {
    private final StreamObserver<Trace.Req> requestObserver;
    private final CountDownLatch finishLatch = new CountDownLatch(1);

    private final List<Throwable> errors = new ArrayList<>();

    TraceStream(TracingServiceStub serviceStub) {
        requestObserver = serviceStub.stream(new TracingResponseObserver());
    }

    void traceRootStart(UUID traceId, UUID analysisId, String name, String tracker, int iteration, long startMillis) {
        ensureConnection();
        Trace.Req req = Trace.Req.newBuilder()
                .setId(toBuf(traceId))
                .setRootStart(Trace.Req.StartRoot.newBuilder()
                        .setAnalysisId(toBuf(analysisId))
                        .setTracker(tracker)
                        .setIteration(iteration))
                .setName(name)
                .setStarted(startMillis)
                .build();
        requestObserver.onNext(req);
    }

    void traceChildStart(UUID rootId, UUID traceId, UUID parentId, String name, long startMillis) {
        ensureConnection();
        Trace.Req.Builder reqBuilder = Trace.Req.newBuilder()
                .setId(toBuf(traceId))
                .setRootId(toBuf(rootId))
                .setParentId(toBuf(parentId))
                .setName(name)
                .setStarted(startMillis);

        requestObserver.onNext(reqBuilder.build());
    }

    void traceData(UUID rootId, UUID traceId, String data) {
        ensureConnection();
        Trace.Req req = Trace.Req.newBuilder()
                .setId(toBuf(traceId))
                .setRootId(toBuf(rootId))
                .setData(data)
                .build();
        requestObserver.onNext(req);
    }

    void traceLabels(UUID rootId, UUID traceId, String[] labels) {
        ensureConnection();
        Trace.Req req = Trace.Req.newBuilder()
                .setId(toBuf(traceId))
                .setRootId(toBuf(rootId))
                .addAllLabels(Arrays.asList(labels))
                .build();
        requestObserver.onNext(req);
    }

    void traceEnd(UUID rootId, UUID traceId, long endMillis) {
        ensureConnection();
        Trace.Req req = Trace.Req.newBuilder()
                .setId(toBuf(traceId))
                .setRootId(toBuf(rootId))
                .setEnded(endMillis)
                .build();
        requestObserver.onNext(req);
    }

    void close() throws Exception {
        requestObserver.onCompleted();

        try {
            finishLatch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            requestObserver.onError(e);
            throw e;
        }

        if (errors.size() != 0) {
            throwErrors(TraceStream.class.getSimpleName() + " error");
        }
    }

    private void ensureConnection() {
        if (finishLatch.getCount() == 0) {
            throwErrors(TraceStream.class.getSimpleName() + " already closed or lost");
        }
    }

    private void throwErrors(String message) {
        RuntimeException ex = new RuntimeException(message);
        for (Throwable error : errors) {
            ex.addSuppressed(error);
        }
        throw ex;
    }

    private class TracingResponseObserver implements StreamObserver<Trace.Res> {

        @Override
        public void onNext(Trace.Res res) {
        }

        @Override
        public void onError(Throwable throwable) {
            errors.add(throwable);
            finishLatch.countDown();
        }

        @Override
        public void onCompleted() {
            finishLatch.countDown();
        }
    }
}
