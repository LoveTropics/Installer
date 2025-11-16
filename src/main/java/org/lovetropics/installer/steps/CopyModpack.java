package org.lovetropics.installer.steps;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.lovetropics.installer.ProgressCallback;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class CopyModpack extends SingleTaskStep<Void, Void> {

    private final String modpackZip;
    private final Supplier<String> gameDir;

    public CopyModpack(String modpackZip, Supplier<String> gameDir) {
        this.modpackZip = modpackZip;
        this.gameDir = gameDir;
    }

    @Override
    protected Future<Void> startTask(Void in, ProgressCallback callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                ZipFile zip = new ZipFile(modpackZip);
                zip.extractAll(gameDir.get());
            } catch (ZipException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public String getName() {
        return "Copying Modpack";
    }
}
