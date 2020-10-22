package com.lovetropics.installer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutionException;

import com.lovetropics.installer.steps.InstallStep;

public class InstallProcess<T> {
    
    public static InstallProcess<Void> create() {
        return new InstallProcess<>();
    }

    private final Deque<InstallStep<?, ?>> steps = new ArrayDeque<>();
    @SuppressWarnings("rawtypes")
    private InstallStep currentStep;
    private boolean canceled;

    private InstallProcess() {}

    @SuppressWarnings("unchecked")
    public <R> InstallProcess<R> then(InstallStep<T, R> step) {
        steps.add(step);
        return (InstallProcess<R>) this;
    }

    @SuppressWarnings("unchecked")
    public void run(ProgressCallback callback) {
        Object result = null;
        while (!canceled && !steps.isEmpty()) {
            try {
                currentStep = steps.remove();
                callback.push(currentStep.getName(), currentStep.getMaxProgress());
                Thread.sleep(500); // TODO REMOVE THIS
                result = currentStep.start(result, callback).get();
                callback.pop();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            } finally {
                currentStep = null;
            }
        }
        steps.clear();
    }

    public void cancel() {
        canceled = true;
        if (running()) {
            currentStep.cancel();
        } else {
            // Not started yet, nothing to cancel
            return;
        }
        while (!steps.isEmpty()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean running() {
        return currentStep != null;
    }
}
