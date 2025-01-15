package com.meoying.localmessage.api;

import java.util.function.Supplier;

public interface TransactionV1 {

    <T> T doWithTransaction(Supplier<T> supplier);
}
