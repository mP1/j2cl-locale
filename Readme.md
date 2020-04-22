[![Build Status](https://travis-ci.com/mP1/j2cl-locale.svg?branch=master)](https://travis-ci.com/mP1/j2cl-locale.svg.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/j2cl-locale/badge.svg?branch=master)](https://coveralls.io/github/mP1/j2cl-locale?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/j2cl-locale.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/j2cl-locale/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/j2cl-locale.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/j2cl-locale/alerts/)



j2c-locale
=================

This should be considered part of the internal support classes for the emulated `java.util.Locale` and should not be referenced in code.
It contains some utilties that can be used to assist the authoring of annotation processors that generate code or data that is Locale aware,
such as the emulation of `java.text` and others.

The classes included in this project should be considered internal, and this project should only be referenced as a dependency
by [j2cl-java-util-Locale](https://travis-ci.com/mP1/j2cl-java-util-Locale)].



## Locale selection (javac annotation processor argument)

The locales must be selected by setting a [annotation argument](https://docs.oracle.com/javase/7/docs/technotes/tools/windows/javac.html)
`walkingkooka.java-util-Locale` with a comma separated list of desired locales with trailing wildcard support.

Some examples values include.

- `*` All locales
- `EN` Only includes the `EN` locale without including `EN-US` or `EN-GB`.
- `EN-*` Includes all locales beginning with `EN`.
- `EN-*,FR-*` Include all English and French locales.

### Maven annotation processor argument

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <source>9</source>
        <target>9</target>
        <compilerArgs>
            <arg>-Awalkingkooka.j2cl.java.util.Locale=EN-*</arg>
        </compilerArgs>
    </configuration>
</plugin>
```



## Example uses

TODO Create a list of annotation processors and parent public project.

# Usage

The preferred way to use the plugin is to checkout the source

```bash
git clone git://github.com/mP1/j2cl-locale.git
```

and build and install with Maven.

```bash
mvn clean install
```



# Contributions

Suggestions via the issue tracker, and pull requests are most welcomed.
