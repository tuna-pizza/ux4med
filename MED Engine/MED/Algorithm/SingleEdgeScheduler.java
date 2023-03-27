package MED.Algorithm;

import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class SingleEdgeScheduler extends Scheduler
{
    public SingleEdgeScheduler(double speed, double fullLengthTime, double crossingDelay, MEDAnimation.MorphType morphType)
    {
        super(speed,fullLengthTime,crossingDelay,morphType,false,false);
    }
    @Override
    public void schedule(MEDGraph g)
    {
        LinkedList<MEDEdge> edges = new LinkedList<>();
        Iterator<MEDEdge> e_it = g.getEdges();
        while (e_it.hasNext())
        {
            edges.add(e_it.next());
        }
        double time = 0;
        HashMap<MEDEdge,Double> startTimes = new HashMap<>();
        for (MEDEdge e : edges)
        {
            startTimes.put(e,time);
            double stubMorphLengthE = Utils.getStubMorphLength(e);
            double stubMorphTimeE = stubMorphLengthE/speed;
            double fullMorphTimeE = 2*stubMorphTimeE + fullLengthTime;
            time += fullMorphTimeE;
        }
        double period = time;
        for (MEDEdge e : edges)
        {
            MEDAnimation a = new MEDAnimation(startTimes.get(e),speed,fullLengthTime,period,morphType);
            e.addAnimation(a);
        }
        g.updateTimes();
    }
}