load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_binary")

java_library(
    name = "clikt",
    exports = [
        "@maven//:com_github_ajalt_clikt_clikt_jvm",
    ],
)

kt_jvm_binary(
    name = "GitQuery",
    deps = [
        "//core:core",
        ":clikt",
    ],
    visibility = ["//visibility:public"],
    main_class = "com.tinder.gitquery.cli.GitQueryCliKt",
    srcs = [
        "cli/src/main/kotlin/com/tinder/gitquery/cli/GitQueryCli.kt",
    ],
)
