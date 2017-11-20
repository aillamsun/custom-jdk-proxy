package com.william.custom.proxy;

/**
 * Created by sungang on 2017/11/20.
 */
public class Main {


    public static void main(String[] args) {

        //被代理对象
        HelloWord iHelloWord = new HelloWordImpl();
        LogHandler handler = new LogHandler(iHelloWord);

        HelloWord helloWord = (HelloWord) Proxy.newProxyInstance(iHelloWord.getClass().getClassLoader(), HelloWord.class, handler);

        helloWord.setMsg("hello ", "sungang");

        String msg = helloWord.getMsg();
        System.out.println(msg);

        helloWord.setUser(new User(1, "sungang", "123465"));

        User user = helloWord.getUser();
        System.out.println(user.toString());
    }
}
