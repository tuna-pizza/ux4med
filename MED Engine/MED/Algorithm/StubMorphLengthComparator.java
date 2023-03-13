package MED.Algorithm;

import MED.Graph.MEDEdge;

import java.util.Comparator;

public class StubMorphLengthComparator implements Comparator<MEDEdge>
{
    @Override
    public int compare(MEDEdge a, MEDEdge b)
    {
        double lengthA = Utils.getStubMorphLength(a);
        double lengthB = Utils.getStubMorphLength(b);
        return (int)(lengthB - lengthA);
    }
}