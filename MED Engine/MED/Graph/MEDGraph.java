package MED.Graph;

import MED.Engine.MEDEngine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class MEDGraph
{
    private final HashMap<String,MEDVertex> vertices;
    private final HashSet<MEDEdge> edges;
    private double firstStart;
    private double lastEnd;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

    public MEDGraph()
    {
        this.vertices = new HashMap<>();
        this.edges = new HashSet<>();
        this.minX = Double.MAX_VALUE;
        this.minY = Double.MAX_VALUE;
        this.maxX = Double.MIN_VALUE;
        this.maxY = Double.MIN_VALUE;
        this.firstStart = Double.MAX_VALUE;
        this.lastEnd = Double.MIN_VALUE;
    }
    public MEDVertex getVertex(String id)
    {
        if (vertices.containsKey(id))
        {
            return vertices.get(id);
        }
        else
        {
            return null;
        }
    }
    public double getFirstStart()
    {
        return firstStart;
    }
    public double getLastEnd()
    {
        return lastEnd;
    }
    public double getMinX()
    {
        return minX;
    }
    public double getMinY()
    {
        return minY;
    }
    public double getMaxX()
    {
        return maxX;
    }
    public double getMaxY()
    {
        return maxY;
    }
    public void addVertex(MEDVertex vertex)
    {
        if (vertex.getX()>maxX)
        {
            maxX = vertex.getX();
        }
        if (vertex.getY()>maxY)
        {
            maxY = vertex.getY();
        }
        if (vertex.getX()<minX)
        {
            minX = vertex.getX();
        }
        if (vertex.getY()<minY)
        {
            minY = vertex.getY();
        }
        if (!this.vertices.containsKey(vertex.getID()))
        {
            this.vertices.put(vertex.getID(),vertex);
        }
    }
    public void addEdge(MEDEdge edge)
    {
        updateTimes();
        if (this.vertices.containsKey(edge.getV1().getID()) && this.vertices.containsKey(edge.getV2().getID()))
        {
            this.edges.add(edge);
        }
    }
    public void updateTimes()
    {
        MEDEngine engine = new MEDEngine();
        for (MEDEdge edge : this.edges)
        {
            Iterator<MEDAnimation> a_it = edge.getAnimations();
            while (a_it.hasNext())
            {
                MEDAnimation a = a_it.next();
                if (a.getStartTime() < firstStart)
                {
                    firstStart = a.getStartTime();
                }
                double endTime = a.getStartTime() + engine.getTotalDuration(edge, a);
                if (endTime > lastEnd)
                {
                    lastEnd = endTime;
                }
            }
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
