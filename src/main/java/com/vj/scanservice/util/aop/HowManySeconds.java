package com.vj.scanservice.util.aop;

import org.springframework.context.annotation.Profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
//@Profile("logging")
@Retention(RetentionPolicy.RUNTIME)
public @interface HowManySeconds {}
