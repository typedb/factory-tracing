package(default_visibility = ["//visibility:public"])
load("@stackb_rules_proto//java:java_grpc_compile.bzl", "java_grpc_compile")
load("@graknlabs_build_tools//distribution/maven:rules.bzl", "assemble_maven", "deploy_maven")

proto_library(
    name = "tracing-proto",
    srcs = ["tracing.proto"],
)

java_grpc_compile(
    name = "protocol-src",
    deps = [
        ":tracing-proto",
    ]
)

java_library(
    name = "protocol",
    srcs = [":protocol-src"],
    deps = [
        "//dependencies/maven/artifacts/com/google/guava",
        "//dependencies/maven/artifacts/com/google/protobuf:protobuf-java",
        "//dependencies/maven/artifacts/io/grpc:grpc-core",
        "//dependencies/maven/artifacts/io/grpc:grpc-protobuf",
        "//dependencies/maven/artifacts/io/grpc:grpc-stub",
        "//dependencies/maven/artifacts/io/grpc:grpc-api",
    ],
)