package tp.vrp.parser;


import tp.vrp.Data.Node;
import tp.vrp.Data.Request;
import tp.vrp.Data.Vehicule;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLParser {
    private List<Node> nodeList;
    private List<Request> requestList;
    private List<Vehicule> vehiculeList;

    public XMLParser() {
        this.nodeList = new ArrayList<>();
        this.requestList = new ArrayList<>();
        this.vehiculeList = new ArrayList<>();
    }

    public void parseXMLFile(String filePath) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(filePath));


            Request currentRequest = null;
            Node currentNode = null;
            Vehicule currentVehicle = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();

                    switch (elementName) {
                        case "node":
                            currentNode = new Node();
                            Iterator<Attribute> attributes2 = startElement.getAttributes();
                            while (attributes2.hasNext()) {
                                Attribute attribute = attributes2.next();
                                String attrName = attribute.getName().getLocalPart();
                                String attrValue = attribute.getValue();
                                if ("id".equals(attrName)) {
                                    currentNode.setId(Integer.parseInt(attrValue));
                                } else if ("type".equals(attrName)) {
                                    currentNode.setType(Integer.parseInt(attrValue));
                                }
                            }

                            break;
                        case "cx":
                            event = eventReader.nextEvent();
                            if (currentNode != null) {
                                currentNode.setLongitude(Double.parseDouble(event.asCharacters().getData()));
                            }
                            break;
                        case "cy":
                            event = eventReader.nextEvent();
                            if (nodeList.size() == 0)
                            {//nodeList.add(0,currentNode);
                            }
                            if (currentNode != null) {
                                currentNode.setLatitude(Double.parseDouble(event.asCharacters().getData()));
                                nodeList.add(currentNode);
                            }
                            break;

                        case "request":
                            currentRequest = new Request();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attrName = attribute.getName().getLocalPart();
                                String attrValue = attribute.getValue();
                                if ("id".equals(attrName)) {
                                    currentRequest.setId(Integer.parseInt(attrValue));
                                } else if ("node".equals(attrName)) {
                                    currentRequest.setNode(Integer.parseInt(attrValue));
                                }
                            }
                            break;
                        case "quantity":
                            event = eventReader.nextEvent();


                            if (currentRequest != null) {
                                currentRequest.setQuantity(Double.parseDouble(event.asCharacters().getData()));
                                requestList.add(currentRequest.getId(), currentRequest);
                            }
                            break;
                        case "vehicle_profile":
                            currentVehicle = new Vehicule();
                            Iterator<Attribute> attributes3 = startElement.getAttributes();
                            while (attributes3.hasNext()) {
                                Attribute attribute = attributes3.next();
                                String attrName = attribute.getName().getLocalPart();
                                String attrValue = attribute.getValue();
                                if ("type".equals(attrName)) {
                                    currentVehicle.setVehicleProfile(Integer.parseInt(attrValue));
                                }
                            }
                            break;
                        case "departure_node":
                            event = eventReader.nextEvent();
                            if (currentVehicle != null) {
                                currentVehicle.setDepartureNode(Integer.parseInt(event.asCharacters().getData()));
                            }
                            break;
                        case "arrival_node":
                            event = eventReader.nextEvent();
                            if (currentVehicle != null) {
                                currentVehicle.setArrivalNode(Integer.parseInt(event.asCharacters().getData()));
                            }
                            break;
                        case "capacity":
                            event = eventReader.nextEvent();
                            if (currentVehicle != null) {
                                currentVehicle.setCapacityInitial((int) Double.parseDouble(event.asCharacters().getData()));
                                vehiculeList.add(currentVehicle);
                            }
                            break;

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public List<Request> getRequestList() {
        return requestList;
    }
    public List<Vehicule> getVehicleList() {
        return vehiculeList;
    }
}