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

package com.vaticle.factory.tracing.client;

import com.vaticle.factory.tracing.protocol.TracingProto;
import com.vaticle.factory.tracing.protocol.TracingServiceGrpc;
import com.vaticle.factory.tracing.protocol.TracingServiceGrpc.TracingServiceBlockingStub;
import com.vaticle.factory.tracing.protocol.TracingServiceGrpc.TracingServiceStub;
import io.grpc.ManagedChannel;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class FactoryTracingStandard implements FactoryTracing {
    private final ManagedChannel channel;
    private final TracingServiceBlockingStub tracingServiceBlockingStub;
    private final TracingServiceStub tracingServiceStub;

    private final TraceStream stream;

    public FactoryTracingStandard(ManagedChannel channel) {
        this.channel = channel;
        tracingServiceBlockingStub = TracingServiceGrpc.newBlockingStub(channel);
        tracingServiceStub = TracingServiceGrpc.newStub(channel);
        stream = new TraceStream(tracingServiceStub);
    }

    public Trace trace(UUID rootId, UUID parentId, String name) {
        requireNonNull(rootId, "Cannot use null rootId");
        requireNonNull(parentId, "Cannot use null traceId");
        requireNonNull(name, "Cannot use null name");
        return new TraceImpl(rootId, parentId, name);
    }

    public Analysis analysis(String owner, String repo, String commit, String analysisName) {
        requireNonNull(owner, "Cannot use null owner");
        requireNonNull(repo, "Cannot use null repo");
        requireNonNull(commit, "Cannot use null commit");
        requireNonNull(analysisName, "Cannot use null analysis name");
        return new AnalysisImpl(owner, repo, commit, analysisName);
    }

    @Override
    public void close() throws Exception {
        stream.close();
        channel.shutdown();
        channel.awaitTermination(10, TimeUnit.SECONDS);
        if (!channel.isTerminated()) {
            channel.shutdownNow();
        }
    }

    private class AnalysisImpl implements Analysis {

        private final long analysisId;

        private AnalysisImpl(String owner, String repo, String commit, String analysisName) {
            TracingProto.Analysis.Req req = TracingProto.Analysis.Req.newBuilder()
                    .setOwner(owner)
                    .setRepo(repo)
                    .setCommit(commit)
                    .setName(analysisName)
                    .build();
            TracingProto.Analysis.Res res = tracingServiceBlockingStub.create(req);
            analysisId = res.getAnalysisId();
        }

        public Trace trace(String name, String tracker, int iteration) {
            requireNonNull(name, "Cannot use null name");
            requireNonNull(tracker, "Cannot use null tracker");
            return new TraceImpl(analysisId, name, tracker, iteration);
        }
    }

    private class TraceImpl implements Trace {
        private final UUID id;
        private final UUID rootId;

        private TraceImpl(long analysisId, String name, String tracker, int iteration) {
            id = UUID.randomUUID();
            rootId = id;
            stream.traceRootStart(id, analysisId, name, tracker, iteration, System.currentTimeMillis());
        }

        private TraceImpl(UUID rootId, UUID parentId, String name) {
            this.rootId = rootId;
            id = UUID.randomUUID();
            stream.traceChildStart(rootId, id, parentId, name, System.currentTimeMillis());
        }

        public Trace trace(String name) {
            requireNonNull(name, "Cannot use null name");
            return new TraceImpl(rootId, id, name);
        }

        public Trace data(String data) {
            requireNonNull(data, "Cannot use null data");
            stream.traceData(rootId, id, data);
            return this;
        }

        public Trace labels(String... labels) {
            stream.traceLabels(rootId, id, labels);
            return this;
        }

        public Trace end() {
            stream.traceEnd(rootId, id, System.currentTimeMillis());
            return this;
        }

        @Override
        public UUID getRootId() {
            return rootId;
        }

        @Override
        public UUID getId() {
            return id;
        }
    }
}
