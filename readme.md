# ZoftWhere Bolt Assertion
![Language](https://img.shields.io/github/languages/top/ZoftWhere/bolt-assertion) [![License](https://img.shields.io/github/license/ZoftWhere/bolt-assertion)](https://github.com/ZoftWhere/bolt-assertion/blob/master/license.txt) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/ZoftWhere/bolt-assertion) ![GitHub Release Date](https://img.shields.io/github/release-date/ZoftWhere/bolt-assertion)
![GitHub last commit (branch)](https://img.shields.io/github/last-commit/ZoftWhere/bolt-assertion/master?label=master%20updated)

Bolt-on unit test assertion for program output.

## Overview

The ZoftWhere Bolt Assertion Library is a bolt-on for unit-testing.  It has an elegant DSL for testing the output of a program.

## Compiling and Installing the Library

The source code can be compiled with Java language version 8.  It has been tested with Oracle JDK8, JDK11 and JDK12.

The project is Maven based, so executing the ```mvn install``` should install the library to the local repository.  It has been tested with Apache Maven v3.6.1.

The source code has a compile dependency on the [ZoftWhere Mutable Library](http://github.com/ZoftWhere/mutable-library).  Ensure that this dependency is installed.

The test source code is written against JDK 11, so when compile for and earlier JDK, the main source code can compiled and installed by executing the following maven call:

```shell script
mvn clean compile jar:jar source:jar javadoc:jar install:install-file@main-install -f pom.xml
```


## Release Notes

The [ZoftWhere Bolt Assertion Release Notes](https://github.com/ZoftWhere/bolt-assertion/tree/master/main-github/release-notes) are available for viewing/download [here](https://github.com/ZoftWhere/bolt-assertion/tree/main-github/release-notes).


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
            (Scanner scanner, BufferedWriter writer) -> {
                writer.write("Hello World!");
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
            try (FileWriter fileWriter = new FileWriter(System.getenv("OUTPUT_PATH"))) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                    main(args, scanner, bufferedWriter);
                }
            }
        }
    }

    // Make a proxy method to decrease boilerplate, and simplify.
    static void main(String[] arguments, Scanner scanner, BufferedWriter bufferedWriter)
    throws IOException
    {
        String name = scanner.nextLine();
        bufferedWriter.write(String.format("Hello %s!", name));
        bufferedWriter.newLine();
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
            System.out.println(String.format("Hello %s!", name));
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
    private static ThrowingConsumer3<String[], InputStream, OutputStream> redirect(
        final ThrowingConsumer1<String[]> program)
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

}
```

### More Examples

The source code for the [ZoftWhere Bolt Assertion Examples](https://github.com/ZoftWhere/bolt-assertion/tree/master/test-java/example), and more, are available for download [here](https://github.com/ZoftWhere/bolt-assertion/tree/master/test-java/example).


## License

Copyright (C) 2020 ZoftWhere

Licensed under the MIT License

------