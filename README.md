com.tinder.gitquery - GitQuery
==========

Fast and incremental query and sync of files in a remote git repository.

 - Core library (Kotlin)
 - Command line interface
 - Gradle plugin

An alternative approach for defining dependencies in a build project vs importing of pre-built and published artifacts. 

#### Use cases: 
It is sometimes preferable to query individual files in a remote repo when:
 - we only need a small number of files from (very) large remote (mono)repo.
 - we need granular versioning for each dependency file.
 - it is hard to globally version the files in the remote repo and it would require many artifacts to be published .
 - we want to combine the source files of an artifact into our module.
 - we want to avoid version conflicts as possible when including pre-build artifacts. 

#### Sample config.yaml

```yaml
schema:
  version: 1
# remote - the remote repository to query files from.
remote: https://github.com/aminghadersohi/ProtoExample.git
# branch - only this branch will be cloned/pulled, there the sha values below must be in master.
branch: master
# a list of commit aliases that can be used in the `files` section.
commits:
  latest: d654b510d2689e8ee56d23d03dff2be742737f86
# Indicate the sha (in remote/branch) for each proto file that we want to have models generated for.
# The structure of `files` matches the directory structure of the remote repo.
files:
  # a file at the root fo the remote repo.
  README.md: latest
  # a directory at the root of the remote repo.
  definitions:
    # a file inside the definitions folder.
    user.proto: 42933446d0321958e8c12216d04b9f0c382ebf1b
```

#### Sample module/build.gradle:

```groovy
plugins {
  id 'com.tinder.gitquery'
}

gitQuery {
    configFile =  "config.yml"
    outputDir =  "synced-src"
    repoDir = "remote"
}
```

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

#### Sample CLI
```shell script
./gitquery --config-file=./samples/sample1.yaml --repo-dir=./build/tmp/repo --output-dir=./synced-src
```

```shell script
./gitquery --help   
                                                               
Usage: cli [OPTIONS]

Options:
  --config-file TEXT  a yaml file that describe a set of files to fetch/sync
                      from a given repository
  --output-dir TEXT   path to a directory where the files should be synced to
  --repo-dir TEXT     where the remote repo(s) can be cloned locally and
                      stored
  -h, --help          Show this message and exit
```

#### Example Use Case - Code Generation
We have a tech stack with many mobile/web apps and services with many developers actively working on them. We want to avoid duplicating and handwriting our models and service interfaces, so we decide to store definitions of models and service interfaces in a central idl repo. 

We can choose to build a single artifact for each language that we support in our tech stack or even build many of them. However, since each of our apps or services just needs a couple of files from the idl repo. We can avoid including unneccesary files, in pre-built artifacts, by using GitQuery to pull and sync the idl files that we want. This allows them to be staged for our build. From there we can use our code generation tools, the sames ones we were using to generate code and build them in to an artifact, to generate code from the idl definitions that we care about and use the generated code in our project as any other source code.

License
---
~~~
Copyright (c) 2019, Match Group, LLC
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
