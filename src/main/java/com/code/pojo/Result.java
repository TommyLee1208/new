package com.code.pojo;

public class Result {
    private Integer code; // 响应码，1 代表成功; 0 代表失败
    private String msg;   // 响应信息 描述字符串
    private Object data;  // 返回的数据

    public Result() {
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // 成功响应
    public static Result success(Object data){
        return new Result(1, "success", data);
    }

    // 失败响应
    public static Result error(String msg){
        return new Result(0, msg, null);
    }
}