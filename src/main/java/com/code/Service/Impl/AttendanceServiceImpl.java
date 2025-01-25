package com.code.Service.Impl;

import com.code.Service.AttendanceService;
import com.code.mapper.AttendanceMapper;
import com.code.pojo.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService{

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public String punch(Integer emp_id) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();

        // 查询该员工当天是否已经打卡
        Attendance attendance = attendanceMapper.findTodayAttendanceByEmpId(emp_id, now.toLocalDate());

        if (attendance != null) {
            return "您今天已经打过卡了";
        }

        // 如果没有打卡，则插入新的打卡记录
        attendance = new Attendance();
        attendance.setEmp_id(emp_id);
        attendance.setPunch_time(now);
        attendance.setScheduled_punch_time(LocalTime.parse("09:00:00", DateTimeFormatter.ofPattern("HH:mm:ss")));
        attendance.setIs_late(currentTime.isAfter(attendance.getScheduled_punch_time()));
        attendanceMapper.insert(attendance);

        return attendance.getIs_late() ? "打卡成功，但已迟到" : "打卡成功";
    }

    /**
     * 管理员查询所有打卡记录
     * @return
     */
    @Override
    public List<Attendance> getAllAttendanceRecords() {
        return attendanceMapper.getAllAttendanceRecords();


    }
}
