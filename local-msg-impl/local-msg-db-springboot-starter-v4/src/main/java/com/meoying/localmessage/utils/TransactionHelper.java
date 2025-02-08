package com.meoying.localmessage.utils;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

public class TransactionHelper extends TransactionTemplate {

    private final Cache cache;

    public TransactionHelper(Cache cache,
                             PlatformTransactionManager transactionManager) {
        super(transactionManager);
        this.cache = cache;
    }

    private TransactionTemplate getTemplateInternal(TransactionDefinition definition) {
        return cache.get(definition,
                () -> new TransactionTemplate(Objects.requireNonNull(this.getTransactionManager()), definition));
    }

    private TransactionTemplate getTemplate(TransactionDefinition definition) {
        if (definition == this) {
            return this;
        } else {
            definition = new DefaultTransactionDefinition(definition);
            return definition.equals(this) ? this : this.getTemplateInternal(definition);
        }
    }

    @Nullable
    public <T> T execute(Propagation propagation, TransactionCallback<T> action) throws TransactionException {
        return this.execute(new DefaultTransactionDefinition(propagation.value()), action);
    }

    @Nullable
    public <T> T execute(TransactionDefinition definition, TransactionCallback<T> action) throws TransactionException {
        return this.getTemplate(definition).execute(action);
    }
}
