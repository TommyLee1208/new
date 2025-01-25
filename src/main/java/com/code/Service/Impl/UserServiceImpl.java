    package com.code.Service.Impl;

    import com.code.Service.UserService;
    import com.code.mapper.UserMapper;
    import com.code.pojo.LeaveApplication;
    import com.code.pojo.PageBean;
    import com.code.pojo.Salary;
    import com.code.pojo.User;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.time.LocalDateTime;
    import java.util.Arrays;
    import java.util.List;

    @Service
    public class UserServiceImpl implements UserService {

        @Autowired
        private UserMapper userMapper;
        @Override
        public User login(User user) {
            return userMapper.getByUsernameAndPassword(user);
        }

        /**
         * 获取用户个人信息
         * @param username
         * @return
         */

        @Override
        public User getUserByUsername(String username) {
            return userMapper.getUserByUsername(username);

        }

        /**
         * 修改用户信息(仅姓名和密码)
         * @param user
         */
        @Override
        public void updateUser(User user) {
            user.setUpdate_time(LocalDateTime.now());
            userMapper.updateUser(user);
        }

        /**
         * 上传头像
         */
        @Override
        public void uploadAvatar(MultipartFile image) throws IOException {
            User user = getUserByUsername(image.getOriginalFilename());
            user.setImage(Arrays.toString(image.getBytes()));
            userMapper.updateUser(user);
        }

        /**
         * 员工更新头像
         * @param image
         */
        @Override
        public void updateUserAvatar(String image, Integer id) {
            userMapper.updateUserAvatar(image, id);
        }

        /**
         * 获取员工工资
         * @param empId
         * @return
         */
        @Override
        public Salary getSalaryByEmpId(Integer empId) {
                return userMapper.getSalaryByEmpId(empId);
        }



    }
