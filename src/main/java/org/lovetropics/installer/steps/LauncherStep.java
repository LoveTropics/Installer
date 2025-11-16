package org.lovetropics.installer.steps;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.io.IOUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lovetropics.installer.Installer;
import org.lovetropics.installer.ProgressCallback;
import org.lovetropics.installer.util.MinecraftInstallationUtils;

import net.minecraftforge.installer.json.InstallV1;

public class LauncherStep extends SingleTaskStep<InstallV1, Void> {

    private final String profileName;
    private final Supplier<String> gameDir;

    public LauncherStep(String profileName, Supplier<String> gameDir) {
        this.profileName = profileName;
        this.gameDir = gameDir;
    }

    @Override
    public Future<Void> startTask(InstallV1 profile, ProgressCallback callback) {
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
    private boolean injectProfile(InstallV1 profile, Path target) {
        if (!Files.exists(target)) return false;
        try {
            JsonObject json = null;
            try (InputStream stream = Files.newInputStream(target)) {
                json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
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
                _profiles.add(profileName, _profile);
            }
            _profile.addProperty("name", profileName);
            _profile.addProperty("type", "custom");
            _profile.addProperty("gameDir", gameDir.get());
            _profile.addProperty("lastUsed", Instant.now().plus(1L, ChronoUnit.SECONDS).toString()); // Set this as the most recently used profile so that it's selected by default.
            _profile.addProperty("lastVersionId", profile.getVersion());
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            try {
                // Dark OS level magics
                Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang","type","OperatingSystem"), "TotalPhysicalMemorySize");
                long bytes = Long.parseLong(attribute.toString());
                // Convert bytes to MB
                long megs = bytes >> 20;
                // Between 2 and 4 GB
                int allocate = (int) Math.max(Math.min(megs / 3D, 4 << 10), 2 << 10);
                String args = String.format("-Xmx%dM -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M", allocate);
                _profile.addProperty("javaArgs", args);
            } catch (InstanceNotFoundException | AttributeNotFoundException | MalformedObjectNameException | ReflectionException | MBeanException e) {
                e.printStackTrace();
            }
            try (InputStream is = Installer.class.getResourceAsStream("/icon128.png")) {
                String base64 = new String(Base64.getEncoder().encode(IOUtils.toByteArray(is)));
                _profile.addProperty("icon", "data:image/png;base64," + base64);
            }
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
