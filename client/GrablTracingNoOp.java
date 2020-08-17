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
    public Analysis analysis(String owner, String repo, String commit) {
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
