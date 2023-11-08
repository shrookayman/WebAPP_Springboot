package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes  = {DataController.class, XMLStudentService.class, Student.class , Demo2Application.class })

class Demo2ApplicationTests {

    @Test
    void contextLoads() {
    }

}
