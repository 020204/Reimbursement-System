package com.example.reimbursement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot启动类
 */
@SpringBootApplication
@MapperScan("com.example.reimbursement.mapper")
@EnableTransactionManagement
@EnableCaching
public class ReimbursementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReimbursementSystemApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("报销管理系统启动成功!");
        System.out.println("访问地址: http://localhost:8080/api");
        System.out.println("========================================\n");
    }
}
