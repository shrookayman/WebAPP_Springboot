package com.example.demo;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class XMLStudentService {
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

                System.out.println("-----READ-----");

                students.add(new Student(id, firstName));
                for (Student empl : students) {
                    System.out.println(empl.getFirstName());
                }
            }
        }

        return students;
    }

}
