/*
 * Copyright (C) 2021 Vaticle
 *
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

package com.vaticle.factory.tracing.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class FactoryTracingSlf4j implements FactoryTracing {
    private static final Logger LOG = LoggerFactory.getLogger(FactoryTracingSlf4j.class);

    private final FactoryTracing innerTracing;

    private FactoryTracingSlf4j(FactoryTracing inner) {
        innerTracing = inner;
    }

    static FactoryTracing wrapIfLoggingEnabled(FactoryTracing inner) {
        if (LOG.isTraceEnabled()) {
            return new FactoryTracingSlf4j(inner);
        } else {
            return inner;
        }
    }

    @Override
    public Trace trace(UUID rootId, UUID parentId, String name) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("trace: {} {} {} {}", rootId, parentId, name, Instant.now());
        }
        return new TraceImpl(innerTracing.trace(rootId, parentId, name), name);
    }

    @Override
    public Analysis analysis(String owner, String repo, String commit, String name) {
        LOG.trace("analysis: {} {} {} {}", owner, repo, commit, name);
        return new AnalysisImpl(innerTracing.analysis(owner, repo, commit, name));
    }

    @Override
    public FactoryTracing withLogging() {
        return this;
    }

    @Override
    public void close() throws Exception {
        LOG.trace("close");
        innerTracing.close();
    }

    private static class TraceImpl implements Trace {

        private final Trace innerTrace;
        private final String name;

        private TraceImpl(Trace innerTrace, String name) {
            this.innerTrace = innerTrace;
            this.name = name;
        }

        @Override
        public Trace trace(String name) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("trace.trace: {} {}", name, Instant.now());
            }
            return new TraceImpl(innerTrace.trace(name), name);
        }

        @Override
        public Trace data(String data) {
            LOG.trace("trace.data: {} {}", name, data);
            return wrapIfNecessary(innerTrace.data(data));
        }

        @Override
        public Trace labels(String... labels) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("trace.labels: {} {}", name, String.join(", ", Arrays.asList(labels)));
            }
            return wrapIfNecessary(innerTrace.labels(labels));
        }

        @Override
        public Trace end() {
            if (LOG.isTraceEnabled()) {
                LOG.trace("trace.end: {} {}", name, Instant.now());
            }
            return wrapIfNecessary(innerTrace.end());
        }

        @Override
        public UUID getRootId() {
            return innerTrace.getRootId();
        }

        @Override
        public UUID getId() {
            return innerTrace.getId();
        }

        private TraceImpl wrapIfNecessary(Trace returnedTrace) {
            return returnedTrace == innerTrace ? this : new TraceImpl(returnedTrace, name);
        }
    }

    private static class AnalysisImpl implements Analysis {

        private final Analysis innerAnalysis;

        private AnalysisImpl(Analysis innerAnalysis) {
            this.innerAnalysis = innerAnalysis;
        }

        @Override
        public Trace trace(String name, String tracker, int iteration) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("analysis.trace: {} {} {} {}", name, tracker, iteration, Instant.now());
            }
            return new TraceImpl(innerAnalysis.trace(name, tracker, iteration), name);
        }
    }
}
