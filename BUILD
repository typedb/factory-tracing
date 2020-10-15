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

load("@graknlabs_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@graknlabs_bazel_distribution//github:rules.bzl", "deploy_github")
load("//:deployment.bzl", "deployment")

exports_files(["VERSION", "RELEASE_TEMPLATE.md"])

checkstyle_test(
    name = "checkstyle",
    include = glob([
        "*",
        ".grabl/automation.yml",
        "test/deployment/pom.xml",
        "test/deployment/src/test/**/*",
    ]),
    license_type = "apache",
    size = "small",
)

deploy_github(
    name = "deploy-github",
    release_description = "//:RELEASE_TEMPLATE.md",
    title = "Grabl Tracing API and Client",
    title_append_version = True,
    organisation = deployment["github.organisation"],
    repository = deployment["github.repository"]
)

# CI targets that are not declared in any BUILD file, but are called externally
filegroup(
    name = "ci",
    data = [
        "@graknlabs_dependencies//library/maven:update",
        "@graknlabs_dependencies//tool/bazelrun:rbe",
        "@graknlabs_dependencies//tool/release:approval",
        "@graknlabs_dependencies//tool/release:create-notes",
        "@graknlabs_dependencies//tool/unuseddeps:unused-deps",
        "@graknlabs_dependencies//tool/sync:dependencies",
    ]
)
