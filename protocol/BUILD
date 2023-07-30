#
# Copyright (C) 2022 Vaticle
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
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_dependencies//distribution:deployment.bzl", "deployment")
load("@vaticle_dependencies//distribution/maven:version.bzl", "version")
load("@vaticle_dependencies//library/maven:artifacts.bzl", artifacts_org = "artifacts")
load("//dependencies/maven:artifacts.bzl", "artifacts_repo")
load("@vaticle_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@rules_proto_grpc//java:defs.bzl", "java_grpc_library")

proto_library(
    name = "tracing-proto",
    srcs = ["tracing.proto"],
)

java_grpc_library(
    name = "protocol",
    protos = [
        ":tracing-proto",
    ],
    tags = ["maven_coordinates=com.vaticle.factory:factory-tracing-protocol:{pom_version}"],
)

checkstyle_test(
    name = "checkstyle",
    include = glob([
        "*",
        "util/*",
    ]),
    license_type = "apache-header",
    size = "small",
)

assemble_maven(
    name = "assemble-maven",
    target = "//protocol",
    workspace_refs = "@vaticle_factory_tracing_workspace_refs//:refs.json",
    version_overrides = version(artifacts_org, artifacts_repo),
    project_name = "Vaticle Factory Tracing Protocol",
    project_description = "Vaticle Factory Tracing Protocol",
    project_url = "https://github.com/vaticle/factory-tracing",
    scm_url = "https://github.com/vaticle/factory-tracing",
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
    snapshot = deployment["maven.snapshot"],
    release = deployment["maven.release"]
)
