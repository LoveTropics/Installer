package com.lovetropics.installer.steps;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lovetropics.installer.ProgressCallback;
import com.lovetropics.installer.util.MinecraftInstallationUtils;

import net.minecraftforge.installer.json.Install;

public class LauncherStep extends SingleTaskStep<Install, Void> {

    private final String profileName;
    private final Supplier<String> gameDir;

    public LauncherStep(String profileName, Supplier<String> gameDir) {
        this.profileName = profileName;
        this.gameDir = gameDir;
    }

    @Override
    public Future<Void> startTask(Install profile, ProgressCallback callback) {
        return CompletableFuture.supplyAsync(() -> {
            Path mcDir = MinecraftInstallationUtils.getMCDir().toPath();
            if (injectProfile(profile,  mcDir.resolve("launcher_profiles.json")) ||
                injectProfile(profile, mcDir.resolve("launcher_profiles_microsoft_store.json"))) {
                return null;
            }
            throw new IllegalStateException("No vanilla launcher installation found!");
        });
    }

    // Again borrowed from forge installer <3
    private boolean injectProfile(Install profile, Path target) {
        if (!Files.exists(target)) return false;
        try {
            JsonObject json = null;
            try (InputStream stream = Files.newInputStream(target)) {
                json = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            JsonObject _profiles = json.getAsJsonObject("profiles");
            if (_profiles == null) {
                _profiles = new JsonObject();
                json.add("profiles", _profiles);
            }

            JsonObject _profile = _profiles.getAsJsonObject(profileName);
            if (_profile == null) {
                _profile = new JsonObject();
                _profile.addProperty("name", profileName);
                _profile.addProperty("type", "custom");
                _profile.addProperty("gameDir", gameDir.get());
                _profiles.add(profileName, _profile);
            }
            _profile.addProperty("lastUsed", Instant.now().toString()); // Set this as the most recently used profile so
                                                                        // that it's selected by default.
            _profile.addProperty("lastVersionId", profile.getVersion());
            // String icon = profile.getIcon();
            // if (icon != null)
            // _profile.addProperty("icon", icon);
            String jstring = new GsonBuilder().setPrettyPrinting().create().toJson(json);
            Files.write(target, jstring.getBytes(StandardCharsets.UTF_8));
            
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "Installing vanilla profile";
    }
}
