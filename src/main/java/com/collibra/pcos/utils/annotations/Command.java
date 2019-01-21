package com.collibra.pcos.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    String pattern() default "";

    boolean hello() default false;

    boolean goodbye() default false;

    boolean error() default false;
}
