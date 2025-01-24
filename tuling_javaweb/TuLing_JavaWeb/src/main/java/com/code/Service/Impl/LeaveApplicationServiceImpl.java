package com.code.Service.Impl;

import com.code.Service.LeaveApplicationService;
import com.code.mapper.LeaveApplicationMapper;
import com.code.pojo.LeaveApplication;
import com.code.Service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveApplicationServiceImpl implements LeaveApplicationService {

    private final LeaveApplicationMapper leaveApplicationMapper;

    @Autowired
    public LeaveApplicationServiceImpl(LeaveApplicationMapper leaveApplicationMapper) {
        this.leaveApplicationMapper = leaveApplicationMapper;
    }

    @Override
    public void sendLeaveApplication(LeaveApplication leaveApplication) {
        // 设置默认值
        leaveApplication.setApply_time(java.time.LocalDateTime.now());
        leaveApplication.setApproved_by_manager(0); // 默认未批准

        // 插入请假申请到数据库
        leaveApplicationMapper.insertLeaveApplication(leaveApplication);
    }

    /**
     * 根据员工ID获取请假申请
     * @param emp_id
     * @return
     */
    @Override
    public List<LeaveApplication> getLeaveApplicationByEmpId(Integer emp_id) {
        return leaveApplicationMapper.getLeaveApplicationByEmpId(emp_id);
    }


    @Override
    public List<LeaveApplication> getLeaveApplicationsByApprovalStatus(Integer approvedByManager) {
        // 只有当审批状态为0时才进行查询
        if (approvedByManager == null || approvedByManager != 0) {
            return List.of(); // 返回空列表或根据需求返回其他值
        }
        return leaveApplicationMapper.getLeaveApplicationsByApprovalStatus(approvedByManager);
    }

    @Override
    public List<LeaveApplication> getAllLeaveApplications() {
        return leaveApplicationMapper.getAllLeaveApplications(); // 假设此方法已存在或可以实现
    }

    /**
     *
     * @param id 请假申请ID
     */
    @Override
    public void approveLeaveApplication(Integer id) {
        LeaveApplication leaveApplication = leaveApplicationMapper.getLeaveApplicationById(id);
        if (leaveApplication == null || leaveApplication.getApproved_by_manager() != 0) {
            throw new IllegalArgumentException("无效或已处理的请假申请");
        }

        leaveApplication.setApproved_by_manager(1); // 设置为已批准
        leaveApplication.setApprove_time(LocalDateTime.now());
        leaveApplicationMapper.updateLeaveApplication(leaveApplication);
    }

    /**
     *
     * @param id 请假申请ID
     */

    @Override
    public void rejectLeaveApplication(Integer id) {
        LeaveApplication leaveApplication = leaveApplicationMapper.getLeaveApplicationById(id);
        if (leaveApplication == null || leaveApplication.getApproved_by_manager() != 0) {
            throw new IllegalArgumentException("无效或已处理的请假申请");
        }

        leaveApplication.setApproved_by_manager(2); // 设置为拒绝
        leaveApplication.setApprove_time(LocalDateTime.now());
        leaveApplicationMapper.updateLeaveApplication(leaveApplication);
    }
}