load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "core",
    srcs = glob(["src/main/**/*.kt"], allow_empty = False),
    deps = [
        "@maven//:org_yaml_snakeyaml"
    ],
    module_name = "core",
    visibility = ["//visibility:public"]
)
