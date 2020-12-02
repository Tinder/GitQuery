## Change Log
All notable changes to this library will be documented in this file.

Version in this change log adheres to [Semantic Versioning](http://semver.org/).

### 3.0.2 (2020-12-01) [#48](https://github.com/Tinder/GitQuery/pull/48) Bug Fix - Snakeyaml constructor call ([tinder-aminghadersohi](https://github.com/tinder-aminghadersohi)
    - Upgrade to Snakeyaml 1.27 
    - Simplify constructor call for Yaml to avoid this error: `org.yaml.snakeyaml.representer.Representer.<init>(Lorg/yaml/snakeyaml/DumperOptions;)V`
    
### 3.0.1 (2020-12-01) [#48](https://github.com/Tinder/GitQuery/pull/48) Bug Fix - init config ([tinder-aminghadersohi](https://github.com/tinder-aminghadersohi)
    - Fix a bug related to the gradle plugin init config writing absolute path where relative is desired. 
    
### 3.0.0 (2020-11-14) [#45](https://github.com/Tinder/GitQuery/pull/45) GitQuery 3 ([tinder-aminghadersohi](https://github.com/tinder-aminghadersohi)
    - New `init-config` command line parameter
    - New Gradle extension `gitQueryInit` 
    - Supprt for tags as well as already supported shas
    - `autoSync` option for `gitQuery` task
    - New `version` command line parameter that prints the version of GitQuery
    - Added Detekt
    - Added KtLint
    - Added pre-commit git hook and Gradle task `installGitHooks` for installing it
    - Updated dependencies
    - Updated samples 
    - Updated circleci to use cache, run lint and build samples
    - Updated README.md
    - Unit tests
    - Lots of code cleanup


### 2.0.1 (2020-07-06) [#43](https://github.com/Tinder/GitQuery/pull/43) Set the default for cleanOutput to true ([tinder-aminghadersohi](https://github.com/tinder-aminghadersohi)

### 2.0.0 (2020-07-04) [#42](https://github.com/Tinder/GitQuery/pull/42) Version 2.0.0 ([tinder-aminghadersohi](https://github.com/tinder-aminghadersohi)
    - Kotlin updated to 1.4.10
    - Breaking change: Gradle Plugin: Renamed gitQueryTask to gitQuery 
    - Add option to not clean output directory - cleanOutput
    - Ability to specify output and repo dirs in config file
    - Bug: Plugin should detect absolute path for repo or output dir correctly
    - Bug: When changing the branch of a config file, the first try errors out
    - Remove the do not edit banner on top of files
    - Add verbose option
    - Add sample gradle project that syncs protos and generates kotlin
    - Updated circle ci file to publish snapshot and build sample
    - Replaces occurances of config.yaml with gitquery.yml to be more intuitive
    - Added a free form `extra` map to the `GitQueryConfig` to allow for custom key values for ease of integration
    - Added `Libs.kt` to `buildSrc`
    - More tests
    - Add changelog, and pull request template

### 1.1.1 (2020-07-01)
