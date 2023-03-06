package MED.Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class MEDGraph
{
    private HashMap<String,MEDVertex> vertices;
    private HashSet<MEDEdge> edges;

    public MEDGraph()
    {
        this.vertices = new HashMap<String, MEDVertex>();
        this.edges = new HashSet<MEDEdge>();
    }
    public void addVertex(MEDVertex vertex)
    {
        if (!this.vertices.containsKey(vertex.getID()))
        {
            this.vertices.put(vertex.getID(),vertex);
        }
    }
    public void addEdge(MEDEdge edge)
    {
        if (this.vertices.containsKey(edge.getV1().getID()) && this.vertices.containsKey(edge.getV2().getID()))
        {
            this.edges.add(edge);
        }
    }
    public Iterator<MEDVertex> getVertices()
    {
        return this.vertices.values().iterator();
    }
    public Iterator<MEDEdge> getEdges()
    {
        return this.edges.iterator();
    }
}
