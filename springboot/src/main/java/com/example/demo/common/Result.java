package com.example.demo.common;

/**
 * 通用返回结果封装
 */
public class Result<T> {

    private Integer code;      // 状态码：200 成功，其它为失败
    private String message;    // 提示信息
    private T data;            // 返回数据

    public Result() {
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 工厂方法 - 成功
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    // 工厂方法 - 失败（自定义消息）
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    // Getter & Setter
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
