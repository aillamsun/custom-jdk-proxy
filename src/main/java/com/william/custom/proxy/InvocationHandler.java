package com.william.custom.proxy;

import java.lang.reflect.Method;

/**
 *
 * Created by sungang on 2017/11/20.
 */
public interface InvocationHandler {

    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
