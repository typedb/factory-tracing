java_binary(
    name = "server",
    srcs = ["ExampleTracingServer.java"],
    deps = [
        "//protocol",
        "//dependencies/maven/artifacts/com/google/protobuf:protobuf-java",
        "//dependencies/maven/artifacts/io/grpc:grpc-core",
        "//dependencies/maven/artifacts/io/grpc:grpc-protobuf",
        "//dependencies/maven/artifacts/io/grpc:grpc-stub",
        "//dependencies/maven/artifacts/io/grpc:grpc-api",
    ],
    main_class = "grabl.tracing.test.TestTracingServer",
    runtime_deps = [
        "//dependencies/maven/artifacts/io/grpc:grpc-netty",
        "//dependencies/maven/artifacts/io/netty:netty-codec-http2",
        "//dependencies/maven/artifacts/io/netty:netty-handler",
        "//dependencies/maven/artifacts/io/netty:netty-tcnative-boringssl-static",
    ],
)

java_binary(
    name = "client",
    srcs = ["ExampleTracingClient.java"],
    classpath_resources = ["logback.xml"],
    deps = [
        "//client",
        "//dependencies/maven/artifacts/com/google/protobuf:protobuf-java",
        "//dependencies/maven/artifacts/io/grpc:grpc-core",
        "//dependencies/maven/artifacts/io/grpc:grpc-protobuf",
        "//dependencies/maven/artifacts/io/grpc:grpc-stub",
        "//dependencies/maven/artifacts/io/grpc:grpc-api",
        "//dependencies/maven/artifacts/org/slf4j:slf4j-api",
    ],
    main_class = "grabl.tracing.test.TestTracingClient",
    runtime_deps = [
        "//protocol",
        "//dependencies/maven/artifacts/io/grpc:grpc-netty",
        "//dependencies/maven/artifacts/io/netty:netty-codec-http2",
        "//dependencies/maven/artifacts/io/netty:netty-handler",
        "//dependencies/maven/artifacts/io/netty:netty-tcnative-boringssl-static",
        "//dependencies/maven/artifacts/ch/qos/logback:logback-classic",
    ],
)