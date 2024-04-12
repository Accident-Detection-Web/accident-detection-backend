package com.example.accidentdetectionservice.domain.notify.aop;

import com.example.accidentdetectionservice.domain.notify.aop.proxy.NotifyInfo;
import com.example.accidentdetectionservice.domain.notify.dto.NotifyMessage;
import com.example.accidentdetectionservice.domain.notify.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j(topic = "알림 기능 AOP 기능 분리")
@Component
@EnableAsync
public class NotifyAspect {

    private final NotifyService notifyService;

    public NotifyAspect(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Pointcut("@annotation(com.example.accidentdetectionservice.domain.notify.annotation.NeedNotify)")
    public void annotationPointcut(){
    }

    @Async
    @AfterReturning(pointcut = "annotationPointcut()", returning = "result")
    public void checkValue(JoinPoint joinPoint, Object result) {
        NotifyInfo notifyProxy = (NotifyInfo) result;
        notifyService.send(
            notifyProxy.getReceiver(),
            notifyProxy.getNotificationType(),
            NotifyMessage.ACCIDENT_DETECTION.getMessage(),
            null

        );
        log.info("result = {}", result);
    }
}
