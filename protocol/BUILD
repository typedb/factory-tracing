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
load("@stackb_rules_proto//java:java_grpc_compile.bzl", "java_grpc_compile")
load("@graknlabs_dependencies//distribution/maven:rules.bzl", "assemble_maven", "deploy_maven")

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
    srcs = [
        ":protocol-src",
        "util/ProtobufUUIDUtil.java",
    ],
    deps = [
        "//dependencies/maven/artifacts/com/google/guava",
        "//dependencies/maven/artifacts/com/google/protobuf:protobuf-java",
        "//dependencies/maven/artifacts/io/grpc:grpc-core",
        "//dependencies/maven/artifacts/io/grpc:grpc-protobuf",
        "//dependencies/maven/artifacts/io/grpc:grpc-stub",
        "//dependencies/maven/artifacts/io/grpc:grpc-api",
    ],
    tags = ["maven_coordinates=io.grabl.tracing:grabl-tracing-protocol:{pom_version}"],
)

assemble_maven(
    name = "assemble-maven",
    target = "//protocol",
    package = "",
    workspace_refs = "@graknlabs_grabl_tracing_workspace_refs//:refs.json",
    project_name = "Grabl Tracing Protocol",
    project_description = "Grabl Tracing Protocol",
    project_url = "https://github.com/graknlabs/grabl-tracing",
    scm_url = "https://github.com/graknlabs/grabl-tracing",
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
)