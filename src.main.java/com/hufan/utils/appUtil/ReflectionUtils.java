package com.hufan.utils.appUtil;

import com.google.common.base.CaseFormat;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:反射工具类
 *
 * @author Limiaojun
 * @date: 2017年9月28日 下午3:28:38
 * @version 1.0
 * @since JDK 1.8
 */
public class ReflectionUtils {

    /**
     * Description:循环向上转型, 获取对象的 DeclaredMethod
     * 
     * @param object
     *            : 子类对象
     * @param methodName
     *            : 父类中的方法名
     * @param parameterTypes
     *            : 父类中的方法参数类型
     * @return 父类中的方法对象
     * @author Limiaojun
     * @date 2017年9月28日 下午3:28:45
     */
    public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了

            }
        }

        return null;
    }

    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     * 
     * @param object
     *            : 子类对象
     * @param methodName
     *            : 父类中的方法名
     * @param parameterTypes
     *            : 父类中的方法参数类型
     * @param parameters
     *            : 父类中的方法参数
     * @return 父类中方法的执行结果
     */

    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes,
            Object[] parameters) {
        // 根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
        Method method = getDeclaredMethod(object, methodName, parameterTypes);

        // 抑制Java对方法进行检查,主要是针对私有方法而言
        method.setAccessible(true);

        try {
            if (null != method) {

                // 调用object 的 method 所代表的方法，其方法的参数是 parameters
                return method.invoke(object, parameters);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     * 
     * @param object
     *            : 子类对象
     * @param fieldName
     *            : 父类中的属性名
     * @return 父类中的属性对象
     */

    public static Field getDeclaredField(Object object, String fieldName) {
        Field field = null;

        Class<?> clazz = object.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了

            }
        }

        return null;
    }


    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型
     * 
     * @Description 通过反射,获得定义Class时声明的父类的范型参数的类型
     * @param clazz
     * @return
     * @author Limiaojun
     * @date 2017年9月1日
     */
    public static Class<?> getSuperClassGenricType(Class<?> clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型
     * 
     * @Description 通过反射,获得定义Class时声明的父类的范型参数的类型
     * @param clazz
     * @param index
     * @return
     * @author Limiaojun
     * @date 2017年9月1日
     */
    public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class<?>) params[index];
    }

    /** 
     * Description: 复制对象
     *  
     * @param origin 源对象
     * @param target 目标对象
     * @return
     * @author Limiaojun
     * @date 2018-04-28 09:44
     */  
    public static Object copyObject(Object origin, Object target) {
        if (origin != null && target != null) {
            Class<?> originClazz = origin.getClass();
            Class<?> targetClazz = target.getClass();
            try {
                Field[] originFields = originClazz.getDeclaredFields();
                Field[] targetFields = targetClazz.getDeclaredFields();
                for (Field originField : originFields) {
                    String originFieldName = originField.getName();
                    if (!"serialVersionUID".equals(originFieldName)) {
                        for (Field targetField : targetFields) {
                            String targetFieldName = targetField.getName();
                            if (originFieldName.equals(targetFieldName)) {
                                PropertyDescriptor originPd = new PropertyDescriptor(originFieldName, originClazz);
                                PropertyDescriptor targetPd =
                                        new PropertyDescriptor(targetFieldName, target.getClass());
                                Method readMethod = originPd.getReadMethod();
                                Object value = readMethod.invoke(origin);
                                Method writeMethod = targetPd.getWriteMethod();
                                writeMethod.invoke(target, value);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return target;
        }
        return null;
    }
    
    
    /**
     * 把对象以 属性名-属性值 的方式格式化成 map
     * 
     * @Description 把对象以 属性名-属性值 的方式格式化成 map
     * @param object
     * @return
     * @author Limiaojun
     * @date 2017年9月1日
     */
    public static Map<String, Object> object2Map(Object object) {
        Class<?> clazz = object.getClass();
        Map<String, Object> objectMap = null;
        try {
            objectMap = new HashMap<String, Object>();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                if (!"serialVersionUID".equals(fieldName)) {
                    PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
                    Method readMethod = pd.getReadMethod();
                    Object value = readMethod.invoke(object);
                    if (!(value instanceof Set)) {
                        objectMap.put(fieldName, value == null ? "" : value.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectMap;
    }
    
    /**
     * Description:List<Object>对象转List<Map>
     * 
     * @param objList
     *            List<Object>对象
     * @return
     * @author Limiaojun
     * @date 2017年10月13日 上午11:19:43
     */
    public static List<Map<String, Object>> listObj2ListMap(List<?> objList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (null == objList || objList.isEmpty()) {
            return mapList;
        }
        for (Object obj : objList) {
            mapList.add(object2UnderlineMap(obj));
        }

        return mapList;
    }
    
    /**
     * 把对象以 属性名-属性值 的方式格式化成下划线 map
     * 
     * @Description 把对象以 属性名-属性值 的方式格式化成 map
     * @param object
     * @return
     * @author Limiaojun
     * @date 2017年9月1日
     */
    public static Map<String, Object> object2UnderlineMap(Object object) {
        Class<?> clazz = object.getClass();
        Map<String, Object> objectMap = null;
        try {
            objectMap = new HashMap<String, Object>();
            Field[] fields = clazz.getDeclaredFields();
            SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

            String key = null;
            Object value = null;
            for (Field field : fields) {
                String fieldName = field.getName();
                if (!"serialVersionUID".equals(fieldName)) {
                    PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
                    Method readMethod = pd.getReadMethod();
                    value = readMethod.invoke(object);

                    if(value instanceof Date) {
                        value = sdf.format(value);
                    }


                    if ("wx_id".equals(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName))) {
                        key = "id";
                    } else if ("sex".equals(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName))) {
                        //sex 原始数据
                        objectMap.put("sex",value);
                        
                        key = "sexDisplay";
                        if ("1".equals(value)) {
                            value = "男";
                        } else if ("2".equals(value)) {
                            value = "女";
                        }
                    } else if ("currency".equals(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName))) {
                        key = "balance";
                        if(value == null)
                            value =0;

                    } else if ("phone".equals(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName))) {
                        key = "phone_num";
                    }else {
                        key = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
                    }

                    objectMap.put(key,value == null? null:value);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectMap;
    }
    
    

    
    

    /**
     * 判断对象是否是基本类型
     * 
     * @Description 判断对象是否是基本类型
     * @param clazz
     * @return
     * @author Limiaojun
     * @date 2017年9月1日
     */
    public static boolean isWrapClass(Class<?> clazz) {
        try {
            return ((Class<?>) clazz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /** 
     * Description: 序列化对象转byte
     *  
     * @param object
     * @return
     * @author Limiaojun
     * @date 2018-04-28 09:46
     */  
    public static byte[] getBytesFromObject(Serializable object) {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo;
        try {
            oo = new ObjectOutputStream(bo);
            oo.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bo.toByteArray();
    }

    /** 
     * Description:byte转对象
     *  
     * @param b
     * @return
     * @author Limiaojun
     * @date 2018-04-28 09:46
     */  
    public static Object getObjectFromBytes(byte[] b) {
        if (b != null && b.length > 0) {
            try {
                ByteArrayInputStream bi = new ByteArrayInputStream(b);
                ObjectInputStream oi = new ObjectInputStream(bi);
                return oi.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 执行某个类下某个属性的get方法 ()
     * 
     * @Description 执行某个类下某个属性的get方法 ()
     * @param object
     * @param field_name
     *            属性名
     * @return
     * @author Limiaojun
     * @date 2017年9月1日
     */
    public static Object invokeFieldGetMethod(Object object, String field_name) {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        try {
            // Field field = clazz.getField(field_name);
            PropertyDescriptor originPd = new PropertyDescriptor(field_name, clazz);
            Method readMethod = originPd.getReadMethod();
            return readMethod.invoke(object);
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 执行某个类下某个属性的set方法 ()
     * 
     * @Description 执行某个类下某个属性的set方法 ()
     * @param object
     * @param field_name
     *            属性名
     * @param field_value
     * @return
     * @author Limiaojun
     * @date 2017年9月1日
     */
    public static Object invokeFieldSetMethod(Object object, String field_name, Object field_value) {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        try {
            // Field field = clazz.getField(field_name);
            PropertyDescriptor originPd = new PropertyDescriptor(field_name, clazz);
            Method readMethod = originPd.getWriteMethod();
            return readMethod.invoke(object, field_value);
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return "";
    }
  
}
