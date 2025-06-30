package com.example.backendproject.threadlocal;

// 이 클래스는 스레드마다 고유한 요청 ID를 저장하고 꺼내는 역할을 한다.
public class TraceIdHolder {

    // 기본적으로 알아서 값을 생성하고 저장해둠.
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // ThreadLocal에 값을 저장하고 꺼낼 set 메소드와 get 메소드
    public static void set(String traceId) {
        threadLocal.set(traceId);
    }

    public static String get() {
        return threadLocal.get();
    }

    // 하나의 요청이 끝났을 때 ThreadLocal에 저장된 값을 지우기 위한 메서드
    public static void clear() {
        threadLocal.remove();
    }

}
