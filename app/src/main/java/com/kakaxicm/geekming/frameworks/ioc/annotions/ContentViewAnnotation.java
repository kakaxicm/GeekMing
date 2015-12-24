package com.kakaxicm.geekming.frameworks.ioc.annotions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kakaxicm on 2015/12/24.
 * annotation for layoutid of contentview
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentViewAnnotation {
    int value() default -1;
}
