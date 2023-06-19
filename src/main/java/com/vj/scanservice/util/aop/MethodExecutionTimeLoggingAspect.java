package com.vj.scanservice.util.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MethodExecutionTimeLoggingAspect {

    @Around("@annotation(com.vj.scanservice.util.aop.HowManySeconds)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        log.info(joinPoint.getSignature() + " executed in " + executionTime + "ms", AnsiColor.BRIGHT_RED);

        return proceed;
    }
}
