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

import io.grpc.*;

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
