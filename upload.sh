#!/bin/sh

echo "\033[1;32mBuild Start \033[0m"
./gradlew :imagegroupview:build
echo "\033[1;32mInstall Start \033[0m"
./gradlew :imagegroupview:install
echo "\033[1;32mBintray Upload Start \033[0m"
./gradlew :imagegroupview:bintrayUpload
