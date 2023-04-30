package MED.IO;

import MED.Data.Region;
import MED.Graph.MEDGraph;
import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import MED.Graph.MEDVertex;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import org.xml.sax.*;

public class MEDmlReader
{
    private final String file;
    public enum InputType {MEDml, yEdOld, yEdNew};
    private InputType inputType;
    private double defaultEdgeLength;
    private String dataFieldID;
    private LinkedList<Region> regions;
    public MEDmlReader(String file, InputType inputType, double defaultEdgeLength)
    {
        this.file = file;
        this.dataFieldID = "d5";
        this.inputType = inputType;
        if (defaultEdgeLength >= 0 && defaultEdgeLength <= 1)
        {
            this.defaultEdgeLength = defaultEdgeLength;
        }
        else
        {
            this.defaultEdgeLength = 0.5;
        }
    }
    public void setDataFieldID(String dataFieldID)
    {
        this.dataFieldID = dataFieldID;
    }
    public MEDGraph read()
    {
        MEDGraph g = new MEDGraph();
        Document document;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(new File(file));
            Element root = document.getDocumentElement();

            Node graph = root.getFirstChild();
            boolean graphFound = false;
            while (graph != null && !graphFound)
            {
                if(!graph.getNodeName().equals("graph"))
                {
                    graph = graph.getNextSibling();
                }
                else
                {
                    graphFound = true;
                }
            }
            if (graph != null)
            {
                readVertices(graph, g);
                readEdges(graph, g);
                readRegions(graph,g);
            }
        }
        catch (ParserConfigurationException | SAXException | IOException ex)
        {
            System.err.println(ex.getMessage());
        }
        return g;
    }
    public List<Region> getRegions()
    {
        return regions;
    }

    private void readRegions(Node graph, MEDGraph g)
    {
        this.regions = null;
        Node region = graph.getFirstChild();
        while (region != null)
        {
            if (region.getNodeName().equals("region"))
            {
                ArrayList<Integer> xCoords = new ArrayList<>();
                ArrayList<Integer> yCoords = new ArrayList<>();
                String color = "#AFFE07";
                NamedNodeMap regionAttributes = region.getAttributes();
                if (regionAttributes.getNamedItem("color") != null)
                {
                    color = regionAttributes.getNamedItem("color").getTextContent();
                }
                Node point = region.getFirstChild();
                while (point != null)
                {
                    if (point.getNodeName().equals("point"))
                    {
                        NamedNodeMap pointAttributes = point.getAttributes();
                        int x = 0;
                        int y = 0;
                        if (pointAttributes.getNamedItem("x") != null)
                        {
                            x = (int)Double.parseDouble(pointAttributes.getNamedItem("x").getTextContent());
                        }
                        if (pointAttributes.getNamedItem("y") != null)
                        {
                            y = (int)Double.parseDouble(pointAttributes.getNamedItem("y").getTextContent());
                        }
                        xCoords.add(x);
                        yCoords.add(y);
                    }
                    point = point.getNextSibling();
                }
                if (regions == null)
                {
                    regions = new LinkedList<>();
                }
                regions.add(new Region(xCoords,yCoords,color));
            }
            region = region.getNextSibling();
        }
    }
    private void readEdges(Node graph, MEDGraph g)
    {
        Node edge = graph.getFirstChild();
        while (edge != null)
        {
            if (edge.getNodeName().equals("edge"))
            {
                NamedNodeMap edgeAttributes = edge.getAttributes();
                String sourceID = edgeAttributes.getNamedItem("source").getTextContent();
                String targetID = edgeAttributes.getNamedItem("target").getTextContent();
                String color = "#000000";
                if (edgeAttributes.getNamedItem("color") != null)
                {
                    color = edgeAttributes.getNamedItem("color").getTextContent();
                }
                double minLength = defaultEdgeLength;
                if (edgeAttributes.getNamedItem("minLength") != null)
                {
                    minLength = Double.parseDouble(edgeAttributes.getNamedItem("minLength").getTextContent());
                }
                MEDVertex v1 = g.getVertex(sourceID);
                MEDVertex v2 = g.getVertex(targetID);
                if (v1 != null && v2 != null)
                {
                    MEDEdge e = new MEDEdge(v1,v2,minLength,color);
                    Node animation = edge.getFirstChild();
                    while (animation != null)
                    {
                        if (animation.getNodeName().equals("animation"))
                        {
                            NamedNodeMap animationAttributes = animation.getAttributes();
                            double startTime = 0;
                            if (animationAttributes.getNamedItem("startTime") != null)
                            {
                                startTime = Double.parseDouble(animationAttributes.getNamedItem("startTime").getTextContent());
                            }
                            double speed = 0.5;
                            if (animationAttributes.getNamedItem("speed") != null)
                            {
                                speed = Double.parseDouble(animationAttributes.getNamedItem("speed").getTextContent());
                            }
                            double fullLengthTime = 100;
                            if (animationAttributes.getNamedItem("fullLengthTime") != null)
                            {
                                fullLengthTime = Double.parseDouble(animationAttributes.getNamedItem("fullLengthTime").getTextContent());
                            }
                            double period = 2500;
                            if (animationAttributes.getNamedItem("period") != null)
                            {
                                period = Double.parseDouble(animationAttributes.getNamedItem("period").getTextContent());
                            }
                            String morphType = "N/A";
                            if (animationAttributes.getNamedItem("morphType") != null)
                            {
                                morphType = animationAttributes.getNamedItem("morphType").getTextContent();
                            }
                            e.addAnimation(new MEDAnimation(startTime,speed,fullLengthTime,period,morphType));
                        }
                        animation = animation.getNextSibling();
                    }
                    g.addEdge(e);
                }
            }
            edge = edge.getNextSibling();
        }
    }

    private void readVertices(Node graph, MEDGraph g)
    {
        Node vertex = graph.getFirstChild();
        while (vertex != null)
        {
            if (vertex.getNodeName().equals("node"))
            {
                NamedNodeMap vertexAttributes = vertex.getAttributes();
                String id = vertexAttributes.getNamedItem("id").getTextContent();
                double x,y;
                if (inputType == InputType.yEdOld)
                {
                    Node data = vertex.getFirstChild();
                    while (data != null)
                    {
                        if (data.getNodeName().equals("data"))
                        {
                            NamedNodeMap dataAttributes = data.getAttributes();
                            if (dataAttributes.getNamedItem("key") != null)
                            {
                                if (dataAttributes.getNamedItem("key").getTextContent().equals(dataFieldID))
                                {
                                    Node yShapeNode = data.getFirstChild();
                                    while (yShapeNode != null)
                                    {
                                        if (yShapeNode.getNodeName().equals("y:ShapeNode"))
                                        {
                                            Node yGeometry = yShapeNode.getFirstChild();
                                            while (yGeometry != null) {
                                                if (yGeometry.getNodeName().equals("y:Geometry"))
                                                {
                                                    vertexAttributes = yGeometry.getAttributes();
                                                    break;
                                                }
                                                yGeometry = yGeometry.getNextSibling();
                                            }
                                            break;
                                        }
                                        yShapeNode = yShapeNode.getNextSibling();
                                    }
                                    break;
                                }
                            }
                        }
                        data = data.getNextSibling();
                    }
                }
                if (inputType == InputType.yEdNew)
                {
                    Node data = vertex.getFirstChild();
                    while (data != null)
                    {
                        if (data.getNodeName().equals("data"))
                        {
                            NamedNodeMap dataAttributes = data.getAttributes();
                            if (dataAttributes.getNamedItem("key") != null)
                            {
                                if (dataAttributes.getNamedItem("key").getTextContent().equals(dataFieldID))
                                {
                                    Node yShapeNode = data.getFirstChild();
                                    while (yShapeNode != null)
                                    {
                                        if (yShapeNode.getNodeName().equals("y:SVGNode"))
                                        {
                                            Node yGeometry = yShapeNode.getFirstChild();
                                            while (yGeometry != null) {
                                                if (yGeometry.getNodeName().equals("y:Geometry"))
                                                {
                                                    vertexAttributes = yGeometry.getAttributes();
                                                    break;
                                                }
                                                yGeometry = yGeometry.getNextSibling();
                                            }
                                            break;
                                        }
                                        yShapeNode = yShapeNode.getNextSibling();
                                    }
                                    break;
                                }
                            }
                        }
                        data = data.getNextSibling();
                    }
                }
                if (vertexAttributes.getNamedItem("x") != null)
                {
                    x = Double.parseDouble(vertexAttributes.getNamedItem("x").getTextContent());
                }
                else
                {
                    x = 0;
                }
                if (vertexAttributes.getNamedItem("y") != null)
                {
                    y = Double.parseDouble(vertexAttributes.getNamedItem("y").getTextContent());
                }
                else
                {
                    y = 0;
                }
                String color = "#808080";
                if (vertexAttributes.getNamedItem("color") != null)
                {
                    color = vertexAttributes.getNamedItem("color").getTextContent();
                }
                g.addVertex(new MEDVertex(id, x, y, color));
            }
            vertex = vertex.getNextSibling();
        }
    }
}
