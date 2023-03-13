package MED.Algorithm;

import MED.Data.Interval;
import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;

import java.util.*;

public class GreedyVertexScheduler extends  Scheduler
{
    public GreedyVertexScheduler(double speed, double fullLengthTime, double crossingDelay, MEDAnimation.MorphType morphType)
    {
        super(speed,fullLengthTime,crossingDelay,morphType);
    }
    @Override
    public void schedule(MEDGraph g)
    {
        HashSet<MEDVertex> scheduled = new HashSet<>();
        HashSet<MEDVertex> toBeScheduled  = new HashSet<>();
        Iterator<MEDVertex> v_it = g.getVertices();
        while (v_it.hasNext())
        {
            toBeScheduled.add(v_it.next());
        }
        HashMap<MEDVertex,Double> startTimes = new HashMap<>();
        double period = 0;
        while (!toBeScheduled.isEmpty())
        {
            double firstEndTime = Double.MAX_VALUE;
            double startTime = 0;
            MEDVertex next = null;
            for (MEDVertex v : toBeScheduled)
            {
                double stubMorphLengthV = 0;
                Iterator<MEDEdge> e_it = g.getIncidentEdges(v.getID());
                while (e_it.hasNext())
                {
                    MEDEdge e = e_it.next();
                    stubMorphLengthV = Math.max(Utils.getStubMorphLength(e),stubMorphLengthV);
                }
                double stubMorphTimeV = stubMorphLengthV/speed;
                double fullMorphTimeV = 2*stubMorphTimeV + fullLengthTime;
                e_it = g.getIncidentEdges(v.getID());
                List<Interval> restrictedIntervals = new ArrayList<>();
                while (e_it.hasNext())
                {
                    MEDEdge e = e_it.next();
                    for (MEDVertex c : scheduled)
                    {
                        Iterator<MEDEdge> ec_it = g.getIncidentEdges(c.getID());
                        while (ec_it.hasNext())
                        {
                            MEDEdge ec = ec_it.next();
                            if (!ec.equals(e))
                            {
                                Interval restrictedInterval = Utils.getConflictInterval(e, ec, startTimes.get(c), speed, fullLengthTime, crossingDelay, morphType);
                                if (restrictedInterval != null) {
                                    restrictedIntervals.add(restrictedInterval);
                                }
                            }
                            else
                            {
                                double stubMorphLengthE = Utils.getStubMorphLength(e);
                                double stubMorphTimeE = stubMorphLengthE/speed;
                                double fullMorphTimeE = 2*stubMorphTimeE + fullLengthTime;
                                Interval restrictedInterval = new Interval(startTimes.get(c),startTimes.get(c)+fullMorphTimeE);
                                restrictedIntervals.add(restrictedInterval);
                            }
                        }
                    }
                }
                double possibleStart = Utils.earliestSpace(restrictedIntervals);
                double possibleEnd = possibleStart + fullMorphTimeV;
                if (possibleEnd < firstEndTime)
                {
                    firstEndTime = possibleEnd;
                    startTime = possibleStart;
                    next = v;
                }
            }
            startTimes.put(next,startTime);
            if (firstEndTime > period)
            {
                period = firstEndTime;
            }
            scheduled.add(next);
            toBeScheduled.remove(next);
        }
        for (MEDVertex v : scheduled)
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
