package(default_visibility = ["//visibility:public"])

load("@graknlabs_build_tools//distribution/maven:rules.bzl", "deploy_maven", "assemble_maven")

assemble_maven(
    name = "assemble-maven",
    target = "//client",
    package = "",
    workspace_refs = "@graknlabs_grabl_tracing_workspace_refs//:refs.json",
    project_name = "Grabl Tracing",
    project_description = "Grabl Tracing API and Client",
    project_url = "https://github.com/graknlabs/grabl-tracing",
    scm_url = "https://github.com/graknlabs/grabl-tracing",
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
)