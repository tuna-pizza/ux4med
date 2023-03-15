package MED.IO;

import MED.Data.Coordinate;
import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class BoardgameReader
{
    private final String graphFile;
    private double defaultEdgeLength;
    public BoardgameReader(String graphFile, double defaultEdgeLength)
    {
        this.graphFile = graphFile;
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
        String graphString;
        try
        {
            Stream<String> graphStream = Files.lines(graphPath, StandardCharsets.ISO_8859_1);
            StringBuilder graphContentBuilder = new StringBuilder();
            Iterator<String> graphIterator = graphStream.iterator();
            while (graphIterator.hasNext())
            {
                graphContentBuilder.append(graphIterator.next());
            }
            graphString = graphContentBuilder.toString();
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
            return null;
        }
        MEDGraph g = new MEDGraph();
        HashMap<String, MEDVertex> vertices = new HashMap<>();
        HashMap<String, List<String>> edges = new HashMap<>();
        JSONArray nodesArray =  new JSONArray(graphString);
        for (int i = 0; i < nodesArray.length(); i++)
        {
            String id = String.valueOf(nodesArray.getJSONObject(i).getInt("id"));
            MEDVertex v = new MEDVertex(id,0,0);
            vertices.put(id,v);
            edges.put(id,new LinkedList<>());
            g.addVertex(v);
        }
        for (int i = 0; i < nodesArray.length(); i++)
        {
            String source = String.valueOf(nodesArray.getJSONObject(i).getInt("id"));
            JSONObject recommendations = nodesArray.getJSONObject(i).getJSONObject("recommendations");
            JSONArray edgesArray = recommendations.getJSONArray("fans_liked");
            for (int j = 0; j < edgesArray.length(); j++)
            {
                String target = String.valueOf(edgesArray.getInt(j));
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
}
