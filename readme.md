# ZoftWhere Bolt Assertion

![Language](https://img.shields.io/github/languages/top/ZoftWhere/bolt-assertion)
[![License](https://img.shields.io/github/license/ZoftWhere/bolt-assertion)](https://github.com/ZoftWhere/bolt-assertion/blob/master/license.txt)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/ZoftWhere/bolt-assertion)
![GitHub Release Date](https://img.shields.io/github/release-date/ZoftWhere/bolt-assertion)
![GitHub last commit (branch)](https://img.shields.io/github/last-commit/ZoftWhere/bolt-assertion/master?label=master%20updated)

Bolt-on unit test assertion for program output.

## Overview

The ZoftWhere Bolt Assertion Library is a bolt-on for unit-testing. It has an elegant Java based DSL for testing the
output of a program.

## Compiling and Installing the Library

The source code can be compiled with Java language version 8. It has been tested with Oracle JDK8, JDK11 and JDK14. The
test sources, including examples, compile with JUnit \(v5.6.2\), and JDK 11.

The project is Maven based, so executing the ```mvn install``` should install the library to the local repository
\(Requires at least JDK11\). It has been tested with Apache Maven v3.6.3.

The project will package the JavaDoc archive using JDK8 rules and styles. The JavaDoc archive can be set to a later
release by specifying the ```maven.compiler.main-jdk``` property. For example, the JavaDoc will be packaged and
installed for JDK11 by calling:

``` shell script
mvn clean install -Dmaven.compiler.main-jdk=11
```

If the project needs to be installed against JDK8, it can be accomplished by calling the following Maven command:

``` shell script
mvn clean compiler:compile@main-compile-jdk8 jar:jar@main-jar source:jar@main-sources javadoc:jar@main-javadoc-jdk8 moditect:add-module-info@main-jpms install:install-file@main-install
```

## Testing the Library

The library source code can/should be tested when using Maven to package the library to a Java archive.

To check for any hidden issue, a barrage of tests can be run by running the `app.zoftwhere.bolt.BoltDelugeBarrage` test
application.

## Release Notes

The [ZoftWhere Bolt Assertion Release Notes](/main-github/release-notes) are available for
viewing/download [here](/main-github/release-notes).

## Examples

Note that the example code includes the JUnit @Test annotation. Java programmers may omit/replace these as is needed
with the unit testing-framework of their choice.

### Hello World Lambda

The bolt assertion provides a Runner instance. Here is a Hello World example:

```java
public class HelloWorldExample {

  // An immutable runner that can be reused.
  private final Runner runner = new Runner();

  @Test
  void testCase() {
    runner
        .run((Scanner scanner, PrintStream out) -> out.print("Hello World!"))
        .input()
        .expected("Hello World!")
        .assertSuccess();
  }
}
```

### Structured Command Line Programs

Java programs that need to read from an input stream, and write to a file \(based on environment variables\), can be
written for testing. The following is an example of this:

```java
public class CommandLineExample {

  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      try (PrintStream out = new PrintStream(System.getenv("OUTPUT_PATH"))) {
        run(args, scanner, out);
      }
    }
  }

  // Make a proxy method to decrease boilerplate, and simplify.
  static void run(String[] arguments, Scanner scanner, PrintStream out) {
    String name = scanner.nextLine();
    out.printf("Hello %s!%n", name);
  }
}
```

```java
public class CommandLineExampleTest {

  private final Runner runner = new Runner();

  @Test
  void testCase() {
    runner
        .run(CommandLineExample::main)
        .argument()
        .input("World")
        .expected("Hello World!", "")
        .assertSuccess();
  }
}
```

### Standard Java Output Redirection

Although it is not recommended, with the correct unit testing framework, an existing program can be run, as is, with the
standard console input/output/error stream redirected. Calling code should include resetting the input/output/error
streams afterward.

```java
public class ConsoleOutputExample {

  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      String name = scanner.nextLine();
      System.out.printf("Hello %s!%n", name);
    }
  }
}
```

```java
public class ConsoleOutputExampleTest {

  private final Runner runner = new Runner();

  /** Not Thread Safe * */
  private static RunConsoleArgued redirect(final ThrowingConsumer<String[]> program) {
    return (arguments, inputStream, outputStream) -> {
      var systemIn = System.in;
      var systemOutput = System.out;
      var systemError = System.err;

      System.setIn(inputStream);
      System.setOut(new PrintStream(outputStream));
      System.setErr(new PrintStream(outputStream));

      try {
        program.accept(arguments);
      } finally {
        System.setIn(systemIn);
        System.setOut(systemOutput);
        System.setErr(systemError);
      }
    };
  }

  @Test
  void testCase() {
    // Not Thread Safe!
    runner
        .runConsole(redirect(ConsoleOutputExample::main))
        .argument()
        .input("World")
        .expected("Hello World!", "")
        .assertSuccess();
  }

  private interface ThrowingConsumer<T> {

    void accept(T value) throws Exception;
  }
}
```

## License

Copyright (c) 2019-2023 ZoftWhere

Licensed under the MIT License

------