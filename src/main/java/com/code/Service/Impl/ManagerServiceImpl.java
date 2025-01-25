package com.code.Service.Impl;

import com.code.mapper.ManagerMapper;
import com.code.mapper.UserMapper;
import com.code.pojo.Manager;
import com.code.pojo.PageBean;
import com.code.pojo.Salary;
import com.code.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class ManagerServiceImpl implements com.code.Service.ManagerService {

    @Autowired
    private ManagerMapper managerMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Manager login(Manager manager) {
        return managerMapper.getByManagernameAndPassword(manager);
    }

    @Override
    public PageBean page(Integer page, Integer pageSize, String name, Integer gender, LocalDate begin, LocalDate end, Integer dept_id) {
        // 1. 查询总记录数
        Long count = managerMapper.count();
        // 2. 计算开始位置
        int start = (page - 1) * pageSize;
        // 3. 获取分页查询结果列表
        List<User> rows = managerMapper.page(start, pageSize, name,  gender,  begin
                , end , dept_id);
        // 4. 封装 PageBean 对象并返回
        PageBean pageBean = new PageBean(count, rows);
        return pageBean;
    }

    /**
     * 根据ids来完成批量删除操作
     * @param ids
     */
    @Override
    public void deleteUser(List<Integer> ids) {
        managerMapper.deleteUser(ids);
    }

    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    @Override
    public Integer addUser(User user) {
        //将创建时间和更新时间设置为当前时间
        user.setCreate_time(LocalDateTime.now());
        user.setUpdate_time(LocalDateTime.now());
        managerMapper.insert(user);
        return null;
    }


    /**
     * 更新修改员工数据
     * @param user
     */

    @Override
    public void updateUser(User user) {
        user.setUpdate_time(LocalDateTime.now());
        managerMapper.updateUser(user);

    }

    @Override
    public void resetPassword(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }

        String rawPassword = "123456"; // 新密码（明文）

        // 将参数封装到Map中
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        params.put("rawPassword", rawPassword);

        // 更新员工密码为明文的新密码
        managerMapper.ResetPassword(params);
    }


    /**
     * 根据员工id查询员工工资信息
     * @param emp_id
     * @return
     */
    @Override
    public PageBean<Salary> getSalaryByEmpId(Integer emp_id) {

        List<Salary> rows = managerMapper.getSalariesByEmpId(emp_id);
        return new PageBean<>(Long.valueOf(rows.size()), rows);
    }

    /**
     * 调整员工基本工资
     * @param emp_id
     * @param base_salary
     */
    @Override
    public void adjustBaseSalary(String emp_id, BigDecimal base_salary) {
        try {
            // 尝试将字符串类型的empId转换为整数类型
            Integer empIdInt = Integer.parseInt(emp_id.trim());

            // 更新员工的基本工资
            int updatedRows = managerMapper.updateBaseSalary(empIdInt, base_salary);
            if (updatedRows == 0) {
                log.warn("未找到员工ID为 " + emp_id + " 的工资记录");
                throw new RuntimeException("未找到员工ID为 " + emp_id + " 的工资记录");
            }

            log.info("成功更新员工ID为 " + emp_id + " 的基本工资至 " + base_salary);
        } catch (NumberFormatException e) {
            log.error("无效的员工ID: " + emp_id, e);
            throw new IllegalArgumentException("无效的员工ID: " + emp_id, e);
        } catch (Exception e) {
            log.error("调整员工基本工资失败", e);
            throw new RuntimeException("调整员工基本工资失败", e);
        }
    }

    /**
     * 调整员工满勤奖金
     *
     * @param emp_id                员工ID（字符串形式）
     * @param fullAttendanceBonus   新的考勤奖金金额
     */
    public void adjustAttendanceBonus(String emp_id, BigDecimal fullAttendanceBonus) {
        try {
            // 尝试将字符串类型的empId转换为整数类型
            Integer empIdInt = Integer.parseInt(emp_id.trim());

            // 检查并限制考勤奖金的最大最小值
            if (fullAttendanceBonus.compareTo(BigDecimal.ZERO) < 0 ||
                    fullAttendanceBonus.compareTo(new BigDecimal("3000")) > 0) {
                throw new IllegalArgumentException("考勤奖金必须在0到3000之间");
            }

            // 更新员工的考勤奖金
            int updatedRows = managerMapper.updateAttendanceBonus(empIdInt, fullAttendanceBonus);
            if (updatedRows == 0) {
                log.warn("未找到员工ID为 " + emp_id + " 的工资记录或更新失败");
                throw new RuntimeException("未找到员工ID为 " + emp_id + " 的工资记录或更新失败");
            }

            log.info("成功调整员工ID为 " + emp_id + " 的考勤奖金至 " + fullAttendanceBonus);

        } catch (NumberFormatException e) {
            log.error("无效的员工ID: " + emp_id, e);
            throw new IllegalArgumentException("无效的员工ID: " + emp_id, e);
        } catch (Exception e) {
            log.error("调整员工考勤工资失败", e);
            throw new RuntimeException("调整员工考勤工资失败", e);
        }
    }
}


