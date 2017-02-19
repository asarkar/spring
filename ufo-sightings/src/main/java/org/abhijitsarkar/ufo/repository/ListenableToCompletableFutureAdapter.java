package org.abhijitsarkar.ufo.repository;

import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

/**
 * @author Abhijit Sarkar
 */
public class ListenableToCompletableFutureAdapter<T> extends CompletableFuture<T> {
    private final ListenableFuture<T> listenable;

    public ListenableToCompletableFutureAdapter(ListenableFuture<T> listenable) {
        this.listenable = listenable;

        this.listenable.addCallback(this::complete, this::completeExceptionally);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean result = listenable.cancel(mayInterruptIfRunning);
        super.cancel(mayInterruptIfRunning);
        return result;
    }
}
