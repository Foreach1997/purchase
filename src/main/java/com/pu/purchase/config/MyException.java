package com.pu.purchase.config;

import com.pu.purchase.util.RepResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class MyException {

    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =BizException.class)
    @ResponseBody
    public Object exceptionHandler(HttpServletRequest req, BizException e){
        log.info(e.getMsg());
        return RepResult.repResult(1,e.getMsg(),null);
    }

}
