package MED.Algorithm;

import MED.Data.Interval;
import MED.Data.Coordinate;
import MED.Engine.MEDEngine;
import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import com.yworks.yfiles.utils.ICollection;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.LinkedList;
import java.util.List;

public final class Utils
{
    static Utils utils;
    private MEDEngine engine;
    private Utils()
    {
        this.engine = new MEDEngine();
    }
    public MEDEngine getEngine()
    {
        return engine;
    }
    private static void init()
    {
        if (utils == null)
        {
            utils = new Utils();
        }
    }
    public static double lineAt(double a, double b, double x)
    {
        return a*x +b;
    }
    public static double distance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }
    public static double distance(Coordinate p, Coordinate q)
    {
        return distance(p.getX(),p.getY(),q.getX(),q.getY());
    }
    public static double getMorphingRatio(MEDEdge e, Coordinate p)
    {
        init();
        double stubMorphingLength = getStubMorphLength(e);
        Coordinate start1 = utils.getEngine().computeStartPosition1(e);
        Coordinate start2 =  utils.getEngine().computeStartPosition2(e);
        double distance = Math.min(distance(start1,p),distance(start2,p));
        return distance/stubMorphingLength;
    }
    public static double getStubMorphLength(MEDEdge e)
    {
        init();
        Coordinate mid = utils.getEngine().computeMidPosition(e);
        Coordinate start = utils.getEngine().computeStartPosition1(e);
        double midX = mid.getX();
        double midY = mid.getY();
        double startX = start.getX();
        double startY = start.getY();
        return distance(midX, midY, startX, startY);
    }

    public static Coordinate computeCrossing(MEDEdge e, MEDEdge c)
    {
        init();
        Coordinate startE1 = utils.getEngine().computeStartPosition1(e);
        Coordinate startE2 = utils.getEngine().computeStartPosition2(e);
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
        Coordinate startC1 = utils.getEngine().computeStartPosition1(c);
        Coordinate startC2 = utils.getEngine().computeStartPosition2(c);
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

    public static double getTimeForMorph(double morphingRatio, double stubMorphTime, MEDAnimation.MorphType morphType)
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
            case EASING:
                double timeRatio = Utils.cubicBezierCurveAt(0.1,0.25,1,0.25,morphingRatio);
                return timeRatio*stubMorphTime;
            default:
            {
                return -1;
            }
        }
    }

    public static double earliestSpace(List<Interval> intervals)
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

    public static Interval getConflictInterval(MEDEdge e, MEDEdge c, double startTimeC, double speed, double fullLengthTime, double crossingDelay, MEDAnimation.MorphType morphType)
    {
        double stubMorphLengthE = Utils.getStubMorphLength(e);
        double stubMorphTimeE = stubMorphLengthE/speed;
        double fullMorphTimeE = 2*stubMorphTimeE + fullLengthTime;
        Coordinate crossing = Utils.computeCrossing(e,c);
        if (crossing != null)
        {
            double morphingRatioE = Utils.getMorphingRatio(e,crossing);
            double crossingMorphTimeE1 = Utils.getTimeForMorph(morphingRatioE,stubMorphTimeE,morphType);
            double crossingMorphTimeE2 = fullMorphTimeE - crossingMorphTimeE1;
            double morphingRatioC = Utils.getMorphingRatio(c,crossing);
            double stubMorphLengthC = Utils.getStubMorphLength(c);
            double stubMorphTimeC = stubMorphLengthC/speed;
            double fullMorphTimeC = 2*stubMorphTimeC + fullLengthTime;
            double crossingMorphTimeC1 = Utils.getTimeForMorph(morphingRatioC,stubMorphTimeC,morphType);
            double crossingMorphTimeC2 = fullMorphTimeC - crossingMorphTimeC1;
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
            return new Interval(r1,r2);
        }
        else
        {
            return null;
        }
    }

    public static LinkedList<MEDEdge> getCrossingFreeEdges(List<MEDEdge> edges)
    {
        LinkedList<MEDEdge> crossingFreeEdges = new LinkedList<>();
        for (MEDEdge e1 : edges)
        {
            boolean isCrossing = false;
            for (MEDEdge e2 : edges)
            {
                if (!e1.equals(e2))
                {
                    if (Utils.computeCrossing(e1, e2) != null)
                    {
                        isCrossing = true;
                    }
                }
            }
            if (!isCrossing)
            {
                crossingFreeEdges.add(e1);
            }
        }
        return crossingFreeEdges;
    }

    /**
     * Computes the y-value of the cubic Bezier curve from (0,0) to (1,1) via control points (x1,y1) and (x2,y2) at x-coordinate x
     */
    static public double cubicBezierCurveAt(double x1,double y1,double x2,double y2,double x)
    {
        //TODO: implement this function
        return 0.5;
    }
}
