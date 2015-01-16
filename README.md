# Datomic Groovy Examples

## Setup

* Install [Groovy](http://groovy.codehaus.org/)
* Install [Gradle](http://www.gradle.org/)

Setup the classpath.

    bin/setup

You will need to re-run `bin/setup` if the classpath changes,
e.g. when Datomic or Groovy version changes.

## Usage

Run the project-local groovysh launcher

    bin/groovy.sh

Within the REPL, you should be able to interactively work through the
various files in src/main.  Start with HelloWorld.groovy.

## Questions, Feedback?

For specific feedback on the examples, please create an
[issue](https://github.com/Datomic/datomic-groovy-examples/issues). 

For questions about Datomic, try the [public mailing
list](http://groups.google.com/group/datomic).

## Contributing

This library is developed internally by Cognitect. Issues can be filed using
[Github Issues](https://github.com/Datomic/datomic-groovy-examples/issues). We do
not accept pull request or patches.

## Related Projects

* [Day of Datomic](https://github.com/Datomic/day-of-datomic) Clojure Examples
* [Datomic Java Examples](https://github.com/Datomic/datomic-java-examples)
* [groovy-datomic](https://github.com/jeffbrown/groovy-datomic) idiomatic Groovy extensions

## License

EPL. See epl-v10.html at the project root.
