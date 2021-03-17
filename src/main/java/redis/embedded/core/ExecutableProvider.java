package redis.embedded.core;

import redis.embedded.model.OsArchitecture;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static redis.embedded.model.OsArchitecture.*;
import static redis.embedded.util.IO.writeResourceToExecutableFile;

public interface ExecutableProvider {

    /**
     * A system property that allows to configure a custom path for redis-server
     */
    String CUSTOM_PATH = System.getProperty("com.github.codemonstur.embedded-redis");

    String getExecutableFor(OsArchitecture osa);

    // The logic implemented here was not changed from the original code,
    // however, this feels like a security vulnerability to me; what happens
    // when an attacker places a binary of his/her choosing at the exact place
    // the default config will look?
    // FIXME provide a proper location lookup implementation
    default File get() throws IOException {
        final String executablePath = getExecutableFor(detectOSandArchitecture());
        final File executable = new File(executablePath);
        return executable.isFile() ? executable : writeResourceToExecutableFile(executablePath);
    }

    /*
    TODO: Decide whether the custom path should have priority or not.
    At the moment it breaks redis.embedded.RedisServerTest.shouldOverrideDefaultExecutable
     */
    static ExecutableProvider newRedis2_8_19Provider() {
        return osArchitecture -> {
            if (CUSTOM_PATH != null) {
                return CUSTOM_PATH;
            }

            return newRedis2_8_19Map().get(osArchitecture);
        };
    }

    static Map<OsArchitecture, String> newRedis2_8_19Map() {
        final Map<OsArchitecture, String> map = new HashMap<>();
        map.put(WINDOWS_x86, "/redis-server-2.8.19.exe");
        map.put(WINDOWS_x86_64, "/redis-server-2.8.19.exe");
        map.put(UNIX_x86, "/redis-server-2.8.19-32");
        map.put(UNIX_x86_64, "/redis-server-2.8.19");
        map.put(MAC_OS_X_x86, "/redis-server-2.8.19.app");
        map.put(MAC_OS_X_x86_64, "/redis-server-2.8.19.app");
        return map;
    }

}
