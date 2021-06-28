package com.soon.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * 控制台执行工具
 *
 * @author HuYiGong
 * @since 2021/6/28
 **/
public class ConsoleExecutor {
    private ConsoleExecutor() {}

    /**
     * 执行命令
     *
     * @param cmd 命令
     * @return java.lang.String 控制台日志
     * @author HuYiGong
     * @since 2021/6/28 11:46
     */
    public static String execute(String cmd) throws IOException {
        Process process = Runtime.getRuntime().exec(cmd);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line = "";
            while(!Objects.isNull(line)) {
                line = reader.readLine();
                sb.append(line);
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) throws IOException {
        String log = execute("java -version");
        System.out.println(log);
    }
}
