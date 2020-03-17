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
        "//dependencies/maven/artifacts/io/grpc:grpc-testing",
        "//dependencies/maven/artifacts/io/grpc:grpc-api",
        "//dependencies/maven/artifacts/io/grpc:grpc-core",
        "//dependencies/maven/artifacts/io/grpc:grpc-stub",
        "//dependencies/maven/artifacts/com/google/protobuf:protobuf-java",
    ],
    runtime_deps = [
        "//dependencies/maven/artifacts/io/grpc:grpc-netty",
        "//dependencies/maven/artifacts/io/netty:netty-codec-http2",
        "//dependencies/maven/artifacts/io/netty:netty-handler",
        "//dependencies/maven/artifacts/io/netty:netty-tcnative-boringssl-static",
    ],
)