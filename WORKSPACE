load("//bazel_support:repositories.bzl", "gitquery_dependencies")
load("//bazel_support:constants.bzl", "MAVEN_ARTIFACTS")

gitquery_dependencies()

load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kotlin_repositories", "kt_register_toolchains")
kotlin_repositories() # if you want the default. Otherwise see custom kotlinc distribution below
kt_register_toolchains() # to use the default toolchain, otherwise see toolchains below

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = MAVEN_ARTIFACTS,
    repositories = [
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)
