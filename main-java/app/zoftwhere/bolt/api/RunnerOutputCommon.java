package app.zoftwhere.bolt.api;

public interface RunnerOutputCommon extends AbstractUnit.Expected<RunnerAsserter> {

    String[] output();

    Exception exception();
}
