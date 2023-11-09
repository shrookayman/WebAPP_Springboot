package com.example.demo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/store-xml-data")
    public String showStudentDataForm(Model model, @RequestParam(required = false) Integer numStudents) {
        model.addAttribute("numStudents", numStudents);
        return "store-xml-data";
    }

    @PostMapping("/store-xml-data")
    public String storeStudentData(Model model, @RequestParam Integer numStudents, @RequestParam Map<String, String> formData) throws Exception {
        List<Student> students = new ArrayList<>();

//        for (int i = 1; i <= numStudents; i++) {
            System.out.println(formData);
            Student student = new Student();
            student.setId(formData.get("id"));
            student.setFirstName(formData.get("firstName"));
            student.setLastName(formData.get("lastName"));
            student.setGender(formData.get("gender"));
            student.setGPA(formData.get("gpa"));
            student.setLevel(formData.get("level"));
            student.setAddress(formData.get("address"));
            students.add(student);
//        }
        xmlStudentService.writeStudentsToXml("Student.xml", students);
        return "redirect:/display-xml-data";
    }

    @PostMapping("/delete-student")
    public String deleteStudent(@RequestParam("studentId") String studentId) throws Exception {
        xmlStudentService.deleteStudentFromXml("Student.xml", studentId);
        return "redirect:/display-xml-data";
    }

    //         ---------Search-----

    @GetMapping("/search-xml-data")
    public String showSearchForm() {
        return "search-xml-data";
    }

    @PostMapping("/search-xml-data")
    public String searchStudentData(Model model, @RequestParam String searchField, @RequestParam String searchTerm) {
        try {
            List<Student> students = xmlStudentService.readStudentsFromXml("Student.xml");

            // Perform the search based on the selected field (GPA or FirstName)
            List<Student> searchResults = new ArrayList<>();
            if ("GPA".equals(searchField)) {
                for (Student student : students) {
                    if (student.getGPA().equals(searchTerm)) {
                        searchResults.add(student);
                    }
                }
            } else if ("FirstName".equals(searchField)) {
                for (Student student : students) {
                    if (student.getFirstName().equalsIgnoreCase(searchTerm)) {
                        searchResults.add(student);
                    }
                }
            }

            model.addAttribute("searchedStudents", searchResults);
        } catch (Exception e) {
            // Handle exception
        }
        return "search-xml-data"; // Thymeleaf template name to display search results
    }

}

