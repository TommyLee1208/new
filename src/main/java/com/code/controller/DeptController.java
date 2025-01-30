package com.code.controller;

import com.code.Service.DeptService;
import com.code.pojo.Dept;
import com.code.pojo.Result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/depts")
public class DeptController {

    @Autowired
    private DeptService deptService;

//    @GetMapping  // 更明确地指定 HTTP 方法为 GET
//    public Result<String> list() {
//        log.info("查询全部部门数据");
//        List<Dept> deptList = deptService.list();
//
//        return Result.success(deptList);
//    }

}
