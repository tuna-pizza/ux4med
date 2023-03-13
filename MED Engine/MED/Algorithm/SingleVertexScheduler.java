package MED.Algorithm;

import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class SingleVertexScheduler extends Scheduler
{
    public SingleVertexScheduler(double speed, double fullLengthTime, double crossingDelay, MEDAnimation.MorphType morphType)
    {
        super(speed,fullLengthTime,crossingDelay,morphType);
    }
    @Override
    public void schedule(MEDGraph g)
    {
        LinkedList<MEDVertex> vertices = new LinkedList<>();
        Iterator<MEDVertex> v_it = g.getVertices();
        while (v_it.hasNext())
        {
            vertices.add(v_it.next());
        }
        double time = 0;
        HashMap<MEDVertex,Double> startTimes = new HashMap<>();
        for (MEDVertex v : vertices)
        {
            startTimes.put(v,time);
            double stubMorphLengthV = 0;
            Iterator<MEDEdge> e_it = g.getIncidentEdges(v.getID());
            while (e_it.hasNext())
            {
                MEDEdge e = e_it.next();
                stubMorphLengthV = Math.max(Utils.getStubMorphLength(e),stubMorphLengthV);
            }
            double stubMorphTimeV = stubMorphLengthV/speed;
            double fullMorphTimeV = 2*stubMorphTimeV + fullLengthTime;
            time += fullMorphTimeV;
        }
        double period = time;
        for (MEDVertex v : vertices)
        {
            MEDAnimation a = new MEDAnimation(startTimes.get(v),speed,fullLengthTime,period,morphType);
            Iterator<MEDEdge> e_it = g.getIncidentEdges(v.getID());
            while (e_it.hasNext())
            {
                e_it.next().addAnimation(a);
            }
        }
        g.updateTimes();
    }
}
