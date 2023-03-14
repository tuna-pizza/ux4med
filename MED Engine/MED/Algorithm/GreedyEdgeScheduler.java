package MED.Algorithm;

import MED.Data.Interval;
import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;

import java.util.*;

public class GreedyEdgeScheduler extends Scheduler
{
    public GreedyEdgeScheduler(double speed, double fullLengthTime, double crossingDelay, MEDAnimation.MorphType morphType)
    {
        super(speed,fullLengthTime,crossingDelay,morphType);
    }
    @Override
    public void schedule(MEDGraph g)
    {
        List<MEDEdge> edges = new ArrayList<>();
        Iterator<MEDEdge> e_it = g.getEdges();
        while (e_it.hasNext())
        {
            edges.add(e_it.next());
        }
        edges.sort(new StubMorphLengthComparator());
        List<Double> startTimes = new ArrayList<>(edges.size());
        double period = 0;
        for (int e = 0; e < edges.size(); e++)
        {
            List<Interval> restrictedIntervals = new ArrayList<>();
            double stubMorphLengthE = Utils.getStubMorphLength(edges.get(e));
            double stubMorphTimeE = stubMorphLengthE/speed;
            double fullMorphTimeE = 2*stubMorphTimeE + fullLengthTime;
            for (int c = 0; c < e; c++)
            {
                Interval restrictedInterval = Utils.getConflictInterval(edges.get(e),edges.get(c),startTimes.get(c),speed,fullLengthTime,crossingDelay,morphType);
                if (restrictedInterval != null)
                {
                    restrictedIntervals.add(restrictedInterval);
                }
            }
            startTimes.add(e,Utils.earliestSpace(restrictedIntervals));
            double endTime = startTimes.get(e) + fullMorphTimeE;
            if (endTime > period)
            {
                period = endTime;
            }
        }
        for (int i = 0; i < edges.size(); i++)
        {
            MEDAnimation a = new MEDAnimation(startTimes.get(i),speed,fullLengthTime,period,morphType);
            edges.get(i).addAnimation(a);
        }
        g.updateTimes();
    }
}