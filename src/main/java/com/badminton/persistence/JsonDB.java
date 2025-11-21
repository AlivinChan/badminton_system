package com.badminton.persistence;

import com.badminton.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JSON数据库类，使用JSON文件存储数据（不依赖外部库）
 */
public class JsonDB {
    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + File.separator + "students.json";
    private static final String ADMINS_FILE = DATA_DIR + File.separator + "admins.json";
    private static final String COURTS_FILE = DATA_DIR + File.separator + "courts.json";
    private static final String BOOKINGS_FILE = DATA_DIR + File.separator + "bookings.json";
    
    private List<Student> students;
    private List<Admin> admins;
    private List<Court> courts;
    private List<Booking> bookings;

    public JsonDB() {
        students = new ArrayList<>();
        admins = new ArrayList<>();
        courts = new ArrayList<>();
        bookings = new ArrayList<>();
        
        // 确保data目录存在
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    // ========== Student 操作 ==========
    public void addStudent(Student student) {
        if (findStudentById(student.getStudentId()) == null) {
            students.add(student);
            saveStudents();
        }
    }

    public Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }

    public Student[] getStudents() {
        return students.toArray(new Student[0]);
    }

    public int getStudentCount() {
        return students.size();
    }

    // ========== Admin 操作 ==========
    public void addAdmin(Admin admin) {
        if (findAdminById(admin.getAdminId()) == null) {
            admins.add(admin);
            saveAdmins();
        }
    }

    public Admin findAdminById(String adminId) {
        for (Admin admin : admins) {
            if (admin.getAdminId().equals(adminId)) {
                return admin;
            }
        }
        return null;
    }

    public Admin[] getAdmins() {
        return admins.toArray(new Admin[0]);
    }

    public int getAdminCount() {
        return admins.size();
    }

    // ========== Court 操作 ==========
    public void addCourt(Court court) {
        if (findCourtById(court.getCourtId()) == null) {
            courts.add(court);
            saveCourts();
        }
    }

    public Court findCourtById(String courtId) {
        for (Court court : courts) {
            if (court.getCourtId().equals(courtId)) {
                return court;
            }
        }
        return null;
    }

    public Court[] getCourts() {
        return courts.toArray(new Court[0]);
    }

    public int getCourtCount() {
        return courts.size();
    }

    // ========== Booking 操作 ==========
    public void addBooking(Booking booking) {
        if (findBookingById(booking.getBookingId()) == null) {
            bookings.add(booking);
            saveBookings();
        }
    }

