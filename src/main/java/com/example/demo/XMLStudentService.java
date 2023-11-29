package com.example.demo;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
@Service
public class XMLStudentService {
    public String writeStudentToXml(String xmlFilePath, Student student) throws Exception {
        // Validate student attributes
        if (validateStudentAttributes(student) != null){
            return(validateStudentAttributes(student));
        }

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc;
        File xmlFile = new File(xmlFilePath);
        if (xmlFile.exists()) {
            doc = docBuilder.parse(xmlFile);
        } else {
            doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Students");
            doc.appendChild(rootElement);
        }

        // Get the root element (Students) of the XML document.
        Element rootElement = doc.getDocumentElement();

        // Check for duplicate ID
        if (isStudentIdDuplicate(rootElement, student.getId())) {
            return("Duplicate student ID");
        }

        if(!student.getFirstName().matches("[a-zA-Z]+")){
            return("Invalid first name");
        }
        if(!student.getLastName().matches("[a-zA-Z]+")){
            return("Invalid last name");
        }
        if(!student.getAddress().matches("[a-zA-Z]+")){
            return("Invalid address");
        }

        Element studentElement = doc.createElement("Student");
        rootElement.appendChild(studentElement);

        studentElement.setAttribute("ID", student.getId());

        studentElement.appendChild(createStudentElement(doc, "FirstName", student.getFirstName()));
        studentElement.appendChild(createStudentElement(doc, "LastName", student.getLastName()));
        studentElement.appendChild(createStudentElement(doc, "Gender", student.getGender()));
        studentElement.appendChild(createStudentElement(doc, "GPA", student.getGPA()));
        studentElement.appendChild(createStudentElement(doc, "Level", student.getLevel()));
        studentElement.appendChild(createStudentElement(doc, "Address", student.getAddress()));

        // Write the DOM document back to the XML file.
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
        return null;
    }

    private String validateStudentAttributes(Student student) throws Exception {
        if (StringUtils.isBlank(student.getId()) || StringUtils.isBlank(student.getFirstName())
                || StringUtils.isBlank(student.getLastName()) || StringUtils.isBlank(student.getGender())
                || StringUtils.isBlank(student.getGPA()) || StringUtils.isBlank(student.getLevel())
                || StringUtils.isBlank(student.getAddress())) {
            return("All attributes must be provided and cannot be null/empty");
        }

        // Validate GPA within the range 0 to 4
        try {
            double gpa = Double.parseDouble(student.getGPA());
            if (gpa < 0 || gpa > 4) {
                return("GPA must be between 0 and 4");
            }
        } catch (NumberFormatException e) {
            return("Invalid GPA, must be between 0 and 4");
        }
        return null;
    }

    private boolean isStudentIdDuplicate(Element rootElement, String studentId) {
        NodeList studentNodes = rootElement.getElementsByTagName("Student");
        for (int i = 0; i < studentNodes.getLength(); i++) {
            Element studentElement = (Element) studentNodes.item(i);
            String existingId = studentElement.getAttribute("ID");
            if (existingId.equals(studentId)) {
                return true; // Duplicate ID found
            }
        }
        return false; // No duplicate ID found
    }

