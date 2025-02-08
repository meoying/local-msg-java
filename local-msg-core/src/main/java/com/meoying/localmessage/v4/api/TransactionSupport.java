package com.meoying.localmessage.v4.api;

import java.util.function.Supplier;

public interface TransactionSupport {

    <T> T doWithTransaction(Supplier<T> supplier);
}
