package com.squirrel.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
public class SystemException extends RuntimeException {
    private Integer code;
    private String msg;

    public SystemException(String message) {
        super(message);
        this.code =200;
        this.msg = message;
    }

    public SystemException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
