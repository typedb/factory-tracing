package grabl.tracing.test;

import grabl.tracing.protocol.TracingProto.Analysis;
import grabl.tracing.protocol.TracingProto.Trace;
import grabl.tracing.protocol.TracingServiceGrpc.TracingServiceImplBase;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

import static grabl.tracing.util.ProtobufUUIDUtil.toBuf;

public class TestTracingServer extends TracingServiceImplBase {
    private Server server;

    public TestTracingServer(int port) {
        server = ServerBuilder.forPort(port).addService(this).build();
    }

    public void start() throws Exception {
        server.start();
        server.awaitTermination();
    }

    @Override
    public void create(Analysis.Req request, StreamObserver<Analysis.Res> responseObserver) {
        UUID id = UUID.randomUUID();
        Analysis.Res response = Analysis.Res.newBuilder()
                .setAnalysisId(toBuf(id))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Trace.Req> stream(StreamObserver<Trace.Res> responseObserver) {
        return new StreamObserver<Trace.Req>() {
            @Override
            public void onNext(Trace.Req req) {
                System.out.print(req);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(Trace.Res.newBuilder().build());
                responseObserver.onCompleted();
            }
        };
    }

    public static void main(String[] args) {
        try {
            TestTracingServer server = new TestTracingServer(Integer.parseInt(args[0]));
            server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
