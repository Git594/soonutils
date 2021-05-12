package com.soon.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

}
