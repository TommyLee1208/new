package com.code.controller;

import com.code.Service.AttendanceService;
import com.code.Service.LeaveApplicationService;
import com.code.Service.ManagerService;
import com.code.Service.TrainingService;
import com.code.pojo.*;
import com.code.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/manager")
@Validated
public class ManagerController {

    private final ManagerService managerService;
    private final JwtUtils jwtUtils;
    private final LeaveApplicationService leaveApplicationService;
    private final TrainingService trainingService;
    private final AttendanceService attendanceService;

    @Autowired
    public ManagerController(ManagerService managerService, JwtUtils jwtUtils, LeaveApplicationService leaveApplicationService,
                             TrainingService trainingService, AttendanceService attendanceService) {
        this.managerService = managerService;
        this.jwtUtils = jwtUtils;
        this.leaveApplicationService = leaveApplicationService;
        this.trainingService = trainingService;
        this.attendanceService = attendanceService;
    }

    @PostMapping("/login")
    public Result login(@RequestBody Manager manager) {
        log.info("管理员登录尝试：{}", manager.getManagername());
        Manager m = managerService.login(manager);

        if (m != null) {
            // 创建 Claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", m.getId());
            claims.put("managername", m.getManagername());
            claims.put("roles", Arrays.asList("ROLE_ADMIN")); // 添加角色信息
            claims.put("sub", m.getManagername()); // 使用 managername 或其他唯一标识符作为 subject

            // 管理员登陆成功，生成并下发JWT令牌
            String token = jwtUtils.generateJwt(claims);
            Map<String, String> data = new HashMap<>();
            data.put("token", token);

            return Result.success(data);  // 返回包含 token 的对象
        } else {
            log.warn("用户名或密码错误");
            return Result.error("用户名或密码错误");
        }
    }

