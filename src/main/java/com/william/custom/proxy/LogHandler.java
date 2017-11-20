package com.william.custom.proxy;

import java.lang.reflect.Method;

/**
 * Created by sungang on 2017/11/20.
 */
public class LogHandler implements InvocationHandler {

    private Object target;


    public LogHandler(Object target) {
        super();
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
        System.out.println("log start ............");
        return method.invoke(target, args);
    }


    public Object getProxy(){
        return null;
    }
}
