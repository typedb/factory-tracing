package grakn.grabl_tracing;

import java.util.Arrays;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import grakn.grabl_tracing.protocol.TracingProto;
import grakn.grabl_tracing.protocol.TracingProto.Trace;
import grakn.grabl_tracing.protocol.TracingServiceGrpc.TracingServiceStub;
import io.grpc.stub.StreamObserver;

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
class TracingSession {
    private final String analysisId;
    private final StreamObserver<Trace.Req> requestObserver;
    private final CountDownLatch finishLatch = new CountDownLatch(1);

    private final Queue<Throwable> errors = new ConcurrentLinkedQueue<>();

    TracingSession(TracingServiceStub serviceStub, String analysisId) {
        this.analysisId = analysisId;
        requestObserver = serviceStub.stream(new TracingResponseObserver());
    }

    void traceRootStart(UUID traceId, String name, String tracker, int iteration, long startMillis) {
        ensureConnection();
        Trace.Req req = Trace.Req.newBuilder()
                .setId(toBuf(traceId))
                .setAnalysisId(analysisId)
                .setName(name)
                .setTracker(tracker)
                .setIteration(iteration)
                .setStartTimeMillis(startMillis)
                .build();
        requestObserver.onNext(req);
    }

    void traceChildStart(UUID traceId, UUID parentId, String name, long startMillis) {
        ensureConnection();
        Trace.Req req = Trace.Req.newBuilder()
                .setId(toBuf(traceId))
                .setParentTraceId(toBuf(parentId))
                .setName(name)
                .setStartTimeMillis(startMillis)
                .build();
        requestObserver.onNext(req);
    }

    void traceData(UUID traceId, String data) {
        ensureConnection();
        Trace.Req req = Trace.Req.newBuilder()
                .setId(toBuf(traceId))
                .setData(data)
                .build();
        requestObserver.onNext(req);
    }

    void traceLabels(UUID traceId, String[] labels) {
        ensureConnection();
        Trace.Req req = Trace.Req.newBuilder()
                .setId(toBuf(traceId))
                .addAllLabels(Arrays.asList(labels))
                .build();
        requestObserver.onNext(req);
    }

    void traceEnd(UUID traceId, long endMillis) {
        ensureConnection();
        Trace.Req req = Trace.Req.newBuilder()
                .setId(toBuf(traceId))
                .setEndTimeMillis(endMillis)
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

        if (errors.peek() != null) {
            throwErrors(TracingSession.class.getSimpleName() + " error");
        }
    }

    private void ensureConnection() {
        if (finishLatch.getCount() == 0) {
            throwErrors(TracingSession.class.getSimpleName() + " already closed or lost");
        }
    }

    private void throwErrors(String message) {
        RuntimeException ex = new RuntimeException(message);
        Throwable error;
        while((error = errors.poll()) != null) {
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

    private static TracingProto.UUID.Builder toBuf(UUID uuid) {
        return TracingProto.UUID.newBuilder()
                .setMsb(uuid.getMostSignificantBits())
                .setLsb(uuid.getLeastSignificantBits());
    }
}
