java_test(
    name = "test",
    srcs = ["GrablTracingClientTest.java"],
    test_class = "grabl.tracing.client.test.GrablTracingClientTest",
    deps = [
        "//client",
        "//protocol",
        "//dependencies/maven/artifacts/junit",
        "//dependencies/maven/artifacts/org/mockito:mockito-core",
        "//dependencies/maven/artifacts/org/hamcrest",
        "@maven//:io_grpc_grpc_testing",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_stub",
        "@maven//:com_google_protobuf_protobuf_java",
    ],
    runtime_deps = [
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_netty_netty_codec_http2",
        "@maven//:io_netty_netty_handler",
        "@maven//:io_netty_netty_tcnative_boringssl_static",
    ],
)