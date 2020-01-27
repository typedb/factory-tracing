java_binary(
    name = "grabl-tracing-server",
    srcs = ["TestTracingServer.java"],
    deps = [
        "//:grabl-tracing",
        "//protocol:protocol",
        "//dependencies/maven/artifacts/com/google/protobuf:protobuf-java",
        "//dependencies/maven/artifacts/io/grpc:grpc-core",
        "//dependencies/maven/artifacts/io/grpc:grpc-protobuf",
        "//dependencies/maven/artifacts/io/grpc:grpc-stub",
        "//dependencies/maven/artifacts/io/grpc:grpc-api",
    ],
    main_class = "grakn.grabl_tracing.test.TestTracingServer",
    runtime_deps = [
        "//:grabl-tracing",
        "//dependencies/maven/artifacts/io/grpc:grpc-netty",
    ],
)

java_binary(
    name = "grabl-tracing-client",
    srcs = ["TestTracingClient.java"],
    deps = [
        "//:grabl-tracing",
        "//protocol:protocol",
        "//dependencies/maven/artifacts/com/google/protobuf:protobuf-java",
        "//dependencies/maven/artifacts/io/grpc:grpc-core",
        "//dependencies/maven/artifacts/io/grpc:grpc-protobuf",
        "//dependencies/maven/artifacts/io/grpc:grpc-stub",
        "//dependencies/maven/artifacts/io/grpc:grpc-api",
    ],
    main_class = "grakn.grabl_tracing.test.TestTracingClient",
    runtime_deps = [
        "//:grabl-tracing",
        "//dependencies/maven/artifacts/io/grpc:grpc-netty",
    ],
)