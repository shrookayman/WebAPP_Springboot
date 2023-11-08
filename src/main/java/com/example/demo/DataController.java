package com.example.demo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DataController {
    private final XMLStudentService xmlStudentService;

    public DataController(XMLStudentService xmlDataService) {
        this.xmlStudentService = xmlDataService;
    }

    @GetMapping("/display-xml-data")
    public String displayXmlData(Model model) {
        try {
            System.out.println("-----Controller-----");
            List<Student> students;
            students = xmlStudentService.readStudentsFromXml("Student.xml");
            model.addAttribute("students", students);
        } catch (Exception e) {
            // Handle exception
        }
        return "display-xml-data"; // Thymeleaf template name
    }
}

