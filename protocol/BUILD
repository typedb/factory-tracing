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
load("@graknlabs_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@graknlabs_dependencies//library/maven:artifacts.bzl", "maven_overrides", maven_overrides_org = "artifacts")
load("//dependencies/maven:artifacts.bzl", maven_overrides_repo = "overrides")

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
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
        "@maven//:io_grpc_grpc_api",
    ],
    tags = ["maven_coordinates=io.grabl.tracing:grabl-tracing-protocol:{pom_version}"],
)

assemble_maven(
    name = "assemble-maven",
    target = "//protocol",
    package = "",
    workspace_refs = "@graknlabs_grabl_tracing_workspace_refs//:refs.json",
    version_overrides = maven_overrides(maven_overrides_org, maven_overrides_repo),
    project_name = "Grabl Tracing Protocol",
    project_description = "Grabl Tracing Protocol",
    project_url = "https://github.com/graknlabs/grabl-tracing",
    scm_url = "https://github.com/graknlabs/grabl-tracing",
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
    deployment_properties = "@graknlabs_dependencies//distribution:deployment.properties",
)
