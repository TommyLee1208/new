package com.code.Service;

import com.code.pojo.LeaveApplication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LeaveApplicationService {


    /**
     * 发送员工的请假申请
     * @param leaveApplication 包含请假信息的对象
     */
    void sendLeaveApplication(LeaveApplication leaveApplication);


    /**
     * 查看请假申请
     * @param emp_id
     * @return
     */
    List<LeaveApplication> getLeaveApplicationByEmpId(Integer emp_id);



    /**
     * 根据审批状态查看所有请假申请
     * @param approvedByManager 审批状态
     * @return
     */
    List<LeaveApplication> getLeaveApplicationsByApprovalStatus(Integer approvedByManager);

    /**
     * 查看所有请假申请
     * @return
     */
    List<LeaveApplication> getAllLeaveApplications();


    /**
     * 管理员根据id来审批员工请假申请(批准)
     * @param id 请假申请ID
     */
    void approveLeaveApplication(Integer id);

    /**
     * 管理员根据id来审批员工请假申请(不批准)
     * @param id 请假申请ID
     */
    void rejectLeaveApplication(Integer id);

}
