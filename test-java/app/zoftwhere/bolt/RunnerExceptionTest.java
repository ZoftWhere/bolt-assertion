package app.zoftwhere.bolt;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RunnerExceptionTest {

    @Test
    void testNonFinal() {
        RuntimeException exception = new RunnerException("open", null) {
            @Override
            public String toString() {
                return "ExtensionException.class";
            }
        };
        try {
            throw exception;
        }
        catch (Exception e) {
            final var className = RunnerExceptionTest.class.getName() + "$1";
            assertEquals(className, e.getClass().getName());
        }
    }

    @Test
    void testPublicConstructors() {
        assertPublicConstructor(array(String.class));
        assertPublicConstructor(array(String.class, Throwable.class));
    }

    private void assertPublicConstructor(Class<?>[] parameters) {
        try {
            final var constructor = RunnerException.class.getConstructor(parameters);
            final var accessMask = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
            final var flag = constructor.getModifiers() & accessMask;

            if (flag == Modifier.PUBLIC) {
                return;
            }
        }
        catch (NoSuchMethodException ignore) {
        }

        final var expected = publicConstructorString(parameters);
        throw new AssertionFailedError("bolt.runner.constructor.check", expected, "<none>");
    }

    private Class<?>[] array(Class<?>... array) {
        return array;
    }

    private String publicConstructorString(Class<?>[] parameters) {
        final var builder = new StringBuilder("public")
            .append(" ")
            .append(RunnerException.class.getName())
            .append("(");
        if (parameters.length > 0) {
            builder.append(parameters[0].getName());
            for (int s = parameters.length, i = 1; i < s; i++) {
                builder.append(",").append(parameters[i].getName());
            }
        }
        builder.append(")");
        return builder.toString();
    }

}