    private Node createStudentElement(Document doc, String tagName, String value) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(value));
        return element;
    }

    public List<Student> readStudentsFromXml(String xmlFilePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Load the input XML document, parse it, and return an instance of the Document class.
        Document document = builder.parse(new File(xmlFilePath));

        List<Student> students = new ArrayList<Student>();


        NodeList studentNodes = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node studentNode = studentNodes.item(i);
            if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) studentNode;
                String id = studentElement.getAttributes().getNamedItem("ID").getNodeValue();
                String firstName = studentElement.getElementsByTagName("FirstName").item(0)
                        .getChildNodes().item(0).getNodeValue();
                String lastName = studentElement.getElementsByTagName("LastName").item(0)
                        .getChildNodes().item(0).getNodeValue();
                String gender = studentElement.getElementsByTagName("Gender").item(0)
                        .getChildNodes().item(0).getNodeValue();
                String gpa = studentElement.getElementsByTagName("GPA").item(0)
                        .getChildNodes().item(0).getNodeValue();
                String level = studentElement.getElementsByTagName("Level").item(0)
                        .getChildNodes().item(0).getNodeValue();
                String address = studentElement.getElementsByTagName("Address").item(0)
                        .getChildNodes().item(0).getNodeValue();


                System.out.println("-----READ-----");

                students.add(new Student(id, firstName, lastName, gender, gpa, level, address));
                for (Student st : students) {
                    System.out.println(st.getFirstName());
                    System.out.println(st.getId());
                }
            }
        }
        return students;
    }


    public void deleteStudentFromXml(String xmlFilePath, String studentId) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlFilePath);

        NodeList studentNodes = doc.getElementsByTagName("Student");

        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node studentNode = studentNodes.item(i);
            if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) studentNode;
                String id = studentElement.getAttribute("ID");

                if (id.equals(studentId)) {
                    studentElement.getParentNode().removeChild(studentElement);
                    break;
                }
            }
        }

        // Write the updated XML back to the file.
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlFilePath));
        transformer.transform(source, result);
    }

    public void updateStudentData(String xmlFilePath, String studentId, Student student) throws Exception {
        File xmlFile = new File(xmlFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        // Find the students element with the specified ID
        NodeList studentsList = doc.getElementsByTagName("Student");
        for (int i = 0; i < studentsList.getLength(); i++) {
            Node Student = studentsList.item(i);
            if (Student.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) Student;
                String studentID = studentElement.getAttribute("ID");
                if (studentID.equals(studentId)) {
                    //  Update students data

                    Element firstNameElement = (Element) studentElement.getElementsByTagName("FirstName").item(0);
                    firstNameElement.setTextContent(student.getFirstName());

                    Element lastNameElement = (Element) studentElement.getElementsByTagName("LastName").item(0);
                    lastNameElement.setTextContent(student.getLastName());

                    Element genderElement = (Element) studentElement.getElementsByTagName("Gender").item(0);
                    genderElement.setTextContent(student.getGender());

                    Element gpaElement = (Element) studentElement.getElementsByTagName("GPA").item(0);
                    gpaElement.setTextContent(student.getGPA());

                    Element levelElement = (Element) studentElement.getElementsByTagName("Level").item(0);
                    levelElement.setTextContent(student.getLevel());

                    Element addressElement = (Element) studentElement.getElementsByTagName("Address").item(0);
                    addressElement.setTextContent(student.getAddress());
                    break; // Exit loop after updating
                }
            }
        }

        // Write the changes back to the XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlFilePath));
        transformer.transform(source, result);

    }

    public void sortStudents(String xmlFilePath, String attribute, String sortType) throws Exception{
        File xmlFile = new File(xmlFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        //  Get all students elements
        NodeList studentsList = doc.getElementsByTagName("Student");

        // Create a list to store <students> elements
        List<Element> Students = new ArrayList<>();
        for (int i = 0; i < studentsList.getLength(); i++) {
            Node student = studentsList.item(i);
            if (student.getNodeType() == Node.ELEMENT_NODE) {
                Students.add((Element) student);
            }
        }

        //  Sort the list of <students> elements based on FirstName attribute
        Collections.sort(Students, new Comparator<Element>() {
            @Override
            public int compare(Element user1, Element user2) {
                //asc sort
               if(Objects.equals(sortType,"asc")){
                   if(Objects.equals(attribute, "fName")){
                       String firstName1 = user1.getElementsByTagName("FirstName").item(0).getTextContent();
                       String firstName2 = user2.getElementsByTagName("FirstName").item(0).getTextContent();
                       return firstName1.compareTo(firstName2);
                   }
                   else if(Objects.equals(attribute, "lName")){
                       String LastName1 = user1.getElementsByTagName("LastName").item(0).getTextContent();
                       String LastName2 = user2.getElementsByTagName("LastName").item(0).getTextContent();
                       return LastName1.compareTo(LastName2);
                   }
                   else if(Objects.equals(attribute, "gpa")){
                       String gpa1 = user1.getElementsByTagName("GPA").item(0).getTextContent();
                       String gpa2 = user2.getElementsByTagName("GPA").item(0).getTextContent();
                       return gpa1.compareTo(gpa2);
                   }
                   else if(Objects.equals(attribute, "level")){
                       String level1 = user1.getElementsByTagName("Level").item(0).getTextContent();
                       String level2 = user2.getElementsByTagName("Level").item(0).getTextContent();
                       return level1.compareTo(level2);
                   }
                   else{
                       String id1 = user1.getAttribute("ID");
                       String id2 = user2.getAttribute("ID");
                       return id1.compareTo(id2);
                   }
               }

               //desc sort
               else{
                   if(Objects.equals(attribute, "fName")){
                       String firstName1 = user1.getElementsByTagName("FirstName").item(0).getTextContent();
                       String firstName2 = user2.getElementsByTagName("FirstName").item(0).getTextContent();
                       return firstName2.compareTo(firstName1);
                   }
                   else if(Objects.equals(attribute, "lName")){
                       String LastName1 = user1.getElementsByTagName("LastName").item(0).getTextContent();
                       String LastName2 = user2.getElementsByTagName("LastName").item(0).getTextContent();
                       return LastName2.compareTo(LastName1);
                   }
                   else if(Objects.equals(attribute, "gpa")){
                       String gpa1 = user1.getElementsByTagName("GPA").item(0).getTextContent();
                       String gpa2 = user2.getElementsByTagName("GPA").item(0).getTextContent();
                       return gpa2.compareTo(gpa1);
                   }
                   else if(Objects.equals(attribute, "level")){
                       String level1 = user1.getElementsByTagName("Level").item(0).getTextContent();
                       String level2 = user2.getElementsByTagName("Level").item(0).getTextContent();
                       return level2.compareTo(level1);
                   }
                   else{
                       String id1 = user1.getAttribute("ID");
                       String id2 = user2.getAttribute("ID");
                       return id2.compareTo(id1);
                   }
               }
            }
        });

        // Clear the existing XML content
        NodeList rootList = doc.getElementsByTagName(doc.getDocumentElement().getNodeName());
        Node root = rootList.item(0);
        while (root.hasChildNodes()) {
            root.removeChild(root.getFirstChild());
        }

        // Add sorted <students> elements back to the XML document
        for (Element student : Students) {
            root.appendChild(doc.importNode(student, true));
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlFilePath));
        transformer.transform(source, result);
    }
}