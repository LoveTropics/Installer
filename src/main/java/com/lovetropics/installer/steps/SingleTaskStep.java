package com.lovetropics.installer.steps;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.lovetropics.installer.ProgressCallback;

public abstract class SingleTaskStep<T, R> implements InstallStep<T, R> {

    private Future<R> task;

    protected SingleTaskStep() {}

    @Override
    public final Future<R> start(T in, ProgressCallback callback) {
        if (this.task != null) {
            throw new IllegalStateException("Task already started!");
        }
        this.task = startTask(in, callback);
        return this.task;
    }

    protected abstract Future<R> startTask(T in, ProgressCallback callback);

    @Override
    public int getMaxProgress() {
        return 0;
    }

    @Override
    public void cancel() {
        if (task != null) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
