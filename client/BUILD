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

package(default_visibility = ["//visibility:public"])

load("@graknlabs_build_tools//distribution/maven:rules.bzl", "deploy_maven", "assemble_maven")

java_library(
    name = "client",
    srcs = glob(["*.java"]),
    deps = [
        "//protocol:protocol",
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
    tags = ["maven_coordinates=io.grabl.tracing:grabl-tracing-client:{pom_version}"],
)

assemble_maven(
    name = "assemble-maven",
    target = "//client",
    package = "",
    workspace_refs = "@graknlabs_grabl_tracing_workspace_refs//:refs.json",
    project_name = "Grabl Tracing Client",
    project_description = "Grabl Tracing API and Client",
    project_url = "https://github.com/graknlabs/grabl-tracing",
    scm_url = "https://github.com/graknlabs/grabl-tracing",
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
)