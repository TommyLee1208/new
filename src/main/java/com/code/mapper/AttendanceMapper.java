package com.code.mapper;

import com.code.pojo.Attendance;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceMapper {

    /**
     * 根据员工id和当天日期查询当天的打卡记录
     * @param empId
     * @param localDate
     * @return
     */
    @Select("select * from attendance where emp_id = #{empId} and punch_time = #{localDate} and is_late = 0")
    Attendance findTodayAttendanceByEmpId(Integer empId, LocalDate localDate);


    /**
     * 插入打卡记录
     * @param attendance
     */
    @Insert("insert into attendance(emp_id, punch_time, scheduled_punch_time, is_late) VALUES(#{emp_id}, #{punch_time}, #{scheduled_punch_time},#{is_late})")
    void insert(Attendance attendance);


    /**
     * 查询所有打卡记录
     * @return
     */
    @Select("select * from attendance")
    List<Attendance> getAllAttendanceRecords();

}
