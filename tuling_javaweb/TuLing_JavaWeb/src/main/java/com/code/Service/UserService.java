package com.code.Service;

import com.code.pojo.LeaveApplication;
import com.code.pojo.PageBean;
import com.code.pojo.Salary;
import com.code.pojo.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface UserService {


    /**
     * 用户登录
     * @param user
     * @return
     */
    User login(User user);


    /**
     * 获取用户信息
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    /**
     * 员工修改个人信息，只能修改自己的名字和密码和头像
     * @param user
     */
    void updateUser(User user);

    /**
     * 员工上传头像
     */

    void uploadAvatar(MultipartFile image) throws IOException;

    /**
     * 员工修改头像
     * @param image
     */
    void updateUserAvatar(String image, Integer id);

    /**
     * 员工查看自己的工资
     * @param empId
     * @return
     */
    Salary getSalaryByEmpId(Integer empId);


}
