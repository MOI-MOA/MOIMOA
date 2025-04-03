package com.b110.jjeonchongmu.domain.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleJenkinsTest {

    @GetMapping("/api/test/hello")
    public String helloWorld() {
        System.out.println("===========================");
        System.out.println("Hello World API 호출 성공!");
        System.out.println("Jenkins 파이프라인 테스트 중...");
        System.out.println("===========================");
        return "Hello World from Jenkins Pipeline!";
    }

    @GetMapping("/api/test/health")
    public String healthCheck() {
        System.out.println("헬스체크 API 호출됨");
        return "{\"status\":\"UP\",\"message\":\"서버가 정상 작동 중입니다\"}";
    }
}