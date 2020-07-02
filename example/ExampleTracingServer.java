package grabl.tracing.example;

import grabl.tracing.protocol.TracingProto.Analysis;
import grabl.tracing.protocol.TracingProto.Trace;
import grabl.tracing.protocol.TracingServiceGrpc.TracingServiceImplBase;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

import static grabl.tracing.protocol.util.ProtobufUUIDUtil.toBuf;

public class ExampleTracingServer extends TracingServiceImplBase {
    private Server server;

    public ExampleTracingServer(int port) {
        server = ServerBuilder.forPort(port).addService(this).build();
    }

    public void start() throws Exception {
        server.start();
        server.awaitTermination();
    }

    @Override
    public void create(Analysis.Req request, StreamObserver<Analysis.Res> responseObserver) {
        System.out.println("Create Request: " + request);
        UUID id = UUID.randomUUID();
        long idLong = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        Analysis.Res response = Analysis.Res.newBuilder()
                .setAnalysisId(idLong)
                .build();
        System.out.println("Create Response: " + response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Trace.Req> stream(StreamObserver<Trace.Res> responseObserver) {
        System.out.println("Trace Stream Started");

        return new StreamObserver<Trace.Req>() {
            @Override
            public void onNext(Trace.Req req) {
                System.out.print("Trace Request: " + req);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(Trace.Res.newBuilder().build());
                System.out.println("Trace Stream Completed");
                responseObserver.onCompleted();
            }
        };
    }

    public static void main(String[] args) {
        try {
            ExampleTracingServer server = new ExampleTracingServer(Integer.parseInt(args[0]));
            server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
