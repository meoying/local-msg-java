package com.meoying.localmessage.utils;

import com.meoying.localmessage.core.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class TransactionHelper extends TransactionTemplate {

    private final Cache<TransactionDefinition, TransactionTemplate> cache;

    public TransactionHelper(Cache<TransactionDefinition, TransactionTemplate> cache,
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

    @Nullable
    public <T> T get(Supplier<T> supplier) {
        return this.get(this, supplier);
    }

    @Nullable
    public <T> T get(Propagation propagation, Supplier<T> supplier) {
        return this.get(new DefaultTransactionDefinition(propagation.value()), supplier);
    }

    @Nullable
    public <T> T get(TransactionDefinition definition, Supplier<T> supplier) {
        return this.execute(definition, (status) -> {
            return supplier.get();
        });
    }

    @Nullable
    public <T> T get(Function<TransactionStatus, T> function) {
        return this.get(this, function);
    }

    @Nullable
    public <T> T get(Propagation propagation, Function<TransactionStatus, T> function) {
        return this.get(new DefaultTransactionDefinition(propagation.value()), function);
    }

    @Nullable
    public <T> T get(TransactionDefinition definition, Function<TransactionStatus, T> function) {
        return this.execute(definition, function::apply);
    }

}
