package cardgame;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Util {

    public static Method getMethodByName(Class clazz, String name) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    public static Object invokeMethod(Object object, String methodName, Object... args) throws InvocationTargetException, IllegalAccessException {
        Method method = getMethodByName(object.getClass(), methodName);
        method.setAccessible(true);
        return method.invoke(object, args);
    }
}