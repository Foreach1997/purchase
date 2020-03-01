package com.pu.purchase.util;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class RepResult implements Serializable {

    public static JSONObject repResult(int code,String msg,Object data){
        JSONObject res = new JSONObject();
        res.put("code",code);
        res.put("msg",msg);
        res.put("data",data);
        return res;
    }

    public static JSONObject repResult(int code,String msg,Object data,int count){
        JSONObject res = new JSONObject();
        res.put("code",code);
        res.put("msg",msg);
        res.put("data",data);
        res.put("count",count);
        return res;
    }
}
