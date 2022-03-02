GitQuery
==========
![workflow](https://github.com/Tinder/GitQuery/actions/workflows/gradle.yml/badge.svg)

Fast and incremental query and sync of files in a remote git repository.

 - Core library (Kotlin)
 - Command line interface
 - Gradle plugin

An alternative approach for defining dependencies in a build project vs importing of pre-built and published artifacts.

#### Use cases:
It is sometimes preferable to query individual files in a remote repo when:
 - we only need a few files from large remote monorepo.
 - we need granular versioning for each dependency file.
 - it is hard to globally version the files in the remote repo, or it would require many artifacts to be published.
 - we want to combine the source files of an artifact into our module.
 - we want to avoid version conflicts as possible when including pre-build artifacts.

#### Sample gitquery.yml

```yaml
# Describe a set of files to fetch from a given repository.
---
schema:
  version: 1
# remote - The remote repository to query files from.
remote: https://github.com/aminghadersohi/ProtoExample.git
# branch - The single branch that will be cloned on first run and pulled incrementally on
# subsequent runs. The revision values used in [commits] and [files] must be available under [branch].
branch: master
# A list of commit aliases that can be used in the [files] section.
commits:
  latest: d654b510d2689e8ee56d23d03dff2be742737f86
# Specify a nested map of filenames to revisions (or commit alias) that we want to sync to [outputDir].
# The structure of [files] matches the directory structure of the remote repo. A key whose value is a
# nested map is considered a directory.
files:
  # A file at the root fo the remote repo.
  README.md: latest
  # A directory at the root of the remote repo.
  definitions:
    # A file inside the definitions folder.
    user.proto: 42933446d0321958e8c12216d04b9f0c382ebf1b
# A directory to sync the queried files into. default: gitquery-output
outputDir: gitquery-output
# A directory to hold the intermediate cloned git repo. default: /tmp/gitquery/repo
repoDir: /tmp/.gitquery
# If true [default], cleans out the output folder prior to running sync.
cleanOutput: true
# A free form map of String -> Any - enabling self contained integration with various systems.
extra:
  attr1: value1
  attr2: 1
#
```

#### Gradle Plugin - (https://plugins.gradle.org/plugin/com.tinder.gitquery)
`module/build.gradle:`
```groovy
plugins {
    id "com.tinder.gitquery" version "3.0.12"
}

gitQuery {
    autoSync = true
    cleanOutput = true
    configFile = "gitquery.yml"
    outputDir = protoDir
    repoDir = "${project.buildDir}/tmp/.gitquery"
}

// This will init/update the config file every time the gitQuery task runs.
gitQueryInit {
    flatFiles = false
    includeGlobs = listOf("**/examples/addressbook.proto")
    excludeGlobs = listOf("**/generated/**/*.proto")
    revision = "v3.14.0"
}
```

This adds a task called `gitQuery` to the module. It can be executed like so:

```terminal
./gradlew :module:gitQuery
```

#### Bazel

Add the needed dependencies to your `WORKSPACE` file:

```WORKSPACE
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
  name = "GitQuery",
  url = "RELEASE_URL",
  sha256 = "UPDATE_ME"
)

load("@GitQuery//bazel_support:repositories.bzl", "gitquery_dependencies")
load("@GitQuery//bazel_support:constants.bzl", "MAVEN_ARTIFACTS")

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
```

Then run

```terminal
bazel run @GitQuery//:GitQuery -- -h
```

to get started.

#### Command line interface
Install using brew (https://github.com/Tinder/homebrew-tap):
```
brew tap Tinder/tap
brew install gitquery
```
or
```
brew install Tinder/tap/gitquery
```

If you need Java 1.8:
```
brew cask install homebrew/cask-versions/adoptopenjdk8
```

To install from source:
```shell script
./install
```

To run
```shell script
gitquery
```

#### Sample Sync
```shell script
./gitquery --config-file=./samples/sample1.yml --repo-dir=./build/tmp/repo --output-dir=./gitquery-output
```

#### Sample Init - create config that doesn't exist, by default a nested structure generated.
```shell script
./gitquery --init-config \
           --config-file samples/sample1-generate-nested.yml \
           --include-globs "**/src/google/protobuf/**/*.proto,**/benchmarks/**/*.proto,README.md,benchmarks/Makefile.am" \
           --exclude-globs "**/ruby/**,**/proto2/**" \
           --remote git@github.com:protocolbuffers/protobuf.git \
           --branch master \
           --revision v3.14.0
```

#### Sample Update - by default a nested structure generated.
```shell script
./gitquery --init-config \
           --config-file samples/sample2-generate-nested.yml \
           --include-globs "**/src/google/protobuf/**/*.proto,**/benchmarks/**/*.proto,README.md" \
           --exclude-globs "**/ruby/**,**/proto2/**" \
           --revision v3.14.0
```

#### Sample Update - flat file structure generated using --flat-files.
```shell script
./gitquery --init-config \
           --config-file samples/sample2-generate-flat.yml --flat-files \
           --include-globs "**/src/google/protobuf/**/*.proto,**/benchmarks/**/*.proto,README.md" \
           --exclude-globs "**/ruby/**,**/proto2/**"\
           --revision v3.14.0
```

```shell script
./gitquery --help

Usage: git-query-cli [OPTIONS]

Options:
  --version             Show the version and exit
  --config-file TEXT    A yaml file that describe a set of files to query and
                        sync from a given repository. default: gitquery.yml
  --remote TEXT         Remote Git repo url. If provided, this will override
                        any value specified for [remote] in the [configFile].
                        default:
  --branch TEXT         Remote Git repo branch. If provided, this will
                        override any value specified for [branch] in the
                        [configFile]. default: master
  --output-dir TEXT     Path to a directory where the files should be synced
                        to. If provided, this will override any value
                        specified for [outputDir] in the [configFile].
                        default: gitquery-output
  --repo-dir TEXT       Where the remote repo(s) can be cloned locally and
                        stored. If provided, this will override any value
                        specified for [repoDir] in the [configFile]. default:
                        /tmp/gitquery/repo
  --dont-clean-output   Whether to not clean the output folder prior to sync.
                        If set, this will override the [cleanOutput] in the
                        [configFile] default: false
  --init-config         Initialize/update the config file based on command
                        line params. Use --include-globs and --exclude-globs.
                        If [configFile] exists, it will be updated, else it
                        will be created with values from command line or
                        internal defaults. default: false
  --include-globs TEXT  A list of globs to include when generating/updating
                        the files attribute in [configFile]. If provided, this
                        comma, space or pipe separated list of globs will
                        override [includeGlobs] in [configFile] and used when
                        initializing/updating the config's [files] map.
  --exclude-globs TEXT  A list of globs to exclude when generating/updating
                        the files attribute in [configFile]. If provided, this
                        comma, space or pipe separated list of globs will
                        override [excludeGlobs] in [configFile] and used to
                        exclude patterns when initializing/updating the
                        config's [files] map.
  --flat-files          When --generate-globs is used, this option helps
                        choose if the files in the generated config file
                        should be in a flat map or a nest map. default: false
  --revision TEXT       A revision to use when --init-config is used, if not
                        provided the revision of latest [branch] is used
  --verbose             Show the underlying commands and their outputs in the
                        console. default: false
  -h, --help            Show this message and exit
```

#### Example Use Case - Code Generation
We have a tech stack with many mobile/web apps and services with many developers actively working on them. We want to avoid duplicating and handwriting our models and service interfaces, so we decide to store definitions of models and service interfaces in a central idl repo.

We can choose to build a single artifact for each language that we support in our tech stack or even build many of them. However, since each of our apps or services just needs a couple of files from the idl repo. We can avoid including unneccesary files, in pre-built artifacts, by using GitQuery to pull and sync the idl files that we want. This allows them to be staged for our build. From there we can use our code generation tools, the sames ones we were using to generate code and build them in to an artifact, to generate code from the idl definitions that we care about and use the generated code in our project as any other source code.

License
---
~~~
Copyright (c) 2021, Match Group, LLC
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Match Group, LLC nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL MATCH GROUP, LLC BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
