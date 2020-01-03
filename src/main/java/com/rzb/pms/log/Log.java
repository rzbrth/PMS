package com.rzb.pms.log;

/**
 * Custom Log annotation to inject the log class at runtime
 * @author Rajib Rath
 * @Target defines where we can use our annotation.
 * @Retention defines when the annotation can be available.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Rajib.Rath
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

}
