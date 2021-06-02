package com.soon.utils;

import com.soon.utils.consts.Tips;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
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

    public static void decompressor(String sourceFile, String targetDir) throws IOException, ArchiveException {
        String ext = FileUtils.getFileExtension(sourceFile);
        Charset charset = FileUtils.getFileEncoding(sourceFile);
        if (ArchiveStreamFactory.ZIP.equals(ext)) {
            decompressorZip(sourceFile, targetDir, charset);
        } else {
            decompressor(sourceFile, targetDir, charset);
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
    public static void decompressor(String sourceFile, String targetDir, Charset charset) throws IOException, ArchiveException {
        Objects.requireNonNull(sourceFile, String.format(Tips.PARAMS_NOT_NULL, "sourceFile"));
        Objects.requireNonNull(sourceFile, String.format(Tips.PARAMS_NOT_NULL, "targetDir"));
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
    public static void decompressorZip(String sourceFile, String targetDir, Charset charset) throws IOException {
        Objects.requireNonNull(sourceFile, String.format(Tips.PARAMS_NOT_NULL, "sourceFile"));
        Objects.requireNonNull(sourceFile, String.format(Tips.PARAMS_NOT_NULL, "targetDir"));
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

    public static void main(String[] args) {
        try {
            decompressor("D:\\飞秋文件\\张述江(6C4B90F6BE70)\\utf-8.zip", "C:\\Users\\Dae\\Desktop\\test");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArchiveException e) {
            e.printStackTrace();
        }
    }

}
