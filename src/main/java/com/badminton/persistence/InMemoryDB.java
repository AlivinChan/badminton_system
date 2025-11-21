package com.badminton.persistence;

import com.badminton.model.*;

import java.io.*;
import java.util.UUID;

/**
 * 内存数据库类，使用对象数组存储数据
 */
public class InMemoryDB implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "data.db";
    private static final int INITIAL_CAPACITY = 10;
    private static final double GROWTH_FACTOR = 1.5;

    private Student[] students;
    private Admin[] admins;
    private Court[] courts;
    private Booking[] bookings;

    private int studentCount;
    private int adminCount;
    private int courtCount;
    private int bookingCount;

    public InMemoryDB() {
        students = new Student[INITIAL_CAPACITY];
        admins = new Admin[INITIAL_CAPACITY];
        courts = new Court[INITIAL_CAPACITY];
        bookings = new Booking[INITIAL_CAPACITY];
        studentCount = 0;
        adminCount = 0;
        courtCount = 0;
        bookingCount = 0;
    }

    // ========== Student 操作 ==========
    public void addStudent(Student student) {
        if (studentCount >= students.length) {
            resizeStudentArray();
        }
        students[studentCount++] = student;
    }

    public Student findStudentById(String studentId) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i] != null && students[i].getStudentId().equals(studentId)) {
                return students[i];
            }
        }
        return null;
    }

    public Student[] getStudents() {
        Student[] result = new Student[studentCount];
        System.arraycopy(students, 0, result, 0, studentCount);
        return result;
    }

    public int getStudentCount() {
        return studentCount;
    }

    private void resizeStudentArray() {
        int newSize = (int) (students.length * GROWTH_FACTOR) + 1;
        Student[] newArray = new Student[newSize];
        System.arraycopy(students, 0, newArray, 0, studentCount);
        students = newArray;
    }

    // ========== Admin 操作 ==========
    public void addAdmin(Admin admin) {
        if (adminCount >= admins.length) {
            resizeAdminArray();
        }
        admins[adminCount++] = admin;
    }

    public Admin findAdminById(String adminId) {
        for (int i = 0; i < adminCount; i++) {
            if (admins[i] != null && admins[i].getAdminId().equals(adminId)) {
                return admins[i];
            }
        }
        return null;
    }

    public Admin[] getAdmins() {
        Admin[] result = new Admin[adminCount];
        System.arraycopy(admins, 0, result, 0, adminCount);
        return result;
    }

    public int getAdminCount() {
        return adminCount;
    }

    private void resizeAdminArray() {
        int newSize = (int) (admins.length * GROWTH_FACTOR) + 1;
        Admin[] newArray = new Admin[newSize];
        System.arraycopy(admins, 0, newArray, 0, adminCount);
        admins = newArray;
    }

    // ========== Court 操作 ==========
    public void addCourt(Court court) {
        if (courtCount >= courts.length) {
            resizeCourtArray();
        }
        courts[courtCount++] = court;
    }

    public Court findCourtById(String courtId) {
        for (int i = 0; i < courtCount; i++) {
            if (courts[i] != null && courts[i].getCourtId().equals(courtId)) {
                return courts[i];
            }
        }
        return null;
    }

    public Court[] getCourts() {
        Court[] result = new Court[courtCount];
        System.arraycopy(courts, 0, result, 0, courtCount);
        return result;
    }

    public int getCourtCount() {
        return courtCount;
    }

    private void resizeCourtArray() {
        int newSize = (int) (courts.length * GROWTH_FACTOR) + 1;
        Court[] newArray = new Court[newSize];
        System.arraycopy(courts, 0, newArray, 0, courtCount);
        courts = newArray;
    }

    // ========== Booking 操作 ==========
    public void addBooking(Booking booking) {
        if (bookingCount >= bookings.length) {
            resizeBookingArray();
        }
        bookings[bookingCount++] = booking;
    }

    public Booking findBookingById(String bookingId) {
        for (int i = 0; i < bookingCount; i++) {
            if (bookings[i] != null && bookings[i].getBookingId().equals(bookingId)) {
                return bookings[i];
            }
        }
        return null;
    }

    public Booking[] getBookings() {
        Booking[] result = new Booking[bookingCount];
        System.arraycopy(bookings, 0, result, 0, bookingCount);
        return result;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    private void resizeBookingArray() {
        int newSize = (int) (bookings.length * GROWTH_FACTOR) + 1;
        Booking[] newArray = new Booking[newSize];
        System.arraycopy(bookings, 0, newArray, 0, bookingCount);
        bookings = newArray;
    }

    // ========== 持久化操作 ==========
    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
        } catch (NotSerializableException e) {
            System.err.println("保存数据失败: 对象不可序列化 - " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("保存数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static InMemoryDB loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new InMemoryDB();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            return (InMemoryDB) ois.readObject();
        } catch (NotSerializableException e) {
            System.err.println("加载数据失败: 对象不可序列化 - " + e.getMessage());
            System.err.println("提示: 可能是旧版本的数据文件，将创建新的数据库");
            // 备份旧文件
            File backupFile = new File(DATA_FILE + ".backup");
            if (file.exists() && !backupFile.exists()) {
                file.renameTo(backupFile);
            }
            return new InMemoryDB();
        } catch (ClassNotFoundException e) {
            System.err.println("加载数据失败: 类未找到 - " + e.getMessage());
            System.err.println("提示: 可能是旧版本的数据文件，将创建新的数据库");
            // 备份旧文件
            File backupFile = new File(DATA_FILE + ".backup");
            if (file.exists() && !backupFile.exists()) {
                file.renameTo(backupFile);
            }
            return new InMemoryDB();
        } catch (IOException e) {
            System.err.println("加载数据失败: " + e.getMessage());
            // 如果是序列化相关的错误，备份旧文件
            if (e.getMessage() != null && e.getMessage().contains("NotSerializableException")) {
                System.err.println("提示: 可能是旧版本的数据文件，将创建新的数据库");
                File backupFile = new File(DATA_FILE + ".backup");
                if (file.exists() && !backupFile.exists()) {
                    file.renameTo(backupFile);
                }
            }
            return new InMemoryDB();
        }
    }

    /**
     * 生成唯一的预约ID
     */
    public String generateBookingId() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

