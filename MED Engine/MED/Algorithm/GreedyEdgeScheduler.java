package MED.Algorithm;

import MED.Data.Interval;
import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;

import java.util.*;

public class GreedyEdgeScheduler extends Scheduler
{
    private boolean edgeRepetition;
    public GreedyEdgeScheduler(double speed, double fullLengthTime, double crossingDelay, MEDAnimation.MorphType morphType, boolean duplicateEdges, boolean ignoreCrossingFreeEdges)
    {
        super(speed,fullLengthTime,crossingDelay,morphType,duplicateEdges,ignoreCrossingFreeEdges);
    }
    @Override
    public void schedule(MEDGraph g)
    {
        List<MEDEdge> edges_to_schedule = new ArrayList<>();
        Iterator<MEDEdge> e_it = g.getEdges();
        while (e_it.hasNext())
        {
            edges_to_schedule.add(e_it.next());
        }
        List<MEDEdge> crossingFreeEdges = new LinkedList<>();
        if (ignoreCrossingFreeEdges)
        {
            crossingFreeEdges = Utils.getCrossingFreeEdges(edges_to_schedule);
            edges_to_schedule.removeAll(crossingFreeEdges);
        }
        edges_to_schedule.sort(new StubMorphLengthComparator());
        List<List<Double>> startTimes = new ArrayList<List<Double>>(edges_to_schedule.size());
        List<MEDEdge> edges_still_to_schedule = new ArrayList<>();
        for (int i = 0; i < edges_to_schedule.size(); i++)
        {
            startTimes.add(new LinkedList<Double>());
            edges_still_to_schedule.add(edges_to_schedule.get(i));
        }
        double period = 0;
        boolean firstIteration = true;
        do
        {
            List<MEDEdge> finishedEdges = new LinkedList<>();
            for (int e = 0; e < edges_to_schedule.size(); e++)
            {
                List<Interval> restrictedIntervals = new ArrayList<>();
                double stubMorphLengthE = Utils.getStubMorphLength(edges_to_schedule.get(e));
                double stubMorphTimeE = stubMorphLengthE / speed;
                double fullMorphTimeE = 2 * stubMorphTimeE + fullLengthTime;
                for (int c = 0; c < edges_to_schedule.size(); c++)
                {
                    if (c != e)
                    {
                        for (double startTime : startTimes.get(c))
                        {
                            Interval restrictedInterval = Utils.getConflictInterval(edges_to_schedule.get(e), edges_to_schedule.get(c), startTime, speed, fullLengthTime, crossingDelay, morphType);
                            if (restrictedInterval != null)
                            {
                                restrictedIntervals.add(restrictedInterval);
                            }
                        }
                    }
                    else
                    {
                        for (double startTime : startTimes.get(c))
                        {
                            Interval restrictedInterval = new Interval(startTime-fullMorphTimeE,startTime+fullMorphTimeE);
                            restrictedIntervals.add(restrictedInterval);
                        }
                    }
                }
                double possibleStartTime = Utils.earliestSpace(restrictedIntervals);
                double endTime = possibleStartTime + fullMorphTimeE;
                if (firstIteration || (endTime < period))
                {
                    startTimes.get(e).add(possibleStartTime);
                }
                else
                {
                    finishedEdges.add(edges_to_schedule.get(e));
                }
                if (firstIteration)
                {
                    if (endTime > period)
                    {
                        period = endTime;
                    }
                }
            }
            firstIteration = false;
            edges_still_to_schedule.removeAll(finishedEdges);
        }
        while (!edges_still_to_schedule.isEmpty() && duplicateEdges);
        for (int i = 0; i < edges_to_schedule.size(); i++)
        {
            for (double startTime : startTimes.get(i))
            {
                MEDAnimation a = new MEDAnimation(startTime, speed, fullLengthTime, period, morphType);
                edges_to_schedule.get(i).addAnimation(a);
            }
        }
        for (MEDEdge e : crossingFreeEdges)
        {
            MEDAnimation a = new MEDAnimation(0,speed,fullLengthTime,period, MEDAnimation.MorphType.COMPLETE);
            e.addAnimation(a);
        }
        g.updateTimes();
    }
}