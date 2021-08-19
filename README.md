# Gestories

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.geniusrus/gestories/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.geniusrus/gestories)
[![codebeat badge](https://codebeat.co/badges/1c94bbef-63bb-45be-bad8-88fe7cbda032)](https://codebeat.co/projects/github-com-geniusrus-gestories-main)

## Short description

Android library for handling gestures on view like in Instagram Stories

## Details

Allows handling the following gestures:
1. holding your finger and releasing it
1. clicking on the left edge of the screen
1. clicking on the main screen space

You can also additionally track simple gestures of directions (up, down, left, right)

## Usage

Artifact is publishing to Maven Central. You can add this repository to your project with:
```gradle
repositories {
    mavenCentral()
}
```

Add to your app-level `build.gradle` file:
```gradle
implementation "io.github.geniusrus:gestories:$latest_version"
```

## Sample

The sample is on `app` module

## Developers

1. Viktor Likhanov

Yandex: [Gen1usRUS@yandex.ru](mailto:Gen1usRUS@yandex.ru)

## License
```
Apache v2.0 License

Copyright (c) 2021 Viktor Likhanov
