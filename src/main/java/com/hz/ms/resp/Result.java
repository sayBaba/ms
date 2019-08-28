package com.hz.ms.resp;

import lombok.Data;
/**
 *
 * @param <T>
 */
@Data
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    /**
     * 返回失败
     * @param code
     * @param msg
     * @return
     */
    public static Result getFail(int code,String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }


}
