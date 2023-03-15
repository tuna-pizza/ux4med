package MED.IO;

import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class CSVReader
{
    private final String nodeFile;
    private final String edgeFile;
    private double defaultEdgeLength;
    public CSVReader(String nodeFile, String edgeFile, double defaultEdgeLength)
    {
        this.nodeFile = nodeFile;
        this.edgeFile = edgeFile;
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
        BufferedReader nodeReader;
        BufferedReader edgeReader;
        try
        {
            nodeReader = new BufferedReader(new FileReader(nodeFile));
            edgeReader = new BufferedReader(new FileReader(edgeFile));
            MEDGraph g = new MEDGraph();
            HashMap<String, MEDVertex> vertices = new HashMap<>();
            HashMap<String, List<String>> edges = new HashMap<>();
            String nodeLine;
            while ((nodeLine = nodeReader.readLine()) != null)
            {
                String[] nodeData = nodeLine.split(",");
                if (!nodeData[0].equals("id") && (nodeData[1].equals("label") || nodeData[1].equals("group")))
                {
                    String id = nodeData[0];
                    MEDVertex v = new MEDVertex(id,0,0);
                    vertices.put(id, v);
                    edges.put(id, new LinkedList<>());
                    g.addVertex(v);
                }
            }
            nodeReader.close();
            String edgeLine;
            while ((edgeLine = edgeReader.readLine()) != null)
            {
                String[] edgeData = edgeLine.split(",");
                if (!edgeData[0].equals("source"))
                {
                    String source = edgeData[0];
                    String target = edgeData[1];
                    if (edges.containsKey(source) && edges.containsKey(target))
                    {
                        if (!edges.get(source).contains(target) && !edges.get(target).contains(source))
                        {
                            edges.get(source).add(target);
                            g.addEdge(new MEDEdge(vertices.get(source), vertices.get(target), defaultEdgeLength));
                        }
                    }
                }
            }
            return g;
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
