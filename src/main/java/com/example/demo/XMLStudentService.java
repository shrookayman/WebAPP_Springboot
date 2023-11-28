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
}