package com.code.Service.Impl;

import com.code.Service.DeptService;
import com.code.mapper.DeptMapper;
import com.code.pojo.Dept;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptMapper deptMapper;
    @Override
    public List<Dept> list() {
        // 实现查询逻辑
        return deptMapper.list(); // 示例代码，实际应返回部门列表
    }
}
