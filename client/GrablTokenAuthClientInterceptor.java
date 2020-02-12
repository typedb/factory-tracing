package grabl.tracing.client;

import io.grpc.*;

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