    public Booking findBookingById(String bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }
        return null;
    }

    public Booking[] getBookings() {
        return bookings.toArray(new Booking[0]);
    }

    public int getBookingCount() {
        return bookings.size();
    }

    // ========== 持久化操作 ==========
    private void saveStudents() {
        try {
            writeListToFile(STUDENTS_FILE, students, "students");
        } catch (IOException e) {
            System.err.println("保存学生数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveAdmins() {
        try {
            writeListToFile(ADMINS_FILE, admins, "admins");
        } catch (IOException e) {
            System.err.println("保存管理员数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveCourts() {
        try {
            writeListToFile(COURTS_FILE, courts, "courts");
        } catch (IOException e) {
            System.err.println("保存场地数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveBookings() {
        try {
            writeListToFile(BOOKINGS_FILE, bookings, "bookings");
        } catch (IOException e) {
            System.err.println("保存预约数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveToFile() {
        saveStudents();
        saveAdmins();
        saveCourts();
        saveBookings();
    }

    private <T> void writeListToFile(String filePath, List<T> data, String type) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("[");
            for (int i = 0; i < data.size(); i++) {
                T item = data.get(i);
                String json = toJson(item, type);
                writer.print("  " + json);
                if (i < data.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println("]");
        }
    }

    private <T> String toJson(T obj, String type) {
        if (obj == null) return "null";
        
        if (type.equals("students")) {
            return toJsonStudent((Student) obj);
        } else if (type.equals("admins")) {
            return toJsonAdmin((Admin) obj);
        } else if (type.equals("courts")) {
            return toJsonCourt((Court) obj);
        } else if (type.equals("bookings")) {
            return toJsonBooking((Booking) obj);
        }
        return "{}";
    }

    private String toJsonStudent(Student student) {
        return String.format("{\n    \"studentId\": \"%s\",\n    \"name\": \"%s\",\n    \"phone\": \"%s\"\n  }",
                escapeJson(student.getStudentId()),
                escapeJson(student.getName()),
                escapeJson(student.getPhone()));
    }

    private String toJsonAdmin(Admin admin) {
        return String.format("{\n    \"adminId\": \"%s\",\n    \"name\": \"%s\",\n    \"phone\": \"%s\",\n    \"password\": \"%s\"\n  }",
                escapeJson(admin.getAdminId()),
                escapeJson(admin.getName()),
                escapeJson(admin.getPhone()),
                escapeJson(admin.getPassword()));
    }

    private String toJsonCourt(Court court) {
        return String.format("{\n    \"courtId\": \"%s\",\n    \"type\": \"%s\",\n    \"status\": \"%s\",\n    \"baseScore\": %.2f\n  }",
                escapeJson(court.getCourtId()),
                court.getType().name(),
                court.getStatus().name(),
                court.getBaseScore());
    }

    private String toJsonBooking(Booking booking) {
        Student student = booking.getStudent();
        TimeSlot slot = booking.getSlot();
        String studentJson = student != null ? toJsonStudent(student) : "null";
        String slotJson = slot != null ? toJsonTimeSlot(slot) : "null";
        String createdAtStr = booking.getCreatedAt() != null ? 
                "\"" + booking.getCreatedAt().toString() + "\"" : "null";
        
        return String.format("{\n    \"bookingId\": \"%s\",\n    \"student\": %s,\n    \"courtId\": \"%s\",\n    \"slot\": %s,\n    \"state\": \"%s\",\n    \"fee\": %.2f,\n    \"rating\": %d,\n    \"createdAt\": %s\n  }",
                escapeJson(booking.getBookingId()),
                studentJson,
                escapeJson(booking.getCourtId()),
                slotJson,
                booking.getState().name(),
                booking.getFee(),
                booking.getRating(),
                createdAtStr);
    }

    private String toJsonTimeSlot(TimeSlot slot) {
        return String.format("{\n      \"date\": \"%s\",\n      \"start\": \"%s\",\n      \"end\": \"%s\"\n    }",
                slot.getDate().toString(),
                slot.getStart().toString(),
                slot.getEnd().toString());
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static JsonDB loadFromFile() {
        JsonDB db = new JsonDB();
        
        // 加载学生数据
        db.students = db.readStudentsFromFile(STUDENTS_FILE);
        
        // 加载管理员数据
        db.admins = db.readAdminsFromFile(ADMINS_FILE);
        
        // 加载场地数据
        db.courts = db.readCourtsFromFile(COURTS_FILE);
        
        // 加载预约数据
        db.bookings = db.readBookingsFromFile(BOOKINGS_FILE);
        
        return db;
    }

    private List<Student> readStudentsFromFile(String filePath) {
        List<Student> result = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return result;
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String content = readAll(reader);
            if (content == null || content.trim().isEmpty()) {
                return result;
            }
            
            // 简单的JSON解析 - 查找所有对象
            int start = content.indexOf('[');
            if (start == -1) return result;
            
            int pos = start + 1;
            while (pos < content.length()) {
                int objStart = content.indexOf('{', pos);
                if (objStart == -1) break;
                
                int objEnd = findMatchingBrace(content, objStart);
                if (objEnd == -1) break;
                
                String objJson = content.substring(objStart, objEnd + 1);
                Student student = parseStudent(objJson);
                if (student != null) {
                    result.add(student);
                }
                
                pos = objEnd + 1;
            }
        } catch (IOException e) {
            System.err.println("读取学生数据失败: " + e.getMessage());
        }
        
        return result;
    }

    private List<Admin> readAdminsFromFile(String filePath) {
        List<Admin> result = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return result;
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String content = readAll(reader);
            if (content == null || content.trim().isEmpty()) {
                return result;
            }
            
            // 简单的JSON解析 - 查找所有对象
            int start = content.indexOf('[');
            if (start == -1) return result;
            
            int pos = start + 1;
            while (pos < content.length()) {
                int objStart = content.indexOf('{', pos);
                if (objStart == -1) break;
                
                int objEnd = findMatchingBrace(content, objStart);
                if (objEnd == -1) break;
                
                String objJson = content.substring(objStart, objEnd + 1);
                Admin admin = parseAdmin(objJson);
                if (admin != null) {
                    result.add(admin);
                }
                
                pos = objEnd + 1;
            }
        } catch (IOException e) {
            System.err.println("读取管理员数据失败: " + e.getMessage());
        }
        
        return result;
    }

    private List<Court> readCourtsFromFile(String filePath) {
        List<Court> result = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return result;
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String content = readAll(reader);
            if (content == null || content.trim().isEmpty()) {
                return result;
            }
            
            // 简单的JSON解析 - 查找所有对象
            int start = content.indexOf('[');
            if (start == -1) return result;
            
            int pos = start + 1;
            while (pos < content.length()) {
                int objStart = content.indexOf('{', pos);
                if (objStart == -1) break;
                
                int objEnd = findMatchingBrace(content, objStart);
                if (objEnd == -1) break;
                
                String objJson = content.substring(objStart, objEnd + 1);
                Court court = parseCourt(objJson);
                if (court != null) {
                    result.add(court);
                }
                
                pos = objEnd + 1;
            }
        } catch (IOException e) {
            System.err.println("读取场地数据失败: " + e.getMessage());
        }
        
        return result;
    }

    private List<Booking> readBookingsFromFile(String filePath) {
        List<Booking> result = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return result;
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String content = readAll(reader);
            if (content == null || content.trim().isEmpty()) {
                return result;
            }
            
            // 简单的JSON解析 - 查找所有对象
            int start = content.indexOf('[');
            if (start == -1) return result;
            
            int pos = start + 1;
            while (pos < content.length()) {
                int objStart = content.indexOf('{', pos);
                if (objStart == -1) break;
                
                int objEnd = findMatchingBrace(content, objStart);
                if (objEnd == -1) break;
                
                String objJson = content.substring(objStart, objEnd + 1);
                Booking booking = parseBooking(objJson);
                if (booking != null) {
                    result.add(booking);
                }
                
                pos = objEnd + 1;
            }
        } catch (IOException e) {
            System.err.println("读取预约数据失败: " + e.getMessage());
        }
        
        return result;
    }

    private String readAll(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private int findMatchingBrace(String str, int start) {
        int count = 0;
        for (int i = start; i < str.length(); i++) {
            if (str.charAt(i) == '{') count++;
            if (str.charAt(i) == '}') {
                count--;
                if (count == 0) return i;
            }
        }
        return -1;
    }

    private Student parseStudent(String json) {
        try {
            String studentId = extractString(json, "studentId");
            String name = extractString(json, "name");
            String phone = extractString(json, "phone");
            return new Student(studentId, name, phone);
        } catch (Exception e) {
            System.err.println("解析学生数据失败: " + e.getMessage());
            return null;
        }
    }

    private Admin parseAdmin(String json) {
        try {
            String adminId = extractString(json, "adminId");
            String name = extractString(json, "name");
            String phone = extractString(json, "phone");
            String password = extractString(json, "password");
            return new Admin(adminId, name, phone, password);
        } catch (Exception e) {
            System.err.println("解析管理员数据失败: " + e.getMessage());
            return null;
        }
    }

    private Court parseCourt(String json) {
        try {
            String courtId = extractString(json, "courtId");
            String typeStr = extractString(json, "type");
            String statusStr = extractString(json, "status");
            double baseScore = extractDouble(json, "baseScore");
            
            CourtType type = CourtType.valueOf(typeStr);
            CourtStatus status = CourtStatus.valueOf(statusStr);
            return new Court(courtId, type, status, baseScore);
        } catch (Exception e) {
            System.err.println("解析场地数据失败: " + e.getMessage());
            return null;
        }
    }

    private Booking parseBooking(String json) {
        try {
            String bookingId = extractString(json, "bookingId");
            String courtId = extractString(json, "courtId");
            String stateStr = extractString(json, "state");
            double fee = extractDouble(json, "fee");
            int rating = extractInt(json, "rating");
            
            // 解析student
            int studentStart = json.indexOf("\"student\":");
            Student student = null;
            if (studentStart != -1) {
                int objStart = json.indexOf('{', studentStart);
                if (objStart != -1) {
                    int objEnd = findMatchingBrace(json, objStart);
                    if (objEnd != -1) {
                        String studentJson = json.substring(objStart, objEnd + 1);
                        student = parseStudent(studentJson);
                    }
                }
            }
            
            // 解析slot
            int slotStart = json.indexOf("\"slot\":");
            TimeSlot slot = null;
            if (slotStart != -1) {
                int objStart = json.indexOf('{', slotStart);
                if (objStart != -1) {
                    int objEnd = findMatchingBrace(json, objStart);
                    if (objEnd != -1) {
                        String slotJson = json.substring(objStart, objEnd + 1);
                        slot = parseTimeSlot(slotJson);
                    }
                }
            }
            
            // 解析createdAt
            LocalDateTime createdAt = null;
            String createdAtStr = extractString(json, "createdAt");
            if (createdAtStr != null && !createdAtStr.isEmpty()) {
                createdAt = LocalDateTime.parse(createdAtStr);
            }
            
            BookingState state = BookingState.valueOf(stateStr);
            return new Booking(bookingId, student, courtId, slot, state, fee, rating, createdAt);
        } catch (Exception e) {
            System.err.println("解析预约数据失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private TimeSlot parseTimeSlot(String json) {
        try {
            String dateStr = extractString(json, "date");
            String startStr = extractString(json, "start");
            String endStr = extractString(json, "end");
            
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime start = LocalTime.parse(startStr);
            LocalTime end = LocalTime.parse(endStr);
            return new TimeSlot(date, start, end);
        } catch (Exception e) {
            System.err.println("解析时段数据失败: " + e.getMessage());
            return null;
        }
    }

    private String extractString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return unescapeJson(m.group(1));
        }
        return null;
    }

    private double extractDouble(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([0-9]+\\.?[0-9]*)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        return 0.0;
    }

    private int extractInt(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([0-9]+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    private String unescapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    /**
     * 生成唯一的预约ID
     */
    public String generateBookingId() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
