package grabl.tracing.client;

import io.grpc.ManagedChannelBuilder;

/**
 * The primary factory to obtain instances of GrablTracing.
 */
public class GrablTracingFactory {

    /**
     * Connect to the Grabl tracing server with TLS and providing
     *
     * @param grablUri The URI of your Grabl server.
     * @param username Your username on the Grabl server.
     * @param apiToken Your API token for the username.
     * @return A GrablTracing instance that has securely connected to your Grabl server.
     */
    public static GrablTracing secureTracing(String grablUri, String username, String apiToken) {
        return new GrablTracingStandard(
                ManagedChannelBuilder.forTarget(grablUri)
                .useTransportSecurity()
                .intercept(new GrablTokenAuthClientInterceptor(username, apiToken))
                .build()
        );
    }

    /**
     * A plaintext variation of Grabl tracing, useful for testing the tracing protocol but should not be used in real
     * applications.
     *
     * @param grablUri The URI of your test tracing server.
     * @return A GrablTracing instance that has connected to your server without any authentication.
     */
    public static GrablTracing unauthenticatedTracing(String grablUri) {
        return new GrablTracingStandard(
                ManagedChannelBuilder.forTarget(grablUri)
                .usePlaintext()
                .build()
        );
    }

    /**
     * Get a GrablTracing that can be used to safely run tracing-enabled applications with no connection and no
     * associated overhead.
     *
     * @return a GrablTracing that does nothing.
     */
    public static GrablTracing noopTracing() {
        return GrablTracingNoOp.getInstance();
    }

    /**
     * Decorate a GrablTracing with Slf4j logging.
     *
     * @param inner The actual GrablTracing that underlies this implementation
     * @return
     */
    public static GrablTracing withSlf4jLogging(GrablTracing inner) {
        return new GrablTracingSlf4j(inner);
    }
}
