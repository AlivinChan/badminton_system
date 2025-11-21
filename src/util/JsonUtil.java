package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JSON工具类，用于读写JSON文件
 */
public class JsonUtil {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    /**
     * 将对象列表写入JSON文件
     */
    public static <T> void writeToFile(String filePath, List<T> data) throws IOException {
        File file = new File(filePath);
        // 确保父目录存在
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        }
    }

    /**
     * 从JSON文件读取对象列表
     */
    public static <T> List<T> readFromFile(String filePath, Type type) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new java.util.ArrayList<>();
        }
        
        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            List<T> list = gson.fromJson(reader, type);
            return list != null ? list : new java.util.ArrayList<>();
        }
    }

    /**
     * 将对象写入JSON文件
     */
    public static <T> void writeObjectToFile(String filePath, T object) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(object, writer);
        }
    }

    /**
     * 从JSON文件读取对象
     */
    public static <T> T readObjectFromFile(String filePath, Class<T> clazz) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        
        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clazz);
        }
    }
}

