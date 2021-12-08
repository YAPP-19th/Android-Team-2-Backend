package com.yapp.sharefood.common.utils;

import com.yapp.sharefood.common.exception.file.FileTypeValidationException;
import com.yapp.sharefood.common.random.RandomStringCreator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final String FILE_SEPARATOR = ".";


    public static String createSaveFileName(String prefix, String originalFileName) {
        final String formattedDateTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String extension = getFileExtension(originalFileName);
        return prefix + formattedDateTime + RandomStringCreator.createRandomUUIDStr() + extension;
    }

    private static String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf(FILE_SEPARATOR));
        } catch (StringIndexOutOfBoundsException e) {
            throw new FileTypeValidationException();
        }
    }
}
