package cardgame;

import org.junit.Assert;

import java.lang.reflect.Method;
import static org.junit.Assert.assertEquals;

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
}