    @GetMapping("/check/user/info")
    public Result getEmployeePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name, Integer gender,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            Integer dept_id) {

        log.info("分页查询员工信息，参数：{}, {},{},{},{},{}", page, pageSize, name, gender, begin, end);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            log.info("当前用户角色: {}", userDetails.getAuthorities());

            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                PageBean pageBean = managerService.page(page, pageSize, name, gender, begin, end, dept_id);
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("rows", pageBean.getRows());
                responseMap.put("total", pageBean.getTotal());

                return Result.success(responseMap);
            } else {
                log.warn("用户没有足够的权限访问资源");
                return Result.error("未授权访问");
            }
        } else {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
    }

    @DeleteMapping("/delete/user")
    public Result deleteUser(@RequestParam("ids") List<Integer> ids) {
        log.info("批量删除员工信息，参数：{}", ids);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            log.info("当前用户角色: {}", userDetails.getAuthorities());

            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                try {
                    managerService.deleteUser(ids);
                    return Result.success("删除成功！");
                } catch (Exception e) {
                    log.error("删除员工信息失败：", e);
                    return Result.error("删除失败：" + e.getMessage());
                }
            } else {
                log.warn("用户没有足够的权限访问资源");
                return Result.error("未授权访问");
            }
        } else {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
    }

    @PostMapping("/add/user")
    public Result addUser(@RequestBody User user) {
        log.info("新增员工信息，参数：{}", user);

        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        // 检查用户是否有足够的权限
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }

        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }

        try {
            // 调用服务层方法添加新用户
            Integer userId = managerService.addUser(user);
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);

            return Result.success("新增员工成功！");
        } catch (Exception e) {
            log.error("新增员工信息失败：", e);
            return Result.error("新增员工失败：" + e.getMessage());
        }
    }
    @PutMapping("/update/user")
    public Result updateUser(@RequestBody User user) {
        log.info("修改员工信息，参数：{}", user);
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        // 检查用户是否有足够的权限
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }

        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }

        try {
            managerService.updateUser(user);
            return Result.success("修改员工成功！");
        } catch (Exception e) {
            log.error("修改员工信息失败：", e);
            return Result.error("修改员工失败：" + e.getMessage());
        }
    }
    @PutMapping("/reset/password")
    public Result resetPassword(@RequestParam("ids") List<Integer> ids) {
        log.info("重置员工密码，参数：{}", ids);

        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        // 检查用户是否有足够的权限
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }

        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }
        try {
            managerService.resetPassword(ids);
            return Result.success("重置员工密码成功！");
        } catch (Exception e) {
            log.error("重置员工密码失败：", e);
            return Result.error("重置员工密码失败：" + e.getMessage());
        }

    }
    /**
     * 管理员查询员工工资
     */
    @GetMapping("/check/user/salary")
    public Result getSalary(@RequestParam("emp_id") Integer emp_id) {
        log.info("管理员查询员工工资");

        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        // 检查用户是否有足够的权限
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }

        try {
            PageBean<Salary> salaryPage = managerService.getSalaryByEmpId(emp_id);
            return Result.success(salaryPage);
        } catch (Exception e) {
            log.error("查询员工工资失败：", e);
            return Result.error("查询员工工资失败：" + e.getMessage());
        }
    }

    /**
     * 管理员调整员工基本工资
     */
    @PutMapping("/adjust/base/salary")
    public Result adjustBaseSalary(@RequestParam("emp_id") String emp_id,
                                   @RequestParam("base_salary") BigDecimal base_salary) {
        log.info("管理员调整员工基本工资: emp_id={}, base_salary={}", emp_id, base_salary);

        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        // 检查用户是否有足够的权限
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }

        try {
            managerService.adjustBaseSalary(emp_id, base_salary);
            return Result.success("调整员工基本工资成功！");
        } catch (Exception e) {
            log.error("调整员工基本工资失败：", e);
            return Result.error("调整员工基本工资失败：" + e.getMessage());
        }
    }

    /**
     * 管理员修改员工考勤工资
     */
    @PutMapping("/adjust/attendance/bonus")
    public Result adjustAttendanceBonus(@RequestParam("emp_id") String emp_id,
                                        @RequestParam("attendance_bonus") BigDecimal attendance_bonus) {
        log.info("管理员修改员工考勤工资: emp_id={}, attendance_bonus={}", emp_id, attendance_bonus);
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        // 检查用户是否有足够的权限
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }
        try {
            managerService.adjustAttendanceBonus(emp_id, attendance_bonus);
            return Result.success("调整员工考勤工资成功！");
        } catch (Exception e) {
            log.error("调整员工考勤工资失败：", e);
            return Result.error("调整员工考勤工资失败：" + e.getMessage());
        }
    }

    /**
     * 管理员查看所有员工的请假申请
     */
    @GetMapping("/check/user/sendApplication")
    public Result getAllLeaveApplications() {
        log.info("管理员查看所有员工的请假申请");

        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        // 检查用户是否有足够的权限
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }



        try {
            List<LeaveApplication> leaveApplicationList = leaveApplicationService.getAllLeaveApplications();

            // 如果没有找到任何请假申请，返回相应的提示信息
            if (leaveApplicationList.isEmpty()) {
                return Result.success("没有找到任何请假申请");
            }

            // 返回成功结果及请假申请列表
            return Result.success(leaveApplicationList);
        } catch (Exception e) {
            log.error("查询所有员工请假申请失败：", e);
            return Result.error("查询所有员工请假申请失败：" + e.getMessage());
        }
    }


    /**
     * 管理员查看所有未审批的请假申请
     */
    @GetMapping("/check/unapproved/application")
    public Result getUnapprovedLeaveApplications() {
        log.info("管理员查看所有未审批的请假申请");

        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        // 检查用户是否有足够的权限
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }


        try {
            Integer unapprovedStatus = 0; // 假设0表示未审批状态
            List<LeaveApplication> leaveApplicationList = leaveApplicationService.getLeaveApplicationsByApprovalStatus(unapprovedStatus);

            // 如果没有找到任何未审批的请假申请，返回相应的提示信息
            if (leaveApplicationList.isEmpty()) {
                return Result.success("没有找到任何未审批的请假申请");
            }

            // 返回成功结果及未审批的请假申请列表
            return Result.success(leaveApplicationList);
        } catch (Exception e) {
            log.error("查询未审批的员工请假申请失败：", e);
            return Result.error("查询未审批的员工请假申请失败：" + e.getMessage());
        }
    }

    /**
     * 管理员根据请假申请ID来审批员工请假申请(1批准)
     */
    @PutMapping("/approve")
    public Result approveLeaveApplication(@RequestParam("id") Integer leaveApplicationId) {
        log.info("管理员审批员工请假申请: id={}", leaveApplicationId);

        // 获取当前认证信息并检查权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }

        try {
            leaveApplicationService.approveLeaveApplication(leaveApplicationId);
            return Result.success("请假申请已批准");
        } catch (IllegalArgumentException e) {
            log.error("处理请假申请时出错: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员根据请假申请ID来审批员工请假申请(2不批准)
     */
    @PutMapping("/reject")
    public Result rejectLeaveApplication(@RequestParam("id") Integer leaveApplicationId) {
        log.info("管理员审批员工请假申请: id={}", leaveApplicationId);

        // 获取当前认证信息并检查权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }

        try {
            leaveApplicationService.rejectLeaveApplication(leaveApplicationId);
            return Result.success("请假申请已拒绝");
        } catch (IllegalArgumentException e) {
            log.error("处理请假申请时出错: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员发布培训活动
     */
    @PostMapping("/publish/training/event")
    public Result publishTrainingEvent(@RequestBody TrainingEvent trainingEvent) {
        log.info("管理员发布培训活动: {}", trainingEvent);

        // 获取当前认证信息并检查权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }
        try {
            trainingService.publishTrainingEvent(trainingEvent);
            return Result.success("培训活动发布成功");
        } catch (IllegalArgumentException e) {
            log.error("发布培训活动时出错: {}", e.getMessage());
            return Result.error(e.getMessage());
        }

    }

    /**
     * 管理员查看所有培训活动
     */
    @GetMapping("/check/training/event")
    public Result getAllTrainingEvents() {
        log.info("管理员查看所有培训活动");

        // 获取当前认证信息并检查权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }

        try {
            List<TrainingEvent> trainingEvents = trainingService.getAllTrainingEvents();
            return Result.success(trainingEvents);
        } catch (IllegalArgumentException e) {
            log.error("获取培训活动列表时出错: {}", e.getMessage());
            return Result.error(e.getMessage());
        }


    }

    /**
     * 管理员查看打卡表
     */
    @GetMapping("/check/user/punch")
    public Result getAllPunchRecords() {
        log.info("管理员查看打卡表");

        // 获取当前认证信息并检查权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未找到用户认证信息");
            return Result.error("未授权访问");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("认证主体不是 UserDetails 类型");
            return Result.error("未授权访问");
        }
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("用户没有足够的权限访问资源");
            return Result.error("未授权访问");
        }
        try {
            List<Attendance> attendanceList = attendanceService.getAllAttendanceRecords();
            return Result.success(attendanceList);
        } catch (IllegalArgumentException e) {
            log.error("获取打卡记录列表时出错: {}", e.getMessage());
            return Result.error(e.getMessage());
        }


    }



}
