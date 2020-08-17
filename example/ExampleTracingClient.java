/*
 * Copyright (C) 2020 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
