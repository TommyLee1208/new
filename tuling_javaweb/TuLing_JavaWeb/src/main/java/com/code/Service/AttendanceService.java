package com.code.Service;

import com.code.pojo.Attendance;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AttendanceService {
    /**
     * 员工完成签到
     * @param empId
     * @return
     */

    String punch(Integer empId);


    /**
     * 管理员查询所有签到记录
     * @return
     */
    List<Attendance> getAllAttendanceRecords();

}
