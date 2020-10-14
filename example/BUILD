
load("@graknlabs_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_binary(
    name = "server",
    srcs = ["ExampleTracingServer.java"],
    deps = [
        "//protocol",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
        "@maven//:io_grpc_grpc_api",
    ],
    main_class = "grabl.tracing.example.ExampleTracingServer",
    runtime_deps = [
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_netty_netty_codec_http2",
        "@maven//:io_netty_netty_handler",
        "@maven//:io_netty_netty_tcnative_boringssl_static",
    ],
)

java_binary(
    name = "client",
    srcs = ["ExampleTracingClient.java"],
    classpath_resources = ["logback.xml"],
    deps = [
        "//client",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
        "@maven//:io_grpc_grpc_api",
        "@maven//:org_slf4j_slf4j_api",
    ],
    main_class = "grabl.tracing.example.ExampleTracingClient",
    runtime_deps = [
        "//protocol",
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_netty_netty_codec_http2",
        "@maven//:io_netty_netty_handler",
        "@maven//:io_netty_netty_tcnative_boringssl_static",
        "@maven//:ch_qos_logback_logback_classic",
    ],
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "agpl",
    size = "small",
)
