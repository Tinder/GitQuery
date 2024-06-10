load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("//bazel_support:repositories.bzl", "gitquery_dependencies")
load("//bazel_support:constants.bzl", "MAVEN_ARTIFACTS")

gitquery_dependencies()

load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")
kotlin_repositories()

load("@io_bazel_rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")
kt_register_toolchains()

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = MAVEN_ARTIFACTS,
    repositories = [
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)

http_archive(
    name = "rules_java",
    urls = [
        "https://github.com/bazelbuild/rules_java/releases/download/7.6.1/rules_java-7.6.1.tar.gz",
    ],
    sha256 = "f8ae9ed3887df02f40de9f4f7ac3873e6dd7a471f9cddf63952538b94b59aeb3",
)
load("@rules_java//java:repositories.bzl", "rules_java_dependencies", "rules_java_toolchains")
rules_java_dependencies()
rules_java_toolchains()
