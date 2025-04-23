package org.example.fleetingtime.common;

import lombok.Data;

@Data
public class R<T> {
    private Integer code;
    private String msg;
    private T data;
    public static<T> R<T> ok(T data){
        R<T> r = new R<T>();
        r.setCode(200);
        r.setMsg("goodjob");
        r.setData(data);
        return r;
    }
    public static R ok(){
        R r = new R();
        r.setCode(200);
        r.setMsg("goodjob");
        return r;
    }
    public static R ok(String msg){
        R r = new R();
        r.setCode(200);
        r.setMsg("msg");
        return r;
    }
    public static R error(Integer code, String msg) {
        R r = new R();
        r.setCode(code);
        r.setMsg("error");
        return r;
    }
    public static<T> R<T> error(Integer code, String msg, T data) {
        R<T> r = new R<T>();
        r.setCode(code);
        r.setMsg("error");
        return r;
    }
}
