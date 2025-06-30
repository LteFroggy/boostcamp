package com.example.backendproject.aop;

import com.example.backendproject.threadlocal.TraceIdHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Aspect // 공통으로 관리하고 싶은 기능을 담당하는 클래스에 붙이는 어노케이션
public class LogAspect {

    // AOP를 적용할 클래스
    @Pointcut(
            "execution(* com.example.backendproject.board.service.BoardService.createBoard(..)) || " +
            "execution(* com.example.backendproject.board.controller..*(..)) "
    )
    public void method() {}

    // PointCut
    // @Around("execution(* com.example.backendproject.board.service..*(..)) ")
    @Around("method()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName();

        try {
            log.info("[AOP_LOG][TRACE_ID] {} : {} 메서드 호출 시작", TraceIdHolder.get(), methodName);

            Object result = joinPoint.proceed(); // JoinPoint // AOP를 적용할 시점
            return result;
        } catch (Exception e) {
            log.error("[AOP_LOG][TRACE_ID] {} : {} 메서드 예외 {}", TraceIdHolder.get(), methodName, e.getMessage());

            return e;
        } finally {
            long end = System.currentTimeMillis();
            log.info("[AOP_LOG][TRACE_ID] {} : {} 메서드 실행 완료 시간 = {}", TraceIdHolder.get(), methodName, end - start);
        }
    }
}
