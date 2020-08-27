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

load("@graknlabs_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@graknlabs_dependencies//distribution:deployment.bzl", "deployment")
load("@graknlabs_dependencies//distribution/maven:version.bzl", "version")
load("@graknlabs_dependencies//library/maven:artifacts.bzl", artifacts_org = "artifacts")
load("//dependencies/maven:artifacts.bzl", "artifacts_repo")
load("@graknlabs_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")

java_library(
    name = "client",
    srcs = glob(["*.java"]),
    deps = [
        "//protocol:protocol",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:io_grpc_grpc_core",
        "@maven//:io_grpc_grpc_protobuf",
        "@maven//:io_grpc_grpc_stub",
        "@maven//:io_grpc_grpc_api",
        "@maven//:org_slf4j_slf4j_api",
    ],
    runtime_deps = [
        "@maven//:io_grpc_grpc_netty",
        "@maven//:io_netty_netty_codec_http2",
        "@maven//:io_netty_netty_handler",
        "@maven//:io_netty_netty_tcnative_boringssl_static",
    ],
    tags = ["maven_coordinates=io.grabl.tracing:grabl-tracing-client:{pom_version}"],
)

assemble_maven(
    name = "assemble-maven",
    target = "//client",
    package = "",
    workspace_refs = "@graknlabs_grabl_tracing_workspace_refs//:refs.json",
    version_overrides = version(artifacts_org, artifacts_repo),
    project_name = "Grabl Tracing Client",
    project_description = "Grabl Tracing API and Client",
    project_url = "https://github.com/graknlabs/grabl-tracing",
    scm_url = "https://github.com/graknlabs/grabl-tracing",
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
    snapshot = deployment["maven.snapshot"],
    release = deployment["maven.release"],
)
