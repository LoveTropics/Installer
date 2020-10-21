package com.lovetropics.installer.steps;

import java.util.concurrent.Future;

import com.lovetropics.installer.ProgressCallback;

public interface InstallStep<T, R> {

    Future<R> start(T in, ProgressCallback callback);

    String getName();

    int getMaxProgress();

    void cancel();
}
