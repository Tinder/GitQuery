load("//bazel_support:repositories.bzl", "gitquery_dependencies")
load("//bazel_support:constants.bzl", "MAVEN_ARTIFACTS")

gitquery_dependencies()

load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")
kotlin_repositories()

load("@io_bazel_rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")
# kt_register_toolchains()
register_toolchains("//:kotlin_toolchain")

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = MAVEN_ARTIFACTS,
    repositories = [
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)
