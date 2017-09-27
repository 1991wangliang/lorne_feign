package com.lorne.feign.decoder;

import com.alibaba.fastjson.JSONObject;
import com.lorne.core.framework.exception.FeignErrorException;
import com.lorne.feign.model.FeignError;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;

/**
 * create by lorne on 2017/9/27
 */
@Configuration
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if(response.status() >= 400 && response.status() <= 500){
            try {
                String json = Util.toString(response.body().asReader());
                FeignError feignException =  JSONObject.parseObject(json, FeignError.class);
                String expClass =  feignException.getException();
                Class<?> clazz =  Class.forName(expClass);
                Constructor constructor = clazz.getConstructor(String.class);
                Exception obj = (Exception)constructor.newInstance(feignException.getMessage());
                return obj;
            } catch (Exception e) {
                return new FeignErrorException(e);
            }
        }
        return feign.FeignException.errorStatus(methodKey, response);
    }
}
