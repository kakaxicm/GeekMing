package com.kakaxicm.geekming.frameworks.ioc;

import android.app.Activity;

import com.kakaxicm.geekming.frameworks.ioc.annotions.ContentViewAnnotation;
import com.kakaxicm.geekming.frameworks.ioc.annotions.ViewIdAnnotation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by kakaxicm on 2015/12/24.
 */
public class ViewInjector {
    public static void injectContentViewForActivity(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        ContentViewAnnotation annotation = clazz.getAnnotation(ContentViewAnnotation.class);
        if (annotation != null) {
            int layoutId = annotation.value();
            try {
                Method method = clazz.getMethod("setContentView", int.class);
                method.invoke(activity, layoutId);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void injectViewsForActivity(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            ViewIdAnnotation annotation = field.getAnnotation(ViewIdAnnotation.class);
            if(annotation != null) {
                int id = annotation.value();
                if(id != -1) {
                    try {
                        Method method = clazz.getMethod("findViewById", int.class);
                        Object view = method.invoke(activity, id);
                        field.setAccessible(true);
                        field.set(activity, view);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
