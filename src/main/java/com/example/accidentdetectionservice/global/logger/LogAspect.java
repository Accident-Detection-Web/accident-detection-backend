package com.example.accidentdetectionservice.global.logger;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.example.accidentdetectionservice..*(..))")
    public void all() {
    }

    @Pointcut("execution(* com.example.accidentdetectionservice..*Controller.*(..))")
    public void controller() {
    }

    @Pointcut("execution(* com.example.accidentdetectionservice..*Service.*(..))")
    public void service() {
    }

    /**
     * ProceedingJoinPoint - JoinPoint 확장, 실제 메소드 실행을 직접 제어하는 기능을 제공하는 객체
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("all()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            return result;
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            log.info("log = {}", joinPoint.getSignature());
            log.info("timeMs = {}", timeMs);
        }

    }

    /**
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("controller() || service()")
    public void beforeLogic(JoinPoint joinPoint) throws Throwable{
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        log.info("method = {}", method.getName());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                log.info("type = {}", arg.getClass().getSimpleName());
                log.info("value = {}", arg);
            }
        }
    }

    /**
     *
     * @param joinPoint
     * @throws Throwable
     */
    @After("controller() || service()")
    public void afterLogic(JoinPoint joinPoint) throws Throwable{
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        log.info("method = {}", method.getName());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                log.info("type = {}", arg.getClass().getSimpleName());
                log.info("value = {}", arg);
            }
        }
    }
}
