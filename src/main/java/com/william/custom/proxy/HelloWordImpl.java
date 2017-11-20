package com.william.custom.proxy;

/**
 * Created by sungang on 2017/11/20.
 */
public class HelloWordImpl implements HelloWord {

    @Override
    public void setMsg(String msg, String name) {
        System.out.println(msg + ": " + name);
    }

    @Override
    public String getMsg() {
        return "hello : sungang";
    }

    @Override
    public void setUser(User user) {
        System.out.println(user.toString());
    }

    @Override
    public User getUser() {
        User user = new User(1, "sungang", "12346");
        return user;
    }
}
