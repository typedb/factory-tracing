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

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

/**
 * A GRPC {@link ClientInterceptor} that adds the username and api-token to the metadata for every rpc so that the
 * grabl tracing server can authenticate us.
 */
class GrablTokenAuthClientInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> USERNAME = Metadata.Key.of("username", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> API_TOKEN = Metadata.Key.of("api-token", Metadata.ASCII_STRING_MARSHALLER);

    private final String username;
    private final String apiToken;

    GrablTokenAuthClientInterceptor(String username, String apiToken) {
        this.username = username;
        this.apiToken = apiToken;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> listener, Metadata metadata) {
                metadata.put(USERNAME, username);
                metadata.put(API_TOKEN, apiToken);
                super.start(listener, metadata);
            }
        };
    }
}
