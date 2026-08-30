package com.fixmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FixMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixMateApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  FixMate Platform Backend Started Successfully  ");
        System.out.println("  Ready for Local & Production Service Dispatch   ");
        System.out.println("=================================================");
    }
}
