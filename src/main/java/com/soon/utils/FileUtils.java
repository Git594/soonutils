package com.soon.utils;

import com.soon.utils.consts.Tips;
import info.monitorenter.cpdetector.io.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 文件工具
 *
 * @author HuYiGong
 * @since 2021/5/10
 **/
public class FileUtils {
    private FileUtils() {}

    /**
     * 删除过期文件
     * 根据创建时间判断是否过期
     * 若目录未过期则不遍历
     * 若过期后则遍历子文件删除过期的子文件
     * 删除文件后，目录为空则删除目录
     *
     * @param dirPath 待遍历目录
     * @param effectiveDays 有效的天数
     * @author HuYiGong
     * @since 2021/5/10 14:27
     */
    public static void deleteExpiredFiles(String dirPath, final int effectiveDays) throws IOException {
        Objects.requireNonNull(dirPath, String.format(Tips.PARAMS_NOT_NULL, "dirPath"));
        Objects.requireNonNull(dirPath, String.format(Tips.PARAMS_NOT_NULL, "effectiveDays"));
        Path path = Paths.get(dirPath);
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                ZonedDateTime createTime = attrs.creationTime().toInstant().atZone(ZoneId.systemDefault());
                ZonedDateTime expiredTime = LocalDateTime.now().minusDays(effectiveDays).atZone(ZoneId.systemDefault());
                if (expiredTime.compareTo(createTime) >= 0) {
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                ZonedDateTime createTime = attrs.creationTime().toInstant().atZone(ZoneId.systemDefault());
                ZonedDateTime expiredTime = LocalDateTime.now().minusDays(effectiveDays).atZone(ZoneId.systemDefault());
                if (expiredTime.compareTo(createTime) >= 0) {
                    Files.delete(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    Files.deleteIfExists(dir);
                } catch (DirectoryNotEmptyException ignored){
                    // 忽略该异常，不删除文件夹
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return java.lang.String 扩展名
     * @author HuYiGong
     * @since 2021/6/1 17:16
     */
    public static String getFileExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        int index = fileName.lastIndexOf('.') + 1;
        if (index >= fileName.length()) {
            return null;
        }
        return fileName.substring(index);
    }

    /**
     * 获取编码格式
     *
     * @param filePath 文件路径
     * @return java.nio.charset.Charset
     *         编码格式
     *         若未判断出编码格式，会返回默认编码格式
     * @author HuYiGong
     * @since 2021/6/2 11:06
     */
    public static Charset getFileEncoding(String filePath) {
        // Create the proxy:
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        // Add the implementations of info.monitorenter.cpdetector.io.ICodepageDetector:
        // This one is quick if we deal with unicode codepages:
        detector.add(new ByteOrderMarkDetector());
        // The first instance delegated to tries to detect the meta charset attribut in html pages.
        detector.add(new ParsingDetector(true));
        // This one does the tricks of exclusion and frequency detection, if first implementation is
        // unsuccessful:
        detector.add(JChardetFacade.getInstance());
        detector.add(ASCIIDetector.getInstance());
        Path path = Paths.get(filePath);
        // Work with the configured proxy:
        java.nio.charset.Charset charset = Charset.defaultCharset();
        try {
            charset = detector.detectCodepage(path.toUri().toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return charset;
    }

}
