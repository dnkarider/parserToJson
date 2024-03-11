import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String args []) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCSV = parseCSV(columnMapping, fileName);
        String jsonCSV = listToJson(listCSV);
        writeString(jsonCSV, "new_data_json");

        List<Employee> listXML = parseXML("data.xml");
        String jsonXML = listToJson(listXML);
        writeString(jsonXML, "data2.json");
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try(CSVReader csvReader = new CSVReader(new FileReader(fileName))){
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean <Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> list = csv.parse();
            return list;
        } catch (IOException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    private static String listToJson(List<Employee> list){
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Employee>>(){}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }
    private static void writeString(String json, String fileName) throws IOException {
        try(FileWriter fileWriter = new FileWriter(fileName)){
            fileWriter.write(json);
            fileWriter.flush();
        }
    }
    public static List<Employee> parseXML(String file) throws ParserConfigurationException, IOException, SAXException {
        List <Employee> listStaff = new ArrayList<>();
        Long id = 0L;
        String firstName = "";
        String lastName = "";
        String country = "";
        int age = 0;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(file));

        Node root = doc.getDocumentElement();
        NodeList firstLevelList = root.getChildNodes(); //получили список employee

        for (int i = 0; i < firstLevelList.getLength(); i++) {
            Node firstLevelNode = firstLevelList.item(i);

            if (firstLevelNode.getNodeType() == Node.ELEMENT_NODE) {
                Element firstLevelElement = (Element) firstLevelNode;
                NodeList secondLevelList = firstLevelElement.getChildNodes();
                for (int j = 0; j < secondLevelList.getLength(); j++) {
                    Node secondLevelNode = secondLevelList.item(j);
                    if (secondLevelNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element secondLevelElement = (Element) secondLevelNode;
                        String nodeName = secondLevelElement.getNodeName();
                        String nodeValue = secondLevelElement.getTextContent();

                        switch (nodeName) {
                            case ("id"):
                                id = Long.parseLong(nodeValue);
                                break;
                            case ("firstName"):
                                firstName = nodeValue;
                                break;
                            case ("lastName"):
                                lastName = nodeValue;
                                break;
                            case ("country"):
                                country = nodeValue;
                                break;
                            case ("age"):
                                age = Integer.parseInt(nodeValue);
                                break;
                        }
                    }
                }
                Employee employee = new Employee(id, firstName, lastName, country, age);
                listStaff.add(employee);
            }
        }
        return listStaff;
    }
}
