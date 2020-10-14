#
# Copyright (C) 2020 Grakn Labs
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

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
