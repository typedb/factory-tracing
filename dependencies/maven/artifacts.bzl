artifacts = [
    "ch.qos.logback:logback-classic",
    "com.google.guava:guava",
    "com.google.protobuf:protobuf-java",
    "io.grpc:grpc-api",
    "io.grpc:grpc-core",
    "io.grpc:grpc-protobuf",
    "io.grpc:grpc-stub",
    "io.grpc:grpc-netty",
    "io.grpc:grpc-testing",
    "io.netty:netty-codec-http2",
    "io.netty:netty-handler",
    "io.netty:netty-tcnative-boringssl-static",
    "junit:junit",
    "org.hamcrest:hamcrest",
    "org.mockito:mockito-core",
    "org.slf4j:slf4j-api"
]

# Override libraries conflicting with versions defined in @graknlabs_dependencies
artifacts_repo = {
    "io.netty:netty-all": "4.1.38.Final",
    "io.netty:netty-codec-http2": "4.1.38.Final",
    "io.netty:netty-handler": "4.1.38.Final",
    "io.netty:netty-handler-proxy": "4.1.38.Final",
    "io.netty:netty-buffer": "4.1.38.Final",
    "io.netty:netty-codec": "4.1.38.Final",
    "io.netty:netty-codec-http": "4.1.38.Final",
    "io.netty:netty-codec-socks": "4.1.38.Final",
    "io.netty:netty-common": "4.1.38.Final",
    "io.netty:netty-transport": "4.1.38.Final",
    "io.netty:netty-resolver": "4.1.38.Final",
}
