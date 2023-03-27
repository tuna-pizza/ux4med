package MED.Algorithm;

import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;

import java.util.Comparator;
import java.util.Iterator;

public class IncidentEdgeLengthComparator implements Comparator<MEDVertex>
{
    private MEDGraph g;
    public IncidentEdgeLengthComparator(MEDGraph g)
    {
        this.g = g;
    }
    @Override
    public int compare(MEDVertex a, MEDVertex b)
    {
        double lengthA = 0;
        Iterator<MEDEdge> a_it = g.getIncidentEdges(a.getID());
        while (a_it.hasNext())
        {
            lengthA = Math.max(lengthA,Utils.getStubMorphLength(a_it.next()));
        }
        double lengthB = 0;
        Iterator<MEDEdge> b_it = g.getIncidentEdges(b.getID());
        while (b_it.hasNext())
        {
            lengthB = Math.max(lengthB,Utils.getStubMorphLength(b_it.next()));
        }
        return (int)(lengthB - lengthA);
    }
}
