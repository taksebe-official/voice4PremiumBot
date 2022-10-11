package ru.taksebe.telegram.premium.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EnableAsync
@Component
public class FileScheduler {
    Logger logger = LoggerFactory.getLogger(FileScheduler.class);

    private final String incomingTempFileNamePrefix;
    private final String outgoingTempFileNamePrefix;

    public FileScheduler(@Value("${files.incoming}") String incomingTempFileNamePrefix,
                         @Value("${files.outgoing}") String outgoingTempFileNamePrefix) {
        this.incomingTempFileNamePrefix = incomingTempFileNamePrefix;
        this.outgoingTempFileNamePrefix = outgoingTempFileNamePrefix;
    }

    @Async
    @Scheduled(cron = "${schedule.cron.delete-temp-files}")
    public void deleteTempFiles() {
        for (String path : getToDeletePathList()) {
            try {
                Files.deleteIfExists(Path.of(path));
            } catch (FileSystemException e) {
                logger.debug(e.getMessage());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private List<String> getToDeletePathList() {
        File dir = new File(System.getProperty("java.io.tmpdir"));

        List<String> tempFilePathList = new ArrayList<>();

        for (File file : Objects.requireNonNull(dir.listFiles())){
            if (file.isFile() && needToDelete(file.getName()))
                tempFilePathList.add(file.getAbsolutePath());
        }

        return tempFilePathList;
    }

    private boolean needToDelete(String fileName) {
        return fileName.contains(this.incomingTempFileNamePrefix) || fileName.contains(this.outgoingTempFileNamePrefix);
    }
}