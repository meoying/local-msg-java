package com.meoying.localmessage.api;

import java.util.function.Supplier;

public interface Transaction {

    <T> T doWithTransaction(Supplier<T> supplier);
}
