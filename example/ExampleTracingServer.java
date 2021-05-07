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

package com.vaticle.factory.tracing.example;

import com.vaticle.factory.tracing.protocol.TracingProto.Analysis;
import com.vaticle.factory.tracing.protocol.TracingProto.Trace;
import com.vaticle.factory.tracing.protocol.TracingServiceGrpc.TracingServiceImplBase;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

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
