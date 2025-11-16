package org.lovetropics.installer.ui;

import java.util.function.Supplier;

public class UIConfig implements Supplier<String> {

    private String value;

    public UIConfig() {
        this(null);
    }

    public UIConfig(String def) {
        this.value = def;
    }

    public synchronized void update(String value) {
        this.value = value;
    }

    @Override
    public synchronized String get() {
        return value;
    }
}
