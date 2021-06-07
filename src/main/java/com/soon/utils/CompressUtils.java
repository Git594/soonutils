package com.soon.utils;

import com.soon.utils.consts.Tips;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 *
 * @author HuYiGong
 * @since 2021/4/21
 **/
public class CompressUtils {
    private CompressUtils() {}

    /**
     * 解压缩
     *
     * @param sourceFile 源文件路径(zip,tar)
     * @param targetDir 目标文件夹路径
     * @author HuYiGong
     * @since 2021/6/3 16:18
     */
    public static void decompress(String sourceFile, String targetDir) throws IOException, ArchiveException {
        String ext = FileUtils.getFileExtension(sourceFile);
        Charset charset = FileUtils.getFileEncoding(sourceFile);
        if (ArchiveStreamFactory.ZIP.equals(ext)) {
            decompressZip(sourceFile, targetDir, charset);
        } else {
            decompress(sourceFile, targetDir, charset);
        }
    }

    /**
     * 解压缩
     *
     * @param sourceFile 源文件路径(zip,tar)
     * @param targetDir 目标文件夹路径
     * @param charset 编码格式
     * @author HuYiGong
     * @since 2021/4/21 16:17
     */
    public static void decompress(String sourceFile, String targetDir, Charset charset) throws IOException, ArchiveException {
        Objects.requireNonNull(sourceFile, String.format(Tips.PARAMS_CANNOT_BE_NULL, "sourceFile"));
        Objects.requireNonNull(sourceFile, String.format(Tips.PARAMS_CANNOT_BE_NULL, "targetDir"));
        File archiveFile = new File(sourceFile);
        // 文件不存在，跳过
        if (!archiveFile.exists()) {
            return;
        }
        ArchiveStreamFactory factory = new ArchiveStreamFactory();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceFile));
        try (ArchiveInputStream input = factory.createArchiveInputStream(ArchiveStreamFactory.detect(in), in, charset.toString())) {
            ArchiveEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                if (!input.canReadEntryData(entry)) {
                    // log something?
                    continue;
                }
                String name = Paths.get(targetDir, entry.getName()).toString();
                File f = new File(name);
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        throw new IOException(String.format(Tips.FAILED_TO_CREATE_FOLDER, f));
                    }
                } else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException(String.format(Tips.FAILED_TO_CREATE_FOLDER, parent));
                    }
                    try (OutputStream o = Files.newOutputStream(f.toPath())) {
                        IOUtils.copy(input, o);
                    }
                }
            }
        }
    }

    /**
     * 解压缩Zip
     *
     * @param sourceFile 源文件路径(zip,tar)
     * @param targetDir 目标文件夹路径
     * @param charset 编码格式
     * @author HuYiGong
     * @since 2021/4/21 16:17
     */
    public static void decompressZip(String sourceFile, String targetDir, Charset charset) throws IOException {
        Objects.requireNonNull(sourceFile, String.format(Tips.PARAMS_CANNOT_BE_NULL, "sourceFile"));
        Objects.requireNonNull(sourceFile, String.format(Tips.PARAMS_CANNOT_BE_NULL, "targetDir"));
        try (ZipFile zipFile = new ZipFile(sourceFile, charset)) {
            Enumeration<?> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
                String name = Paths.get(targetDir, zipEntry.getName()).toString();
                File file = new File(name);
                if (zipEntry.isDirectory()) {
                    if (!file.isDirectory() && !file.mkdirs()) {
                        throw new IOException(String.format(Tips.FAILED_TO_CREATE_FOLDER, file));
                    }
                } else {
                    File parent = file.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException(String.format(Tips.FAILED_TO_CREATE_FOLDER, parent));
                    }
                    try (OutputStream o = Files.newOutputStream(file.toPath())) {
                        IOUtils.copy(zipFile.getInputStream(zipEntry), o);
                    }
                }
            }
        }
    }

    /**
     * 压缩多个文件为一个压缩文件
     * 如果文件列表中存在文件夹，则只压缩文件夹中的文件
     * 如果目标文件存在未创建的文件夹，则自动创建
     *
     * @param sourceFiles 源文件列表
     * @param targetFile 目标文件
     * @author HuYiGong
     * @since 2021/6/4 10:18
     */
    public static void compressFiles(List<File> sourceFiles, File targetFile) throws IOException, ArchiveException {
        if (Objects.isNull(sourceFiles) || sourceFiles.isEmpty()) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "sourceFiles"));
        }
        File parentFile = targetFile.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException(String.format(Tips.FAILED_TO_CREATE_FOLDER, parentFile));
        }
        String ext = FileUtils.getFileExtension(targetFile.getName());
        ArchiveStreamFactory asf = new ArchiveStreamFactory();
        try (ArchiveOutputStream o = asf.createArchiveOutputStream(ext, new FileOutputStream(targetFile))) {
            for (int i = 0; i < sourceFiles.size(); i++) {
                File f = sourceFiles.get(i);
                if (f.isDirectory()) {
                    File[] files = f.listFiles();
                    if (files != null) {
                        sourceFiles.addAll(Arrays.asList(files));
                    }
                    continue;
                }
                // maybe skip directories for formats like AR that don't store directories
                ArchiveEntry entry = o.createArchiveEntry(f, f.getName());
                // potentially add more flags to entry
                o.putArchiveEntry(entry);
                if (f.isFile()) {
                    try (InputStream inputStream = Files.newInputStream(f.toPath())) {
                        IOUtils.copy(inputStream, o);
                    }
                }
                o.closeArchiveEntry();
            }
        }
    }

    /**
     * 压缩文件或文件夹为一个压缩文件
     * 如果目标文件存在未创建的文件夹，则自动创建
     *
     * @param sourcePath 原文件
     * @param targetFile 目标文件
     * @author HuYiGong
     * @since 2021/6/4 14:05
     */
    public static void compress(String sourcePath, File targetFile) throws IOException, ArchiveException {
        if (StringUtils.isBlank(sourcePath)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "sourcePath"));
        }
        File parentFile = targetFile.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException(String.format(Tips.FAILED_TO_CREATE_FOLDER, parentFile));
        }
        Path path = Paths.get(sourcePath);
        int idx = path.getParent().toString().length();
        ArchiveStreamFactory asf = new ArchiveStreamFactory();
        String ext = FileUtils.getFileExtension(targetFile.getName());
        try (ArchiveOutputStream o = asf.createArchiveOutputStream(ext, new FileOutputStream(targetFile))) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Objects.requireNonNull(file);
                    Objects.requireNonNull(attrs);
                    String entryName = file.toString().substring(idx);
                    ArchiveEntry entry = o.createArchiveEntry(file.toFile(), entryName);
                    o.putArchiveEntry(entry);
                    try (InputStream inputStream = Files.newInputStream(file)) {
                        IOUtils.copy(inputStream, o);
                    }
                    o.closeArchiveEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
