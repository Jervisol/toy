package com.track.toy.test.core.factory;

import com.track.toy.bean.MultiProcessor;
import com.track.toy.helper.FileHelper;
import com.track.toy.test.core.common.FileLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class LoggerFactory {

    private static String loggerRoot = FileHelper.getAppRoot() + "/log/log" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

    @Getter
    private static boolean isDebug = true;

    private static boolean isRunning = false;

    private static MultiProcessor<Log> multiProcessor;

    private static final FileLogger SYSTEM_FILE_LOGGER = new FileLogger("system.text");

    public static void systemLog(String message, Object... objects) {
        SYSTEM_FILE_LOGGER.info(message, objects);
    }

    public static void startLog() {
        String logLevel = ConfigureFactory.get("log-level");
        LoggerFactory.isDebug = logLevel != null && logLevel.toLowerCase().equals("debug");

        if (LoggerFactory.isRunning) {
            log.info("file log is running");
            return;
        }
        LoggerFactory.isRunning = true;
        LoggerFactory.multiProcessor = new MultiProcessor<>(10_000, 20, Log::toWrite);

        Thread daemon = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("testing , please wait ...");
            }
        });
        daemon.setDaemon(true);
        daemon.start();
    }

    public static void stopLog() {
        multiProcessor.stop();
        isRunning = false;
    }

    public static void log(String message, String path) {
        multiProcessor.add(new Log(message, loggerRoot + "/" + path + ".txt"));
    }

    @AllArgsConstructor
    private static class Log {
        String message;
        String path;

        private static final Map<String, Object> LOCK_MAP = new ConcurrentHashMap<>();

        private void toWrite() {
            File file = new File(path);
            FileHelper.createDirAndFileIfNotExists(file);

            Object lock = LOCK_MAP.get(path);
            if (lock == null) {
                lock = new Object();
                LOCK_MAP.put(path, lock);
            }

            synchronized (lock) {
                List<String> lines = null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
                    lines = reader.lines().collect(Collectors.toList());
                    lines.add(message);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
                    if (lines != null) {
                        lines.stream()
                                .filter(line -> line.indexOf("|") != -1)
                                .sorted()
                                .forEach(line -> {
                                    try {
                                        writer.write(line);
                                        writer.newLine();
                                        writer.newLine();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
