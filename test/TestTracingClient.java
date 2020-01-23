package grakn.grabl_tracing.test;

import grakn.grabl_tracing.GrablTracing;

public class TestTracingClient {
    public static void main(String args[]) {
        try (GrablTracing tracing =
                     new GrablTracing(args[0], "test")) {

            try (GrablTracing.Analysis analysis =
                         tracing.analysis("testowner", "testrepo", "testcommit")) {

                for (int i = 0; i < 10; ++i) {
                    GrablTracing.Trace outerTrace = analysis.trace("test.root", "my:tracker", i);
                    outerTrace.data("my data");
                    outerTrace.labels("my", "labels");

                    GrablTracing.Trace innerTrace = outerTrace.trace("inner");
                    innerTrace.data("my inner data");
                    innerTrace.labels("my", "inner", "labels");
                    innerTrace.end();

                    outerTrace.end();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
