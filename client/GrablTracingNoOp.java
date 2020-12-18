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

package grabl.tracing.client;

import java.util.UUID;

/**
 * A simple no-operation GrablTracing.
 *
 * Uses the Bill Pugh method of lazy singleton instantiation.
 */
public class GrablTracingNoOp implements GrablTracing {

    private GrablTracingNoOp() {
    }

    private static class LazyHolder {
        private static final GrablTracingNoOp TRACING = new GrablTracingNoOp();
        private static final TraceImpl TRACE = new TraceImpl();
        private static final AnalysisImpl ANALYSIS = new AnalysisImpl();
    }

    static GrablTracing getInstance() {
        return LazyHolder.TRACING;
    }

    @Override
    public Trace trace(UUID rootId, UUID parentId, String name) {
        return LazyHolder.TRACE;
    }

    @Override
    public Analysis analysis(String owner, String repo, String commit, String name) {
        return LazyHolder.ANALYSIS;
    }

    @Override
    public void close() {

    }

    private static class TraceImpl implements Trace {

        @Override
        public Trace trace(String name) {
            return LazyHolder.TRACE;
        }

        @Override
        public Trace data(String data) {
            return LazyHolder.TRACE;
        }

        @Override
        public Trace labels(String... labels) {
            return LazyHolder.TRACE;
        }

        @Override
        public Trace end() {
            return LazyHolder.TRACE;
        }

        @Override
        public UUID getRootId() {
            return null;
        }

        @Override
        public UUID getId() {
            return null;
        }
    }

    private static class AnalysisImpl implements Analysis {

        @Override
        public Trace trace(String name, String tracker, int iteration) {
            return LazyHolder.TRACE;
        }
    }
}
