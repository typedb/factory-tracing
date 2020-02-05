java_binary(
    name = "grabl-tracing-server",
    srcs = ["TestTracingServer.java"],
    deps = [
        "//client:grabl-tracing",
        "//protocol:protocol",
        "//util:util",
        "//dependencies/maven/artifacts/com/google/protobuf:protobuf-java",
        "//dependencies/maven/artifacts/io/grpc:grpc-core",
        "//dependencies/maven/artifacts/io/grpc:grpc-protobuf",
        "//dependencies/maven/artifacts/io/grpc:grpc-stub",
        "//dependencies/maven/artifacts/io/grpc:grpc-api",
    ],
    main_class = "grabl.tracing.test.TestTracingServer",
    runtime_deps = [
        "//client:grabl-tracing",
        "//util:util",
        "//dependencies/maven/artifacts/io/grpc:grpc-netty",
        "//dependencies/maven/artifacts/io/netty:netty-codec-http2",
        "//dependencies/maven/artifacts/io/netty:netty-handler",
        "//dependencies/maven/artifacts/io/netty:netty-tcnative-boringssl-static",
    ],
)

java_binary(
    name = "grabl-tracing-client",
    srcs = ["TestTracingClient.java"],
    deps = [
        "//client:grabl-tracing",
        "//protocol:protocol",
        "//util:util",
        "//dependencies/maven/artifacts/com/google/protobuf:protobuf-java",
        "//dependencies/maven/artifacts/io/grpc:grpc-core",
        "//dependencies/maven/artifacts/io/grpc:grpc-protobuf",
        "//dependencies/maven/artifacts/io/grpc:grpc-stub",
        "//dependencies/maven/artifacts/io/grpc:grpc-api",
    ],
    main_class = "grabl.tracing.test.TestTracingClient",
    runtime_deps = [
        "//client:grabl-tracing",
        "//util:util",
        "//dependencies/maven/artifacts/io/grpc:grpc-netty",
        "//dependencies/maven/artifacts/io/netty:netty-codec-http2",
        "//dependencies/maven/artifacts/io/netty:netty-handler",
        "//dependencies/maven/artifacts/io/netty:netty-tcnative-boringssl-static",
    ],
)