package com.code.pojo;

import java.util.List;

// 分页查询结果封装类
public class PageBean<T> {
    private Long total;
    private List<T> rows;

    @Override
    public String toString() {
        return "PageBean{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }

    public PageBean() {
    }

    public PageBean(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}