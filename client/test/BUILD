#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

load("@graknlabs_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_test(
    name = "test",
    srcs = ["GrablTracingClientTest.java"],
    test_class = "grabl.tracing.client.test.GrablTracingClientTest",
    deps = [
        "//client",
        "//protocol",
        "@maven//:junit_junit",
        "@maven//:org_mockito_mockito_core",
        "@maven//:org_hamcrest_hamcrest",
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

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "apache",
    size = "small",
)
