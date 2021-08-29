package com.github.kongwu.xpocket.plugin.usefulscripts;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.management.ManagementFactory.getRuntimeMXBean;

/**
 * Low level access to OS class.
 *
 * Copied from https://github.com/OpenHFT/Chronicle-Core
 */
public final class OS {

    public static final String TMP = System.getProperty("java.io.tmpdir");
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String USER_HOME = System.getProperty("user.home");
    public static final int SAFE_PAGE_SIZE = 64 << 10;
    private static final String HOST_NAME = getHostName0();
    private static final String USER_NAME = System.getProperty("user.name");
    private static final int MAP_RO = 0;
    private static final int MAP_RW = 1;
    private static final int MAP_PV = 2;
    private static final boolean IS64BIT = is64Bit0();
    private static final AtomicInteger PROCESS_ID = new AtomicInteger();
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_LINUX = OS.startsWith("linux");
    private static final boolean IS_MAC = OS.contains("mac");
    private static final boolean IS_WIN = OS.startsWith("win");
    private static final boolean IS_WIN10 = OS.equals("windows 10");
    private static final AtomicLong memoryMapped = new AtomicLong();
    private static int PAGE_SIZE; // avoid circular initialisation
    private static int MAP_ALIGNMENT;

    private OS() {
    }



    public static String getHostName() {
        return HOST_NAME;
    }

    public static String getUserName() {
        return USER_NAME;
    }


    private static String getHostName0() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

    /**
    /**
     * @return is the JVM 64-bit
     */
    public static boolean is64Bit() {
        return IS64BIT;
    }

    private static boolean is64Bit0() {
        String systemProp;
        systemProp = System.getProperty("com.ibm.vm.bitmode");
        if (systemProp != null) {
            return "64".equals(systemProp);
        }
        systemProp = System.getProperty("sun.arch.data.model");
        if (systemProp != null) {
            return "64".equals(systemProp);
        }
        systemProp = System.getProperty("java.vm.version");
        return systemProp != null && systemProp.contains("_64");
    }

    public static int getProcessId() {
        // getting the process id is slow if the reserve DNS is not setup correctly.
        // which is frustrating since we don't actually use the hostname.
        int id = PROCESS_ID.get();
        if (id == 0)
            PROCESS_ID.set(id = getProcessId0());
        return id;
    }

    private static int getProcessId0() {
        String pid = null;
        final File self = new File("/proc/self");
        try {
            if (self.exists())
                pid = self.getCanonicalFile().getName();
        } catch (IOException ignored) {
            // ignored
        }
        if (pid == null)
            pid = getRuntimeMXBean().getName().split("@", 0)[0];
        if (pid != null) {
            try {
                return Integer.parseInt(pid);
            } catch (NumberFormatException e) {
                // ignored
            }
        }
        int rpid = ThreadLocalRandom.current().nextInt(2, 1 << 16);
        return rpid;
    }


    public static boolean isWindows() {
        return IS_WIN;
    }

    public static boolean isMacOSX() {
        return IS_MAC;
    }

    public static boolean isLinux() {
        return IS_LINUX;
    }



    public static String userDir() {
        return USER_DIR;
    }

    public static String run(String... cmds) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        StringWriter sw = new StringWriter();
        char[] chars = new char[1024];
        try (Reader r = new InputStreamReader(process.getInputStream())) {
            for (int len; (len = r.read(chars)) > 0; ) {
                sw.write(chars, 0, len);
            }
        }
        return sw.toString();
    }

    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
