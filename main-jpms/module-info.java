module zoftwhere.bolt.assertion {
    requires transitive zoftwhere.mutable.library;
    uses app.zoftwhere.function.ThrowingConsumer1;
    uses app.zoftwhere.function.ThrowingConsumer2;
    uses app.zoftwhere.function.ThrowingConsumer3;
    uses app.zoftwhere.function.ThrowingFunction0;
    exports app.zoftwhere.bolt;
    exports app.zoftwhere.bolt.api;
}