# ZoftWhere Bolt Assertion
![Language](https://img.shields.io/github/languages/top/ZoftWhere/bolt-assertion) [![License](https://img.shields.io/github/license/ZoftWhere/bolt-assertion)](https://github.com/ZoftWhere/bolt-assertion/blob/master/license.txt) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/ZoftWhere/bolt-assertion) ![GitHub Release Date](https://img.shields.io/github/release-date/ZoftWhere/bolt-assertion)
![GitHub last commit (branch)](https://img.shields.io/github/last-commit/ZoftWhere/bolt-assertion/master?label=master%20updated)

Bolt-on unit test assertion for program output.

## Overview

The ZoftWhere Bolt Assertion Library is a bolt-on for unit-testing.  It has an elegant Java based DSL for testing the output of a program.


## Compiling and Installing the Library

The source code can be compiled with Java language version 8.  It has been tested with Oracle JDK8, JDK11 and JDK12.  The test sources are compiled against JDK 11.

The project is Maven based, so executing the ```mvn install``` should install the library to the local repository (Requires at least JDK11).  It has been tested with Apache Maven v3.6.1.

If the project needs to be installed against JDK8, it can be accomplished by calling the following Maven command:

``` shell script
mvn clean compiler:compile@main-compile-jdk8 jar:jar@main-jar source:jar@main-sources javadoc:jar@main-javadoc moditect:add-module-info@main-jpms install:install-file@main-install
``` 


## Release Notes

The [ZoftWhere Bolt Assertion Release Notes](/main-github/release-notes) are available for viewing/download [here](/main-github/release-notes).


## Examples

### Hello World Lambda

The bolt assertion provides a Runner instance.  Here is a Hello World example:
``` kotlin
public class HelloWorldExample {

    // An immutable runner that can be reused.
    private final Runner runner = new Runner();

    @Test
    void testCase() {

        // Hello World lambda.
        runner.run(
            (Scanner scanner, PrintStream out) -> {
                out.print("Hello World!");
            })
            .input()
            .expected("Hello World!")
            .assertSuccess();
    }

}
```

### Structured Command Line Programs

Java programs that need to read from an input stream, and write to a file (based on environment variables), can be written to be easily testable.  The following is an example of this:
``` kotlin
public class CommandLineExample {

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            try (PrintStream out = new PrintStream(System.getenv("OUTPUT_PATH"))) {
                main(args, scanner, out);
            }
        }
    }

    // Make a proxy method to decrease boilerplate, and simplify.
    static void main(String[] arguments, Scanner scanner, PrintStream out) {
        String name = scanner.nextLine();
        out.printf("Hello %s!%n", name));
    }

}
```
``` kotlin 
class CommandLineExampleTest {

    private final Runner runner = new Runner();

    @Test
    void testCase() {
        runner.run(CommandLineExample::main)
            .argument()
            .input("World")
            .expected("Hello World!", "")
            .assertSuccess();
    }

}
```

### Standard Java Output Redirection

Although it is not recommended, with the correct unit testing framework, an existing program can be run, as is, with the standard console input/output/error stream redirected.  The user will be responsible for ensuring that the console input/output/error streams are properly returned.
``` kotlin
public class ConsoleOutputExample {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String name = scanner.nextLine();
            System.out.printf("Hello %s!%n", name);
        }
    }

}
```
``` kotlin
class ConsoleOutputExampleTest {

    private final Runner runner = new Runner();

    @Test
    void testCase() {
        // Not Thread Safe!
        runner.runConsole(redirect(ConsoleOutputExample::main))
            .argument()
            .input("World")
            .expected("Hello World!", "")
            .assertSuccess();
    }

    /** Not Thread Safe **/
    private static RunConsoleArgued redirect(
        final ThrowingConsumer<String[]> program)
    {
        return (arguments, inputStream, outputStream) -> {
            var systemIn = System.in;
            var systemOutput = System.out;
            var systemError = System.err;

            System.setIn(inputStream);
            System.setOut(new PrintStream(outputStream));
            System.setErr(new PrintStream(outputStream));

            try {
                program.accept(arguments);
            }
            finally {
                System.setIn(systemIn);
                System.setOut(systemOutput);
                System.setErr(systemError);
            }
        };
    }

    private interface ThrowingConsumer<T> {

        void accept(T value) throws Exception;
    }

}
```

### More Examples

The source code for the [ZoftWhere Bolt Assertion Examples](/test-java/example), and more, are available for download [here](/test-java/example).


## License

Copyright (c) 2020 ZoftWhere

Licensed under the MIT License

------