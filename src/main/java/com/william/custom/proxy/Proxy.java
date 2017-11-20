package com.william.custom.proxy;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by sungang on 2017/11/20.
 */
public class Proxy implements Serializable {

    private final static Class[] constructorParams = {InvocationHandler.class};

    private static Map<Class<?>, Void> proxyClasses = Collections.synchronizedMap(new WeakHashMap<Class<?>, Void>());


    /**
     * @param loader
     * @param inface
     * @param handler
     * @return
     * @throws IllegalArgumentException
     */
    public static Object newProxyInstance(ClassLoader loader, Class<?> inface, InvocationHandler handler) throws IllegalArgumentException {
        if (null == handler) {
            throw new NullPointerException("handler is not null...");
        }
        // 获取代理对象
        Class<?> $proxyClass = getProxyClass(inface, loader);
        try {
            final Constructor<?> cons = $proxyClass.getConstructor(constructorParams);
            final InvocationHandler ih = handler;
            return newInstance(cons, ih);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取代理类
     *
     * @param inface
     * @param loader
     * @return
     */
    private static Class<?> getProxyClass(Class<?> inface, ClassLoader loader) {
        String methodStr = "";
        String rt = "\r\n";
        // 过去接口中 所有的方法
        Method[] methods = inface.getMethods();
        // 判断接口中 是否有方法
        if (null != methods) {
            // 遍历所有方法
            for (Method method : methods) {
                // 获取方法所有的的参数类型
                Class<?>[] paramTypes = method.getParameterTypes();
                // 获取方法的返回类型
                Class<?> returnType = method.getReturnType();
                methodStr += "  public " + returnType.getSimpleName() + " " + method.getName();
                // 判断参数类型
                if (paramTypes.length > 0) {
                    //保存临时参数
                    String tempParamNames = "";
                    //保存临时的参数类型
                    String tempParamTypes = "";
                    String tempparamName = "";
                    // 遍历 参数类型
                    for (int i = 0; i < paramTypes.length; i++) {
                        // 获取参数类型名
                        String paramTypeSimpleName = paramTypes[i].getSimpleName();
                        // 制定一个 参数名
                        String paramName = "param" + (i + 1);
                        tempparamName += paramName + ",";
                        tempParamNames += paramTypeSimpleName + " " + paramName + ",";
                        tempParamTypes += paramTypeSimpleName + ".class" + ",";
                    }

                    tempParamNames = tempParamNames.substring(0, tempParamNames.length() - 1);
                    tempParamTypes = tempParamTypes.substring(0, tempParamTypes.length() - 1);
                    tempparamName = tempparamName.substring(0, tempparamName.length() - 1);

                    methodStr += "(" + tempParamNames + "){" + rt
                            + "     try{" + rt
                            + "         Method m = " + inface.getName() + ".class.getMethod(\"" + method.getName() + "\",new Class[]{" + tempParamTypes + "});" + rt
                            + "         handler.invoke(this,m,new Object[]{" + tempparamName + "});" + rt
                            + "     }catch(Exception e) {" + rt
                            + "         e.printStackTrace();" + rt
                            + "     }catch(Throwable e){" + rt
                            + "         e.printStackTrace();" + rt
                            + "     }" + rt
                            + " }" + rt + "" + rt;

                } else {
                    methodStr += "(){" + rt
                            + "     try{" + rt
                            + "         Method m = " + inface.getName() + ".class.getMethod(\"" + method.getName() + "\");" + rt
                            + "         return (" + returnType.getSimpleName() + ")handler.invoke(this,m,new Object[]{});" + rt
                            + "     }catch(Exception e) {" + rt
                            + "         e.printStackTrace();" + rt
                            + "     }catch(Throwable e){" + rt
                            + "         e.printStackTrace();" + rt
                            + "     }" + rt
                            + "         return null;" + rt
                            + " }" + rt + "" + rt;
                }
            }
        } else {
            // 没有方法
        }


        // 获取当前接口的包名
        Package pack = inface.getPackage();
        String packageName = pack.getName();

        String score = "package " + packageName + ";" + rt + "" + rt
                + "import java.lang.reflect.Method;" + rt + "" + rt
                + "public class $ProxyClass implements "
                + inface.getSimpleName() + " { " + rt + "" + rt + " "
                + packageName + ".InvocationHandler handler;" + rt + "" + rt
                + " public $ProxyClass(InvocationHandler handler) {" + rt
                + "     this.handler = handler;" + rt + "   }" + rt + "" + rt +
                methodStr + rt +
                "}";

        //System.out.println(score);
        // 写入内存中
        Class<?> $proxyClass = writeClassToLoad(inface, score, packageName);
        return $proxyClass;
    }


    /**
     * @param inface
     * @param score
     * @param packageName
     * @return
     */
    private static Class<?> writeClassToLoad(Class<?> inface, String score,
                                             String packageName) {
        String fileSeparator = File.separator;

        // 生成 源代码 目录
        String fileName = System.getProperty("user.dir") + fileSeparator
                + "src" + fileSeparator + "main" + fileSeparator + "java"
                + fileSeparator + packageName.replace(".", fileSeparator);

        // 生成到 本地
        File parentFile = new File(fileName);
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(new File(parentFile, fileSeparator + "$ProxyClass.java"));
            fileWriter.write(score);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 编译 本地 JAVA文件
        Class<?> $proxyClass = compilerClassToLoad(fileName, fileSeparator, packageName);
        return $proxyClass;
    }


    @SuppressWarnings("restriction")
    private static Class<?> compilerClassToLoad(String fileName, String fileSeparator, String packageName) {
        // 编译 compile

        // 1，拿到 API 编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // System.out.println("compiler : " + compiler);

        // 2，获取 JAVA file 管理器
        // 参数 null 意味都是用 默认的
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        // 3，根据文件名字，获取一系列 JAVAFileObject 可使用多个
        // 通过 fileManager找到 fileName
        Iterable untis = fileManager.getJavaFileObjects(fileName + fileSeparator + ".$ProxyClass.java");

        // 4，获取编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, untis);

        // 5，实行任务
        task.call();

        try {
            // 6，关掉
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // load 到 内存 和 生成新对象
        try {
            URL[] urls = new URL[]{new URL("file:" + fileSeparator + System.getProperty("user.dir") + fileSeparator + "src"
                    + fileSeparator + "main" + fileSeparator + "java")};

            URLClassLoader classLoader = new URLClassLoader(urls);
            Class<?> clazz = classLoader.loadClass(packageName + ".$ProxyClass");

            return clazz;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isProxyClass(Class<?> cl) {
        if (cl == null) {
            throw new NullPointerException();
        }
        return proxyClasses.containsKey(cl);
    }


    private static Object newInstance(Constructor<?> cons, InvocationHandler h) {
        try {
            return cons.newInstance(new Object[]{h});
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
