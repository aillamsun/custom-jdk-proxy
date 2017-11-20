package com.william.custom.proxy;

import java.lang.reflect.Method;

public class $ProxyClass implements HelloWord { 

 com.william.custom.proxy.InvocationHandler handler;

 public $ProxyClass(InvocationHandler handler) {
     this.handler = handler;
   }

  public void setMsg(String param1,String param2){
     try{
         Method m = com.william.custom.proxy.HelloWord.class.getMethod("setMsg",new Class[]{String.class,String.class});
         handler.invoke(this,m,new Object[]{param1,param2});
     }catch(Exception e) {
         e.printStackTrace();
     }catch(Throwable e){
         e.printStackTrace();
     }
 }

  public String getMsg(){
     try{
         Method m = com.william.custom.proxy.HelloWord.class.getMethod("getMsg");
         return (String)handler.invoke(this,m,new Object[]{});
     }catch(Exception e) {
         e.printStackTrace();
     }catch(Throwable e){
         e.printStackTrace();
     }
         return null;
 }

  public void setUser(User param1){
     try{
         Method m = com.william.custom.proxy.HelloWord.class.getMethod("setUser",new Class[]{User.class});
         handler.invoke(this,m,new Object[]{param1});
     }catch(Exception e) {
         e.printStackTrace();
     }catch(Throwable e){
         e.printStackTrace();
     }
 }

  public User getUser(){
     try{
         Method m = com.william.custom.proxy.HelloWord.class.getMethod("getUser");
         return (User)handler.invoke(this,m,new Object[]{});
     }catch(Exception e) {
         e.printStackTrace();
     }catch(Throwable e){
         e.printStackTrace();
     }
         return null;
 }


}