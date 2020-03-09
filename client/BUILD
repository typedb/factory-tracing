java_library(
    name = "client",
    srcs = glob(["*.java"]),
    deps = [
        "//protocol:protocol",
        "//util:util",
        "//dependencies/maven/artifacts/com/google/protobuf:protobuf-java",
        "//dependencies/maven/artifacts/io/grpc:grpc-core",
        "//dependencies/maven/artifacts/io/grpc:grpc-protobuf",
        "//dependencies/maven/artifacts/io/grpc:grpc-stub",
        "//dependencies/maven/artifacts/io/grpc:grpc-api",
        "//dependencies/maven/artifacts/org/slf4j:slf4j-api",
    ],
    runtime_deps = [
        "//dependencies/maven/artifacts/io/grpc:grpc-netty",
        "//dependencies/maven/artifacts/io/netty:netty-codec-http2",
        "//dependencies/maven/artifacts/io/netty:netty-handler",
        "//dependencies/maven/artifacts/io/netty:netty-tcnative-boringssl-static",
    ],
    visibility = ["//visibility:public"],
    tags = ["maven_coordinates=io.grabl.tracing:grabl-tracing-client:{pom_version}"],
)
