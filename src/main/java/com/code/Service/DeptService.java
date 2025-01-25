package com.code.Service;

import com.code.pojo.Dept;

import java.util.List;

public interface DeptService {
    /**
     * 查询全部部门列表
     * @return
     */
    List<Dept> list();

}
