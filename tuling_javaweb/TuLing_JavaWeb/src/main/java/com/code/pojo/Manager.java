package com.code.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Manager {
    private String managername;
    private String managerpassword;
    private Integer id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> roles; // 存储角色名称列表
}
