package dao;

import com.google.gson.reflect.TypeToken;
import entity.*;
import util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据管理类，负责所有数据的读写操作
 */
public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.json";
    private static final String ADMINS_FILE = DATA_DIR + "/admins.json";
    private static final String COURTS_FILE = DATA_DIR + "/courts.json";
    private static final String RESERVATIONS_FILE = DATA_DIR + "/reservations.json";

    private static DataManager instance;
    private List<Student> students;
    private List<Admin> admins;
    private List<Court> courts;
    private List<Reservation> reservations;

    private DataManager() {
        loadAllData();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    /**
     * 加载所有数据
     */
    private void loadAllData() {
        try {
            Type studentType = new TypeToken<List<Student>>(){}.getType();
            students = JsonUtil.readFromFile(STUDENTS_FILE, studentType);

            Type adminType = new TypeToken<List<Admin>>(){}.getType();
            admins = JsonUtil.readFromFile(ADMINS_FILE, adminType);

            Type courtType = new TypeToken<List<Court>>(){}.getType();
            courts = JsonUtil.readFromFile(COURTS_FILE, courtType);

            Type reservationType = new TypeToken<List<Reservation>>(){}.getType();
            reservations = JsonUtil.readFromFile(RESERVATIONS_FILE, reservationType);
        } catch (IOException e) {
            System.err.println("加载数据失败: " + e.getMessage());
            students = new ArrayList<>();
            admins = new ArrayList<>();
            courts = new ArrayList<>();
            reservations = new ArrayList<>();
        }
    }

    /**
     * 保存所有数据
     */
    public void saveAllData() {
        try {
            JsonUtil.writeToFile(STUDENTS_FILE, students);
            JsonUtil.writeToFile(ADMINS_FILE, admins);
            JsonUtil.writeToFile(COURTS_FILE, courts);
            JsonUtil.writeToFile(RESERVATIONS_FILE, reservations);
        } catch (IOException e) {
            System.err.println("保存数据失败: " + e.getMessage());
        }
    }

    // ========== 学生相关操作 ==========
    public void addStudent(Student student) {
        students.add(student);
        saveAllData();
    }

    public Student findStudentById(String studentId) {
        return students.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst()
                .orElse(null);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    // ========== 管理员相关操作 ==========
    public void addAdmin(Admin admin) {
        admins.add(admin);
        saveAllData();
    }

    public Admin findAdminById(String adminId) {
        return admins.stream()
                .filter(a -> a.getAdminId().equals(adminId))
                .findFirst()
                .orElse(null);
    }

    // ========== 场地相关操作 ==========
    public void addCourt(Court court) {
        courts.add(court);
        saveAllData();
    }

    public Court findCourtById(String courtId) {
        return courts.stream()
                .filter(c -> c.getCourtId().equals(courtId))
                .findFirst()
                .orElse(null);
    }

    public List<Court> getAvailableCourts() {
        return courts.stream()
                .filter(c -> c.getStatus() == Court.CourtStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    public List<Court> getAllCourts() {
        return new ArrayList<>(courts);
    }

    public void updateCourt(Court court) {
        for (int i = 0; i < courts.size(); i++) {
            if (courts.get(i).getCourtId().equals(court.getCourtId())) {
                courts.set(i, court);
                saveAllData();
                return;
            }
        }
    }

    // ========== 预约相关操作 ==========
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        saveAllData();
    }

    public Reservation findReservationById(String reservationId) {
        return reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst()
                .orElse(null);
    }

    public List<Reservation> getReservationsByStudentId(String studentId) {
        return reservations.stream()
                .filter(r -> r.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public void updateReservation(Reservation reservation) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getReservationId().equals(reservation.getReservationId())) {
                reservations.set(i, reservation);
                saveAllData();
                return;
            }
        }
    }

    /**
     * 检查场地在指定时间段是否可用
     */
    public boolean isCourtAvailable(String courtId, java.time.LocalDateTime startTime, 
                                    java.time.LocalDateTime endTime) {
        return reservations.stream()
                .noneMatch(r -> r.getCourtId().equals(courtId)
                        && r.getStatus() != Reservation.ReservationStatus.CANCELLED
                        && isTimeOverlap(r.getStartTime(), r.getEndTime(), startTime, endTime));
    }

    private boolean isTimeOverlap(java.time.LocalDateTime start1, java.time.LocalDateTime end1,
                                  java.time.LocalDateTime start2, java.time.LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}

