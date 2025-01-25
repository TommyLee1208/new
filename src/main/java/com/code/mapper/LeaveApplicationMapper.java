package com.code.mapper;

import com.code.pojo.LeaveApplication;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface LeaveApplicationMapper {

    /**
     * 插入一条新的请假申请记录到数据库。
     *
     * @param leaveApplication 包含请假信息的对象
     */
    //@Insert("")
    void insertLeaveApplication(LeaveApplication leaveApplication);

    /**
     * 根据员工 ID 查询该员工所有的请假申请记录。
     *
     * @param empId 员工 ID
     * @return 包含该员工所有请假申请记录的列表
     */
    List<LeaveApplication> getLeaveApplicationByEmpId(Integer empId);

    /**
     * 根据审批状态来查询请假申请记录。
     *
     * @param approvedByManager 审批状态
     * @return 包含所有具有指定审批状态的请假申请记录的列表
     */
    @Select("select * from leave_application WHERE approved_by_manager = #{approved_by_manager}")
    List<LeaveApplication> getLeaveApplicationsByApprovalStatus(Integer approvedByManager);

    /**
     * 查询所有请假申请记录。
     *
     * @return 包含所有请假申请记录的列表
     */
    @Select("select * from leave_application")
    List<LeaveApplication> getAllLeaveApplications();


    /**
     * 更新一条请假申请记录到数据库。
     *
     * @param leaveApplication 包含更新后的请假信息的对象
     */
    @Update("UPDATE leave_application SET approved_by_manager=#{approved_by_manager}, " +
            "approve_time=#{approve_time} WHERE id=#{id}")
    void updateLeaveApplication(LeaveApplication leaveApplication);

    /**
     * 根据请假申请记录的 ID 查询该条记录。
     * @param id
     * @return
     */
    @Select("select * from leave_application where id=#{id}")
    LeaveApplication getLeaveApplicationById(Integer id);
}