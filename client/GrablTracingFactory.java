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
     * @return An instance that has securely connected to your Grabl server.
     */
    public static GrablTracing tracing(String grablUri, String username, String apiToken) {
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
     * @return An instance that has connected to your server without any authentication.
     */
    public static GrablTracing tracing(String grablUri) {
        return new GrablTracingStandard(
                ManagedChannelBuilder.forTarget(grablUri)
                .usePlaintext()
                .build()
        );
    }

    /**
     * Get a GrablTracing that can be used to safely run tracing-enabled applications with no connection and minimal
     * associated overhead.
     *
     * @return an instance that does nothing but adheres to the {@link GrablTracing} contract sufficiently to work with
     *      any code that does tracing.
     */
    public static GrablTracing noOpTracing() {
        return GrablTracingNoOp.getInstance();
    }

    /**
     * Decorate a GrablTracing with Slf4j logging (if the logging is enabled to the correct level).
     *
     * @param inner The actual GrablTracing that underlies this implementation.
     * @return If logging is enabled, a wrapped instance that logs to Slf4j, otherwise the instance {@param inner}.
     */
    public static GrablTracing withLogging(GrablTracing inner) {
        return GrablTracingSlf4j.wrapIfLoggingEnabled(inner);
    }
}
