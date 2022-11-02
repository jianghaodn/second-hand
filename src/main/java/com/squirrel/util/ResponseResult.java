package com.squirrel.util;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @author Administrator
 * 响应工具类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public ResponseResult() {

    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResponseResult errorResult(int code, String msg) {
        ResponseResult result = new ResponseResult();
        return result.error(code, msg);
    }
    public static ResponseResult errorResult( String msg) {
        return errorResult(500,msg);
    }
    public static ResponseResult errorResult() {
        ResponseResult result = new ResponseResult();
        return result.error(500, "失败");
    }

    public static ResponseResult loginFailResult(){
        return new ResponseResult<>().error(SystemConstant.NEED_LOGIN,"登录失效，请重新登录");
    }

    public static ResponseResult okResult() {
        ResponseResult result = new ResponseResult();
        return result.ok();
    }
    public static<T> ResponseResult okResult(Integer code,String msg,T t) {
        ResponseResult result = new ResponseResult();
        return result.ok(code,t,msg);
    }

    public static ResponseResult result(Integer code, String msg) {
        return new ResponseResult<>(code, msg);
    }

    public static ResponseResult okResult(int code, String msg) {
        ResponseResult result = new ResponseResult();
        return result.ok(code, null, msg);
    }

    public static <T> ResponseResult okResult(T t) {
        ResponseResult result = new ResponseResult();
        return result.ok(200, t, "操作成功");
    }

    public ResponseResult<?> error(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public ResponseResult<?> ok(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    public ResponseResult<?> ok(Integer code, T data, String msg) {
        this.data = data;
        this.ok();
        return this;
    }

    public ResponseResult<?> ok() {
        this.code = 200;
        this.msg = "操作成功";
        return this;
    }

    public ResponseResult<?> ok(T data) {
        this.data = data;
        this.ok();
        return this;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static ResponseResult result(Integer code2, Object o) {
        return new ResponseResult<>(code2, o);
    }


}