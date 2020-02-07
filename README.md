com.tinder.gitquery - GitQuery
==========

A core library, command line interface, and incremental Gradle plugin, that given a config file, syncs files 
from a git repo to a given directory. The git repo is cached to provide ultra fast subsequent/incremental builds. 

This enables a new approach for querying dependencies across repos as an alternative to the traditional  
pre-built and published artifacts. 

It is sometimes preferable to query individual files in a remote repo when:
 - we only need a small number of files from very large remote (mono)repo
 - we need granular versioning for each dependency file 
 - it is hard to globally version the files in the remote repo and it would require many artifacts to be published 
 - we want to avoid decoupling the building of the artifact and our module to avoid breaking changes
 - not all interested in the artifact will be using the same version dependencies as the artifact

#### Sample config.yaml

```yaml
# Describe a set of files to fetch from a given repository.
---
schema:
  version: 1
remote - a remote repository to fetch protobuf definitions from
remote: https://github.com/cdsap/ProtoExample.git
# branch - only this branch will be cloned/pulled, there the sha values below must be in 
# master
branch: master
# Indicate the sha (in remote/branch) for each proto file that we want to have models generated for.
# The structure of the definitions matches the directory structure of the remote repo.
definitions:
  # the root directory in the repo
  definitions:
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
To run
```shell script
./gitquery
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
