package grabl.tracing.test.deployment.src.test.java.application;

import grabl.tracing.client.GrablTracing;
import grabl.tracing.protocol.TracingProto;
import org.junit.Test;

public class MavenApplicationTest {
    @Test
    public void testImport() {
        GrablTracing tracing = GrablTracing.tracingNoOp();
        GrablTracing.Analysis analysis = tracing.analysis("owner", "repo", "commit");
        analysis.trace("trace", "tracker", 1).trace("sub").end();

        TracingProto.Trace.Req.newBuilder().setName("name").build();
    }
}
