package com.code.Service;

import com.code.pojo.Manager;
import com.code.pojo.PageBean;
import com.code.pojo.Salary;
import com.code.pojo.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public interface ManagerService {

    /**
     * 管理员登录
     * @param manager
     * @return
     */
    Manager login(Manager manager);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    PageBean page(Integer page, Integer pageSize, String name, Integer gender, LocalDate begin, LocalDate end, Integer dept_id);

    /**
     * 批量删除操作
     * @param ids
     */
    void deleteUser(List<Integer> ids);

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    Integer addUser(User user);

    /**
     * 更新员工
     * @param user
     */
    void updateUser(User user);

    /**
     * 批量重置密码
     * @param ids
     */
    void resetPassword(List<Integer> ids);


    /**
     * 根据员工id查询工资
     * @param emp_id
     * @return
     */
    PageBean getSalaryByEmpId(Integer emp_id);

    /**
     * 调整员工基本工资
     * @param
     */
    void adjustBaseSalary(String emp_id, BigDecimal base_salary);


    /**
     * 调整员工考勤奖金
     * @param
     */

    void adjustAttendanceBonus(String emp_id, BigDecimal full_attendance_bonus);
}