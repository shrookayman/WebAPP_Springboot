package com.example.demo;

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
    public void writeStudentsToXml(String xmlFilePath, List<Student> students) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc;
        File xmlFile = new File(xmlFilePath);
        if (xmlFile.exists()) {
            doc = docBuilder.parse(xmlFile);
        } else {
            doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Student");
            doc.appendChild(rootElement);
        }

        // Get the root element (Students) of the XML document.
        Element rootElement = doc.getDocumentElement();

        for (Student student : students) {
            Element studentElement = doc.createElement("Student");
            rootElement.appendChild(studentElement);

            studentElement.setAttribute("ID", student.getId());

            studentElement.appendChild(createStudentElement(doc, "FirstName", student.getFirstName()));
            studentElement.appendChild(createStudentElement(doc, "LastName", student.getLastName()));
            studentElement.appendChild(createStudentElement(doc, "Gender", student.getGender()));
            studentElement.appendChild(createStudentElement(doc, "GPA", student.getGPA()));
            studentElement.appendChild(createStudentElement(doc, "Level", student.getLevel()));
            studentElement.appendChild(createStudentElement(doc, "Address", student.getAddress()));
        }

        // Write the DOM document back to the XML file.
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
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

        // Iterate through the student nodes to find the one with the matching ID and remove it.
        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node studentNode = studentNodes.item(i);
            if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) studentNode;
                String id = studentElement.getAttribute("ID");

                if (id.equals(studentId)) {
                    studentElement.getParentNode().removeChild(studentElement);
                    break; // Break once the student is deleted.
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