package com.meoying.localmessage.utils;

import com.meoying.localmessage.api.TransactionV1;
import org.springframework.transaction.annotation.Propagation;

import java.util.function.Supplier;

public class SpringTransactionSupport implements TransactionV1 {

    private final TransactionHelper transactionHelper;

    public SpringTransactionSupport(TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public <T> T doWithTransaction(Supplier<T> supplier) {
        // todo 讨论这里的传播模式 ,REQUIRED必须等外层事务提交才能提交，所以可能会导致发送失败
        return transactionHelper.execute(Propagation.REQUIRED, status -> {
            try {
                return supplier.get();
            }catch (Exception e){
                status.setRollbackOnly();
                throw new RuntimeException(e);
            }
        });
    }
}
