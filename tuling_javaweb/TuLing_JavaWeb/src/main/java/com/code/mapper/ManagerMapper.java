package com.code.mapper;

import com.code.pojo.Manager;
import com.code.pojo.Salary;
import com.code.pojo.User;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ManagerMapper {

    /**
     * 根据管理员名和密码查询管理员信息
     */
    @Select("select * from managers where managername = #{managername} and managerpassword = #{managerpassword}")
    Manager getByManagernameAndPassword(Manager manager);

    /**
     * 查询总记录数
     */
    @Select("select count(*) from emp")
    public Long count();

    /**
     * 分页查询获取列表数据
     * @param start
     * @param pageSize
     * @return
     */
    // ManagerMapper.java
    //@Select("select * from emp limit #{start},#{pageSize}")
    public List<User> page(@Param("start") Integer start, @Param("pageSize") Integer pageSize,@Param("name") String name,@Param("gender") Integer gender,@Param("begin") LocalDate begin,@Param("end") LocalDate end,@Param("dept_id") Integer dept);

    /**
     * 根据ids来完成删除员工的操作
     * @param ids
     */
    void deleteUser(List<Integer> ids);

    /**
     * 管理员添加员工
     */
    @Insert("INSERT INTO emp (username,name, gender, entrydate, dept_id, create_time, update_time) " +
            "VALUES (#{username},#{name}, #{gender}, #{entrydate}, #{dept_id},now(),now())")
    void insert(User user);

    /**
     * 修改更新员工信息
     * @param user
     */
    void updateUser(User user);


    /**
     *根据id重置员工密码
     * @param ids
     * @param rawPassword
     */

    void ResetPassword(@Param("rawPassword") String rawPassword, @Param("ids") List<Integer> ids);

    void ResetPassword(Map<String, Object> params);

    /**
     * 根据员工id查询工资信息
     * @param emp_id
     * @return
     */
    List<Salary> getSalariesByEmpId(Integer emp_id);

    /**
     * 修改员工基本工资
     * @param salary
     */
    /**
     * 修改员工基本工资
     *
     * @param emp_id
     * @param base_salary
     * @return 受影响的行数
     */
    @Update("update salary set base_salary = #{base_salary}, update_time = now() WHERE emp_id = #{emp_id}")
    int updateBaseSalary(@Param("emp_id") Integer emp_id, @Param("base_salary") BigDecimal base_salary);


    @Update("update salary set full_attendance_bonus = #{full_attendance_bonus}, update_time = now() WHERE emp_id = #{emp_id}")
    int updateAttendanceBonus(@Param("emp_id") Integer emp_id,  @Param("full_attendance_bonus") BigDecimal full_attendance_bonus);
}