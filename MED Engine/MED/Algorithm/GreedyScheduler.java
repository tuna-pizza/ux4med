package MED.Algorithm;

import MED.Engine.Coordinate;
import MED.Engine.MEDEngine;
import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;

import java.util.*;

public class GreedyScheduler
{
    private final MEDEngine engine;
    private final double speed;
    private final double fullLengthTime;
    private final double crossingDelay;
    private final MEDAnimation.MorphType morphType;
    public GreedyScheduler(double speed, double fullLengthTime, double crossingDelay, MEDAnimation.MorphType morphType)
    {
        this.speed=speed;
        this.fullLengthTime = fullLengthTime;
        this.crossingDelay = crossingDelay;
        this.morphType=morphType;
        engine = new MEDEngine();
    }
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
            double stubMorphLengthE = getStubMorphLength(edges.get(e));
            double stubMorphTimeE = stubMorphLengthE/speed;
            double fullMorphTimeE = 2*stubMorphTimeE + fullLengthTime;
            for (int c = 0; c < e; c++)
            {
                Coordinate crossing = computeCrossing(edges.get(e),edges.get(c));
                if (crossing != null)
                {
                    double morphingRatioE = getMorphingRatio(edges.get(e),crossing);
                    double crossingMorphTimeE1 = getTimeForMorph(morphingRatioE,stubMorphTimeE);
                    double crossingMorphTimeE2 = fullMorphTimeE - crossingMorphTimeE1;
                    double morphingRatioC = getMorphingRatio(edges.get(c),crossing);
                    double stubMorphLengthC = getStubMorphLength(edges.get(c));
                    double stubMorphTimeC = stubMorphLengthC/speed;
                    double fullMorphTimeC = 2*stubMorphTimeC + fullLengthTime;
                    double crossingMorphTimeC1 = getTimeForMorph(morphingRatioC,stubMorphTimeC);
                    double crossingMorphTimeC2 = fullMorphTimeC - crossingMorphTimeC1;
                    double startTimeC = startTimes.get(c);
                    double r1 = startTimeC + crossingMorphTimeC1 - crossingMorphTimeE2;
                    double r2 = startTimeC + crossingMorphTimeC2 - crossingMorphTimeE1;
                    if (r1 > r2)
                    {
                        double swap = r1;
                        r1 = r2;
                        r2 = swap;
                    }
                    //add some time between stubs passing the crossing
                    r1 = r1-crossingDelay;
                    r2 = r2+crossingDelay;
                    restrictedIntervals.add(new Interval(r1,r2));
                }
            }
            startTimes.add(e,earliestSpace(restrictedIntervals));
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
    private class Interval
    {
        private final double start;
        private final double end;
        public Interval(double start, double end)
        {
            this.start = start;
            this.end = end;
        }
        double getStart()
        {
            return start;
        }
        double getEnd()
        {
            return end;
        }
    }
    private class StubMorphLengthComparator implements Comparator<MEDEdge>
    {
        @Override
        public int compare(MEDEdge a, MEDEdge b)
        {
            double lengthA = getStubMorphLength(a);
            double lengthB = getStubMorphLength(b);
            return (int)(lengthA - lengthB);
        }
    }
    private class IntervalStartComparator implements Comparator<Interval>
    {
        @Override
        public int compare(Interval a, Interval b)
        {
            return (int)(a.getStart() - b.getStart());
        }
    }
    private double getStubMorphLength(MEDEdge e)
    {
        Coordinate mid = engine.computeMidPosition(e);
        Coordinate start = engine.computeStartPosition1(e);
        double midX = mid.getX();
        double midY = mid.getY();
        double startX = start.getX();
        double startY = start.getY();
        return distance(midX, midY, startX, startY);
    }
    private Coordinate computeCrossing(MEDEdge e, MEDEdge c)
    {
        Coordinate startE1 = engine.computeStartPosition1(e);
        Coordinate startE2 = engine.computeStartPosition2(e);
        double leftE = Math.min(startE1.getX(), startE2.getX());
        double rightE = Math.max(startE1.getX(), startE2.getX());
        double bottomE = Math.min(startE1.getY(), startE2.getY());
        double topE = Math.max(startE1.getY(), startE2.getY());
        double aE=0;
        double bE=0;
        boolean verticalE = false;
        if (leftE != rightE)
        {
            aE = (startE1.getY()-startE2.getY())/(startE1.getX()-startE2.getX());
            bE = startE1.getY() - aE * startE1.getX();
        }
        else
        {
            verticalE = true;
        }
        Coordinate startC1 = engine.computeStartPosition1(c);
        Coordinate startC2 = engine.computeStartPosition2(c);
        double leftC = Math.min(startC1.getX(), startC2.getX());
        double rightC = Math.max(startC1.getX(), startC2.getX());
        double bottomC = Math.min(startC1.getY(), startC2.getY());
        double topC = Math.max(startC1.getY(), startC2.getY());
        double aC = 0;
        double bC = 0;
        boolean verticalC = false;
        if (leftC != rightC)
        {
            aC = (startC1.getY()-startC2.getY())/(startC1.getX()-startC2.getX());
            bC = startC1.getY() - aC * startC1.getX();
        }
        else
        {
            verticalC = true;
        }
        if (verticalC && verticalE)
        {
            return null;
        }
        if (verticalC)
        {
            double leftSwap = leftC;
            double rightSwap = rightC;
            double aSwap = aC;
            double bSwap = bC;
            leftC = leftE;
            rightC = rightE;
            aC = aE;
            bC= bE;
            leftE = leftSwap;
            rightE = rightSwap;
            bottomE = bottomC;
            topE = topC;
            aE = aSwap;
            bE = bSwap;
            verticalE = true;
        }
        if (verticalE)
        {
            if (leftC <= leftE && rightC >= leftE)
            {
                double y = lineAt(aC,bC,leftE);
                if (topE >= y && y >= bottomE)
                {
                    return new Coordinate(leftE,y);
                }
            }
        }
        else
        {
            double x = (bC-bE)/(aE-aC);
            if (leftC <= x && x <= rightC)
            {
                if (leftE <= x && x <= rightE)
                {
                    double y = lineAt(aC,bC,x);
                    return new Coordinate(x,y);
                }
            }
        }
        return null;
    }
    private double lineAt(double a, double b, double x)
    {
        return a*x +b;
    }
    private double distance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }
    private double distance(Coordinate p, Coordinate q)
    {
        return distance(p.getX(),p.getY(),q.getX(),q.getY());
    }
    private double getMorphingRatio(MEDEdge e, Coordinate p)
    {
        double stubMorphingLength = getStubMorphLength(e);
        Coordinate start1 = engine.computeStartPosition1(e);
        Coordinate start2 = engine.computeStartPosition2(e);
        double distance = Math.min(distance(start1,p),distance(start2,p));
        return distance/stubMorphingLength;
    }
    private double getTimeForMorph(double morphingRatio, double stubMorphTime)
    {
        switch (morphType)
        {
            case LINEAR:
            {
                return morphingRatio*stubMorphTime;
            }
            case SINE:
            {
                //morphingRatio = sin(time * pi/2)
                //2 arcsin(morphingRatio)/pi = time
                return ((2*Math.asin(morphingRatio))/Math.PI)*stubMorphTime;
            }
            case INVERSESINE:
            {
                //morphingRatio = 1-Math.cos(time*Math.PI/2);
                return (2*Math.acos(1-morphingRatio)/Math.PI)*stubMorphTime;
            }
            case COSINE:
            {
                //morphingRatio = (1-Math.cos(time*Math.PI))/2;
                //1-2morphingRatio = Math.cos(time*Math.PI)
                double timeRatio = Math.acos(1-2*morphingRatio);
                if (timeRatio < 0)
                {
                    timeRatio += Math.PI;
                }
                return (timeRatio/Math.PI)*stubMorphTime;
            }
            default:
            {
                return -1;
            }
        }
    }
    private double earliestSpace(List<Interval> intervals)
    {
        double t = 0;
        intervals.sort(new IntervalStartComparator());
        for (int i=0; i < intervals.size(); i++)
        {
            if (intervals.get(i).getEnd() < t)
            {
                continue;
            }
            else if (t < intervals.get(i).getStart())
            {
                return t;
            }
            else
            {
                t = intervals.get(i).getEnd();
            }
        }
        return t;
    }
}
