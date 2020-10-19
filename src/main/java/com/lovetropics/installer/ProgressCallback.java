package com.lovetropics.installer;

public interface ProgressCallback {

    default void push(String step) {
        System.out.println("Pushing");
        push(step, 0);
    }

    void push(String step, int maxProgress);

    default void next(String step) {
        next(step, 0);
    }

    default void next(String step, int maxProgress) {
        pop();
        push(step, maxProgress);
    }

    void pop();

    default void step() {
        addProgress(1);
    }

    void addProgress(int amount);
}
