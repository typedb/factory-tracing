/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package grabl.tracing.example;

import grabl.tracing.client.GrablTracing;
import grabl.tracing.client.GrablTracing.Analysis;
import grabl.tracing.client.GrablTracing.Trace;

import static grabl.tracing.client.GrablTracing.tracing;
import static grabl.tracing.client.GrablTracing.withLogging;

public class ExampleTracingClient {

    public static void main(String[] args) {
        int iterations = 10;
        if (args.length > 1) {
            iterations = Integer.parseInt(args[1]);
        }

        try (GrablTracing tracing = withLogging(tracing(args[0]))) {

            Analysis analysis = tracing.analysis("testowner", "testrepo", "testcommit");

            for (int i = 0; i < iterations; ++i) {
                Trace outerTrace = analysis.trace("test.root", "my:tracker", i);
                outerTrace.data("my data");
                outerTrace.labels("my", "labels");

                tracedFunction(Integer.parseInt(args[2]), Integer.parseInt(args[3]), outerTrace);

                outerTrace.end();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void tracedFunction(int depth, int width, Trace trace) {
        if (depth > 0) {
            for (int i = 0; i < width; i++) {
                Trace inner = trace.trace("depth-" + depth + "-iter-" + i);

                tracedFunction(depth - 1, width, inner);

                inner.labels("label1", "label2");
                inner.data("data");
                inner.end();
            }
        }
    }
}
