# api-tests

Requirements: JDK-11

To build project:
1. `./gradlew clean build test` 

or use IDEA run configuration

or use docker to run  tests:
1. Open console in root project directory
2. `docker build --tag api-tests .`
3. `docker run api-tests`

[![CircleCI](https://circleci.com/gh/temagi/api-tests.svg?style=svg)](https://circleci.com/gh/temagi/api-tests)
ghj
