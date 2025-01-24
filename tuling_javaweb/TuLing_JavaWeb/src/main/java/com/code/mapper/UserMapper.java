package com.code.mapper;

import com.code.pojo.LeaveApplication;
import com.code.pojo.Salary;
import com.code.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 根据用户名和密码查询用户
     * @param user
     * @return
     */


    @Select("select * from emp where username = #{username} and password = #{password}")
    User getByUsernameAndPassword(User user);

    /**
     * 根据用户名查询个人信息
     * @param username
     * @return
     */
    @Select("select * from emp where username = #{username}")
    User getUserByUsername(String username);

    /**
     * 修改用户信息（仅姓名和密码）
     * @param user
     */
    @Update("update emp set name = #{name}, password = #{password} where id = #{id}")
    void updateUser(User user);

    /**
     * 用户上传头像
     */
    @Update("update emp set image = #{image} where id = #{id}")
    void uploadAvatar(User user);

    /**
     * 修改用户头像
     * @param image
     * @param id
     */
    @Update("UPDATE emp SET image = #{image}, update_time = now() WHERE id = #{id}")
    void updateUserAvatar(@Param("image") String image, @Param("id") Integer id);


    /**
     * 用户查询自己的工资
     * @param empId
     * @return
     */
    @Select("select * from salary where emp_id = #{emp_id}")
    Salary getSalaryByEmpId(Integer empId);


    /**
     * 请假申请
     * @param leaveApplication
     * @return
     */
    @Insert("insert into leave_application (emp_id, leave_type, start_time, end_time, approved_by_manager, approve_time, apply_time, remarks) " +
            "values (#{emp_id}, #{leave_type}, #{start_time}, #{end_time}, #{approved_by_manager}, #{approve_time},now(), #{remarks})")
    LeaveApplication sendLeaveApplication(LeaveApplication leaveApplication);
}
