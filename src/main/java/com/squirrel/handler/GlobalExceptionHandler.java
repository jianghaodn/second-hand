package com.squirrel.handler;

import com.squirrel.exception.SystemException;
import com.squirrel.pojo.User;
import com.squirrel.util.ResponseResult;
import com.squirrel.util.SystemConstant;
import com.squirrel.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author Administrator
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SystemException.class)
    public void handler(SystemException e, HttpServletResponse response) {
        log.error("SystemException:发生了异常！{1}", e.getMessage());
        //捕获异常以后，跳转到异常信息页，并将异常打印到屏幕上
        //直接以json数据的形式返回异常信息
//        HashMap<Object, Object> retultMap = new HashMap<>();
//        retultMap.put("code",400);
//        retultMap.put("msg",e.getMessage());
//        //将结果相应回去，需要用到response
//        WebUtils.render(response,retultMap);
        WebUtils.render(response, ResponseResult.errorResult(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public void handler(Exception e, HttpServletRequest request
    ,HttpServletResponse response) {
        log.error("Exception:发生了没有被捕获的异常,{1}",e.getMessage());
        e.printStackTrace();
        WebUtils.render(response,ResponseResult.errorResult());
    }
}
