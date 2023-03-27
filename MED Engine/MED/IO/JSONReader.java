package MED.IO;

import MED.Data.Coordinate;
import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;
//import package json from maven
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

public class JSONReader
{
    private final String graphFile;
    private final String coordinateFile;
    private double defaultEdgeLength;
    public JSONReader(String graphFile, String coordinateFile, double defaultEdgeLength)
    {
        this.graphFile = graphFile;
        this.coordinateFile = coordinateFile;
        if (defaultEdgeLength >= 0 && defaultEdgeLength <= 1)
        {
            this.defaultEdgeLength = defaultEdgeLength;
        }
        else
        {
            this.defaultEdgeLength = 0.5;
        }
    }
    public MEDGraph read()
    {
        Path graphPath = new File(graphFile).toPath();
        Path coordinatePath = new File(coordinateFile).toPath();
        String graphString;
        String coordinateString;
        try
        {
            Stream<String> graphStream = Files.lines(graphPath,StandardCharsets.ISO_8859_1);
            Stream<String> coordinateStream = Files.lines(coordinatePath,StandardCharsets.US_ASCII);
            StringBuilder graphContentBuilder = new StringBuilder();
            StringBuilder coordinateContentBuilder = new StringBuilder();
            Iterator<String> graphIterator = graphStream.iterator();
            while (graphIterator.hasNext())
            {
                graphContentBuilder.append(graphIterator.next());
            }
            Iterator<String> coordinateIterator = coordinateStream.iterator();
            while (coordinateIterator.hasNext())
            {
                coordinateContentBuilder.append(coordinateIterator.next());
            }
            graphString = graphContentBuilder.toString();
            coordinateString = coordinateContentBuilder.toString();
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
            return null;
        }
        JSONArray coordinatesArray = new JSONArray(coordinateString);
        HashMap<String, Coordinate> coordinates = new HashMap<>();
        for (int i = 0; i < coordinatesArray.length(); i++)
        {
            String id = String.valueOf(coordinatesArray.getJSONObject(i).getInt("id"));
            double x = coordinatesArray.getJSONObject(i).getDouble("xCoord");
            double y = coordinatesArray.getJSONObject(i).getDouble("yCoord");
            coordinates.put(id, new Coordinate(x,y));
        }
        MEDGraph g = new MEDGraph();
        HashMap<String,MEDVertex> vertices = new HashMap<>();
        JSONObject graphObject = new JSONObject(graphString);
        JSONArray nodesArray = graphObject.getJSONArray("nodes");
        for (int i = 0; i < nodesArray.length(); i++)
        {
            String id = String.valueOf(nodesArray.getJSONObject(i).getInt("id"));
            double x = coordinates.get(id).getX();
            double y = coordinates.get(id).getY();
            MEDVertex v = new MEDVertex(id,x,y);
            vertices.put(id,v);
            g.addVertex(v);
        }
        JSONArray edgesArray = graphObject.getJSONArray("edges");
        for (int i = 0; i < edgesArray.length(); i++)
        {
            String source = String.valueOf(edgesArray.getJSONObject(i).getInt("source"));
            String target = String.valueOf(edgesArray.getJSONObject(i).getInt("target"));
            g.addEdge(new MEDEdge(vertices.get(source),vertices.get(target),defaultEdgeLength));
        }
        return g;
    }
}
