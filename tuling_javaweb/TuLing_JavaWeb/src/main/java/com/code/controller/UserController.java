package com.code.controller;

import com.code.Service.AttendanceService;
import com.code.Service.LeaveApplicationService;
import com.code.Service.TrainingService;
import com.code.Service.UserService;
import com.code.pojo.*;
import com.code.utils.AliOSSUtils;
import com.code.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final LeaveApplicationService leaveApplicationService;
    private final TrainingService trainingService;
    private final AttendanceService attendanceService;
    @Autowired
    private View error;

    @Autowired
    public UserController(UserService userService, JwtUtils jwtUtils, LeaveApplicationService leaveApplicationService,
                          TrainingService trainingService,AttendanceService attendanceService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.leaveApplicationService = leaveApplicationService;
        this.trainingService = trainingService;
        this.attendanceService = attendanceService;
    }

    /**
     * 员工登录
     * @param user
     * @return
     */

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:5173") // 允许来自此地址的请求
    public Result login(@RequestBody User user) {
        log.info("员工登录");
        User u = userService.login(user);

        if (u != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", u.getId());
            claims.put("name", u.getUsername());
            claims.put("username", u.getUsername());

            String token = jwtUtils.generateJwt(claims);
            Map<String, String> data = new HashMap<>();
            data.put("token", token);

            return Result.success(data);  // 返回包含 token 的对象
        } else {
            return Result.error("登录失败");
        }
    }

    /**
     * 员工查看个人信息
     * @return
     */
    @GetMapping("/info")
    public Result getUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }


    /**
     * 员工修改个人信息
     */
    @PutMapping("/update")
    public Result updateUser(@RequestBody User user) {
        log.info("员工修改个人信息");

        userService.updateUser(user);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    /**
     * 员工上传头像
     */

    @Autowired
    private AliOSSUtils aliOSSUtils;
    @PostMapping("/upload/avatar")
    public Result uploadAvatar( MultipartFile image) throws IOException {
        log.info("员工上传头像");
        String url = aliOSSUtils.upload(image);
        log.info("文件上传成功,文件访问的URL是：{}",url);
        return Result.success(url);
    }


    @PatchMapping("/update/avatar")
    @CrossOrigin(origins = "http://localhost:5173") // 允许来自此地址的请求

    public Result updateAvatar(@RequestParam("image") String image, @RequestParam("id") Integer id) {
        log.info("开始处理员工修改头像请求");
        log.info("接收到的参数: image={}, id={}", image, id);

        try {
            log.info("调用服务层更新用户头像...");
            userService.updateUserAvatar(image, id);
            log.info("头像更新成功");
            return Result.success("修改成功");
        } catch (Exception e) {
            log.error("修改头像失败: {}", e.getMessage(), e);
            return Result.error("修改失败，请稍后再试");
        }
    }

    /**
     * 员工查看自己的工资
     */
    @GetMapping("/check/salary")
    public Result getSalary() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByUsername(username);

            if (user != null) {
                Salary salary = userService.getSalaryByEmpId(user.getId());
                return Result.success(salary);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("获取工资信息失败: {}", e.getMessage(), e);
            return Result.error("获取工资信息失败，请稍后再试");
        }
    }


    /**
     * 员工发送请假申请
     */
    @PostMapping("/send/application")
    public Result sendLeaveApplication(@RequestBody LeaveApplication leaveApplication) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByUsername(username);

            if (user != null) {
                leaveApplication.setEmp_id(user.getId()); // 设置请假申请的员工ID
                leaveApplicationService.sendLeaveApplication(leaveApplication); // 调用服务层的方法

                return Result.success("请假申请已成功提交");
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("提交请假申请失败: {}", e.getMessage(), e);
            return Result.error("提交请假申请失败，请稍后再试");
        }
    }

    /**
     * 员工查看自己的请假申请
     */
    @GetMapping("/check/application")
    public Result getLeaveApplication() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByUsername(username);

            if (user != null) {
                List<LeaveApplication> leaveApplications = leaveApplicationService.getLeaveApplicationByEmpId(user.getId());
                return Result.success(leaveApplications);
            } else {
                return Result.error("用户不存在");
            }
        } catch(Exception e) {
            log.error("获取请假申请失败: {}", e.getMessage(), e);
            return Result.error("获取请假申请失败，请稍后再试");
        }
    }

    /**
     * 员工查看所有培训活动
     */
    @GetMapping("/check/training/event")
    public Result getTrainingEvent() {
        try {
            List<TrainingEvent> trainingEvents = trainingService.getAllTrainingEvents();
            return Result.success(trainingEvents);
        } catch (Exception e) {
            log.error("获取培训活动失败: {}", e.getMessage(), e);
            return Result.error("获取培训活动失败，请稍后再试");
        }
    }

    @PostMapping("/enroll/training/event")
    public Result enrollTrainingEvent(@RequestParam("event_id") Integer event_id) {
        try {
            // 调用服务层的方法完成报名
            boolean success = trainingService.enrollTrainingEvent(event_id);
            if (success) {
                return Result.success("报名成功");
            } else {
                return Result.error("该活动已满员或报名失败");
            }
        } catch (Exception e) {
            log.error("报名失败: {}", e.getMessage(), e);
            return Result.error("报名失败，请稍后再试");
        }
    }

    /**
     * 员工完成签到
     * @param emp_id
     * @return JSON格式的响应
     */
    @PostMapping("/punch")
    public ResponseEntity<Map<String, Object>> punch(@RequestParam("emp_id") Integer emp_id) {
        log.info("员工完成签到");
        String result = attendanceService.punch(emp_id);

        Map<String, Object> response = new HashMap<>();
        if (result.equals("打卡成功")) {
            response.put("success", true);
            response.put("message", "打卡成功");
            response.put("is_late", false);
        } else if (result.equals("打卡成功，但已迟到")) {
            response.put("success", true);
            response.put("message", "打卡成功，但已迟到");
            response.put("is_late", true);
        } else {
            response.put("success", false);
            response.put("message", result); // 例如："您今天已经打过卡了"
        }

        return ResponseEntity.ok(response);
    }





}