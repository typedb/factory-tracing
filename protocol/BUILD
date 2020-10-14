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

package(default_visibility = ["//visibility:public"])
load("@stackb_rules_proto//java:java_grpc_compile.bzl", "java_grpc_compile")
load("@graknlabs_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@graknlabs_dependencies//distribution:deployment.bzl", "deployment")
load("@graknlabs_dependencies//distribution/maven:version.bzl", "version")
load("@graknlabs_dependencies//library/maven:artifacts.bzl", artifacts_org = "artifacts")
load("//dependencies/maven:artifacts.bzl", "artifacts_repo")
load("@graknlabs_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")

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
    tags = ["maven_coordinates=io.grabl:grabl-tracing-protocol:{pom_version}"],
)

checkstyle_test(
    name = "checkstyle",
    include = glob([
        "*",
        "util/*",
    ]),
    license_type = "agpl",
    size = "small",
)

assemble_maven(
    name = "assemble-maven",
    target = "//protocol",
    package = "",
    workspace_refs = "@graknlabs_grabl_tracing_workspace_refs//:refs.json",
    version_overrides = version(artifacts_org, artifacts_repo),
    project_name = "Grabl Tracing Protocol",
    project_description = "Grabl Tracing Protocol",
    project_url = "https://github.com/graknlabs/grabl-tracing",
    scm_url = "https://github.com/graknlabs/grabl-tracing",
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
    snapshot = deployment["maven.snapshot"],
    release = deployment["maven.release"]
)
