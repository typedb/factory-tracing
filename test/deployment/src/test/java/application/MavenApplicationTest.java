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
