package com.soon.utils;

import com.soon.utils.consts.Tips;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

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
     * @since 2021/4/21 16:17
     */
    public static void decompressor(String sourceFile, String targetDir) throws IOException, ArchiveException {
        Objects.requireNonNull(sourceFile, String.format(Tips.PARAMS_NOT_NULL, "sourceFile"));
        Objects.requireNonNull(sourceFile, String.format(Tips.PARAMS_NOT_NULL, "targetDir"));
        File archiveFile = new File(sourceFile);
        // 文件不存在，跳过
        if (!archiveFile.exists()) {
            return;
        }
        ArchiveStreamFactory factory = new ArchiveStreamFactory();
        try (ArchiveInputStream input = factory.createArchiveInputStream(new BufferedInputStream(new FileInputStream(sourceFile)))) {
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
                        throw new IOException("failed to create directory " + f);
                    }
                } else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("failed to create directory " + parent);
                    }
                    try (OutputStream o = Files.newOutputStream(f.toPath())) {
                        IOUtils.copy(input, o);
                    }
                }
            }
        }
    }

}
