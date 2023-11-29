package com.example.demo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            List<Student> students;
            students = xmlStudentService.readStudentsFromXml("Student.xml");
            model.addAttribute("students", students);
        } catch (Exception e) {
            System.out.println("error ");
        }
        return "display-xml-data";
    }

    @GetMapping("/store-xml-data")
    public String showStudentDataForm(Model model, @RequestParam(required = false) Integer numStudents) {
        model.addAttribute("numStudents", numStudents);
        return "store-xml-data";
    }

    @PostMapping("/store-xml-data")
    public String storeStudentData(Model model, @RequestParam Integer numStudents, @RequestParam Map<String, String> formData) throws Exception {
       try{
        Student student = new Student();
        student.setId(formData.get("id"));
        student.setFirstName(formData.get("firstName"));
        student.setLastName(formData.get("lastName"));
        student.setGender(formData.get("gender"));
        student.setGPA(formData.get("gpa"));
        student.setLevel(formData.get("level"));
        student.setAddress(formData.get("address"));
        String response = xmlStudentService.writeStudentToXml("Student.xml", student);
        if(response == null){
            numStudents--;
            if(numStudents==0){
                return "redirect:/display-xml-data";
            }
            else {
                return "redirect:/store-xml-data?numStudents=" + numStudents;
            }
        }
        else {
            model.addAttribute("numStudents", numStudents);
            model.addAttribute("errorMsg", response);
            return "store-xml-data";
        }
    } catch (Exception e) {
        model.addAttribute("numStudents", numStudents);
        model.addAttribute("errorMsg", "Error storing student data.");
        return "store-xml-data";
    }
//        System.out.println(response);
//        redirectAttributes.addFlashAttribute("numStudents", numStudents);
//        redirectAttributes.addFlashAttribute("errorMsg", response);
//        return "redirect:/store-xml-data?numStudents=" + numStudents;
    }
    String sID="";
    @PostMapping("/delete-student")
    public String deleteStudent(@RequestParam("studentId") String studentId) throws Exception {

        xmlStudentService.deleteStudentFromXml("Student.xml", studentId);
        return "redirect:/display-xml-data";
    }
    //         ---------Update-------
    @GetMapping("/update-student")
    public String updateStudent(Model model,@RequestParam("studentId") String studentId) throws Exception {
        try {
            sID=studentId;
            List<Student> students = xmlStudentService.readStudentsFromXml("Student.xml");
            List<Student> updateStudent = new ArrayList<>();
            for (Student student : students) {
                if (student.getId().equalsIgnoreCase(studentId)) {
                    updateStudent.add(student);
                }
            }
           model.addAttribute("updateStudentData", updateStudent);
        } catch (Exception e) {
            System.out.println("error");
        }
        return "update-attributes";
    }

    @PostMapping("/update-student")
    public String updateStudentData(@RequestParam Map<String, String> formData) throws Exception {
        Student student = new Student();
        student.setId(formData.get("id"));
        student.setFirstName(formData.get("firstName"));
        student.setLastName(formData.get("lastName"));
        student.setGender(formData.get("gender"));
        student.setGPA(formData.get("gpa"));
        student.setLevel(formData.get("level"));
        student.setAddress(formData.get("address"));
        xmlStudentService.updateStudentData("Student.xml", sID,student);
        return "redirect:/display-xml-data";
    }

    //         ---------Sort---------

    @PostMapping("/sort-students")
    public String sortStudents(@RequestParam Map<String, String> sortData) throws Exception {
        xmlStudentService.sortStudents("Student.xml", sortData.get("attributes"),sortData.get("type"));
        System.out.println(sortData.get("attributes")   +   sortData.get("type") + "aaaaaaaaaaaaaaaaaaaaaaaaaa");
        return "redirect:/display-xml-data";
    }


    //         ---------Search-------

    @GetMapping("/search-xml-data")
    public String showSearchForm() {
        return "search-xml-data";
    }

    @PostMapping("/search-xml-data")
    public String searchStudentData(Model model, @RequestParam String searchField, @RequestParam String searchTerm) {
        try {
            List<Student> students = xmlStudentService.readStudentsFromXml("Student.xml");
            int numOfFoundStudents = 0;
            List<Student> searchResults = new ArrayList<>();
            if ("GPA".equals(searchField)) {
                for (Student student : students) {
                    if (student.getGPA().equals(searchTerm)) {
                        searchResults.add(student);
                        numOfFoundStudents++;
                    }
                }
            } else if ("FirstName".equals(searchField)) {
                for (Student student : students) {
                    if (student.getFirstName().equalsIgnoreCase(searchTerm)) {
                        searchResults.add(student);
                        numOfFoundStudents++;

                    }
                }
            }
            else if ("LastName".equals(searchField)) {
                for (Student student : students) {
                    if (student.getLastName().equalsIgnoreCase(searchTerm)) {
                        searchResults.add(student);
                        numOfFoundStudents++;

                    }
                }
            }
            else if ("Level".equals(searchField)) {
                for (Student student : students) {
                    if (student.getLevel().equalsIgnoreCase(searchTerm)) {
                        searchResults.add(student);
                        numOfFoundStudents++;

                    }
                }
            }
            else if ("Address".equals(searchField)) {
                for (Student student : students) {
                    if (student.getAddress().equalsIgnoreCase(searchTerm)) {
                        searchResults.add(student);
                        numOfFoundStudents++;

                    }

                }
            }
            else if ("ID".equals(searchField)) {
                for (Student student : students) {
                    if (student.getId().equalsIgnoreCase(searchTerm)) {
                        searchResults.add(student);
                        numOfFoundStudents++;

                    }
                }
            }
            else if ("Gender".equals(searchField)) {
                for (Student student : students) {
                    if (student.getGender().equalsIgnoreCase(searchTerm)) {
                        searchResults.add(student);
                        numOfFoundStudents++;

                    }
                }
            }

            model.addAttribute("searchedStudents", searchResults);
            model.addAttribute("numOfFoundStudents", numOfFoundStudents);


        } catch (Exception e) {
            System.out.println("error");
        }
        return "search-xml-data";
    }

}

