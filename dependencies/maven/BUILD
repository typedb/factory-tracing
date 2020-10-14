
load("@graknlabs_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

exports_files(["artifacts.bzl"])

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "agpl",
    size = "small",
)
