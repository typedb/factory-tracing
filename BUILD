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
exports_files(["VERSION"], visibility = ["//visibility:public"])

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_bazel_distribution//github:rules.bzl", "deploy_github")
load("//:deployment.bzl", "deployment")

exports_files(["VERSION", "RELEASE_TEMPLATE.md"])

checkstyle_test(
    name = "checkstyle",
    include = glob([
        ".bazelrc",
        ".gitignore",
        ".factory/automation.yml",
        "BUILD",
        "WORKSPACE",
        "deployment.bzl",
        "test/deployment/pom.xml",
        "test/deployment/src/test/**/*",
    ]),
    license_type = "apache-header",
    size = "small",
)

checkstyle_test(
    name = "checkstyle-license",
    include = ["LICENSE"],
    license_type = "apache-fulltext",
    size = "small",
)

deploy_github(
    name = "deploy-github",
    release_description = "//:RELEASE_TEMPLATE.md",
    title = "Vaticle Factory Tracing API and Client",
    title_append_version = True,
    organisation = deployment["github.organisation"],
    repository = deployment["github.repository"],
    draft = False
)

# CI targets that are not declared in any BUILD file, but are called externally
filegroup(
    name = "ci",
    data = [
        "@vaticle_dependencies//library/maven:update",
        "@vaticle_dependencies//tool/bazelrun:rbe",
        "@vaticle_dependencies//tool/release/notes:create",
        "@vaticle_dependencies//tool/unuseddeps:unused-deps",
    ]
)
