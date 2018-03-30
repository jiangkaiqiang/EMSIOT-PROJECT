package com.ems.iot.manage.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * APP注解
 * 表示当前为手机客户端访问
 * @author Jiangkaiqiang
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface APP  {
	String[] value() default {};
}
