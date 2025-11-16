package org.lovetropics.installer.steps;

import java.util.concurrent.Future;

import org.lovetropics.installer.ProgressCallback;

public interface InstallStep<T, R> {

    Future<R> start(T in, ProgressCallback callback);

    String getName();

    int getMaxProgress();

    void cancel();
}
