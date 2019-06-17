package com.track.toy.test.core.factory;

import com.track.toy.bean.MultiProcessor;
import com.track.toy.helper.FileHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

@Slf4j
public class LoggerFactory {
    @Getter
    private static String loggerRoot;

    private static boolean isRunning = false;

    private static MultiProcessor<Log> multiProcessor;

    public static void startLog(String loggerRoot) {
        if (isRunning) {
            log.info("file log is running");
            return;
        }
        isRunning = true;
        LoggerFactory.loggerRoot = loggerRoot;
        multiProcessor = new MultiProcessor<>(10_000, 20, Log::toWrite);
    }

    public static void stopLog() {
        multiProcessor.stop();
        isRunning = false;
        multiProcessor = null;
    }

    public static void log(String message, String path) {
        multiProcessor.add(new Log(message, path));
    }

    @AllArgsConstructor
    private static class Log {
        String message;
        String path;

        private void toWrite() {
            File file = new File(path);
            FileHelper.createDirAndFileIfNotExists(file);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));) {
                writer.write(message);
                writer.newLine();
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
