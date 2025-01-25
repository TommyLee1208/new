package com.code.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String name;
    private String username; // 如果需要保存用户名到数据库，请确保SQL语句中也包含它
    private String password;
    private String image;
    private LocalDate entrydate;
    private Integer dept_id;
    private Integer gender;
    private Integer job; // 如果需要保存职位信息到数据库，请确保SQL语句中也包含它
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}