"""
Methods to assist in loading dependencies for GitQuery in WORKSPACE files
"""

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load(
    "//bazel_support:constants.bzl",
    "RULES_JVM_EXTERNAL_SHA",
    "RULES_JVM_EXTERNAL_TAG",
    "RULES_KOTLIN_SHA",
    "RULES_KOTLIN_VERSION",
)

def _maybe(repo_rule, name, **kwargs):
    if not native.existing_rule(name):
        repo_rule(name = name, **kwargs)

def gitquery_dependencies(
        rules_jvm_external_tag = RULES_JVM_EXTERNAL_TAG,
        rules_jvm_external_sha = RULES_JVM_EXTERNAL_SHA,
        rules_kotlin_version = RULES_KOTLIN_VERSION,
        rules_kotlin_sha = RULES_KOTLIN_SHA):
    _maybe(
        http_archive,
        name = "io_bazel_rules_kotlin",
        urls = ["https://github.com/bazelbuild/rules_kotlin/releases/download/%s/rules_kotlin-%s.tar.gz" % (RULES_KOTLIN_VERSION, RULES_KOTLIN_VERSION)],
        sha256 = RULES_KOTLIN_SHA,
    )

    _maybe(
        http_archive,
        name = "rules_jvm_external",
        strip_prefix = "rules_jvm_external-%s" % rules_jvm_external_tag,
        sha256 = rules_jvm_external_sha,
        url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % rules_jvm_external_tag,
    )

    _maybe(
        http_archive,
        name = "rules_java",
        sha256 = "eb7db63ed826567b2ceb1ec53d6b729e01636f72c9f5dfb6d2dfe55ad69d1d2a",
        url = "https://github.com/bazelbuild/rules_java/releases/download/7.2.0/rules_java-7.2.0.tar.gz",
    )
