#
# GRAKN.AI - THE KNOWLEDGE GRAPH
# Copyright (C) 2020 Grakn Labs Ltd
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

workspace(name = "grabl_tracing")


################################
# Load Grakn Labs dependencies #
################################

load("//dependencies/graknlabs:dependencies.bzl", "graknlabs_dependencies")
graknlabs_dependencies()

load("@graknlabs_dependencies//distribution:deps.bzl", distribution_deps = "deps")
distribution_deps()

load("@graknlabs_dependencies//tools/unuseddeps:deps.bzl", unuseddeps_deps = "deps")
unuseddeps_deps()


###########################
# Load Bazel Dependencies #
###########################

load("@graknlabs_dependencies//builder/bazel:deps.bzl", "bazel_common", "bazel_deps", "bazel_toolchain")
bazel_common()
bazel_deps()
bazel_toolchain()


#################################
# Load Build Tools dependencies #
#################################

load("@graknlabs_dependencies//builder/python:deps.bzl", python_deps = "deps")
python_deps()

load("@rules_python//python:pip.bzl", "pip_repositories", "pip3_import")
pip_repositories()

pip3_import(
    name = "graknlabs_dependencies_ci_pip",
    requirements = "@graknlabs_dependencies//tools:requirements.txt",
)

load("@graknlabs_dependencies_ci_pip//:requirements.bzl",
graknlabs_dependencies_ci_pip_install = "pip_install")
graknlabs_dependencies_ci_pip_install()


###########################
# Load Local Dependencies #
###########################

load("//dependencies/maven:dependencies.bzl", "maven_dependencies")
maven_dependencies()


#######################################
# Load compiler dependencies for GRPC #
#######################################

load("@graknlabs_dependencies//builder/grpc:deps.bzl", grpc_deps = "deps")
grpc_deps()

load("@com_github_grpc_grpc//bazel:grpc_deps.bzl",
com_github_grpc_grpc_deps = "grpc_deps")
com_github_grpc_grpc_deps()

load("@stackb_rules_proto//java:deps.bzl", "java_grpc_compile")
java_grpc_compile()


##################################
# Load distribution dependencies #
##################################

load("@graknlabs_bazel_distribution//github:dependencies.bzl", "tcnksm_ghr")
tcnksm_ghr()

load("@graknlabs_bazel_distribution//common:dependencies.bzl", "bazelbuild_rules_pkg")
bazelbuild_rules_pkg()


#####################################
# Load Bazel common workspace rules #
#####################################

# TODO: Figure out why this cannot be loaded at earlier at the top of the file
load("@com_github_google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")
google_common_workspace_rules()

# Generate a JSON document of commit hashes of all external workspace dependencies
load("@graknlabs_bazel_distribution//common:rules.bzl", "workspace_refs")
workspace_refs(
    name = "graknlabs_grabl_tracing_workspace_refs"
)
