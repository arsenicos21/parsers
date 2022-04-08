import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName1 = "data.csv";
        String fileName2 = "data.xml";
        String fileExitName1 = "data.json";
        String fileExitName2 = "data2.json";

        List<Employee> list1 = parseCSV(columnMapping, fileName1);
        List<Employee> list2 = parseXML(fileName2);

        String json = listToJson(list1);
        parseCSV(columnMapping, fileName1);
        listToJson(list1);
        writeString(json, fileExitName1);

        String json2 = listToJson(list2);
        listToJson(list2);
        writeString(json2, fileExitName2);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> employeeList = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            employeeList = csvToBean.parse();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return employeeList;
    }

    public static List<Employee> parseXML(String fileName)
            throws ParserConfigurationException, IOException, SAXException {
        Employee employee = new Employee();
        List<Employee> employeeList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                for (int j = 0; j < 1; j++) {
                    employee.id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    employee.firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    employee.lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    employee.country = element.getElementsByTagName("country").item(0).getTextContent();
                    employee.age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    employeeList.add(employee);
                }
            }
        }
        return employeeList;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String fileExitName) {
        try (FileOutputStream fos = new FileOutputStream(fileExitName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
