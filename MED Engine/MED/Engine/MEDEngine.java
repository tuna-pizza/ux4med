package MED.Engine;

import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;

import java.util.Iterator;

public class MEDEngine
{
    public MEDEngine()
    {

    }

    public Coordinate computeStubPosition1(MEDEdge e, long time)
    {
        Coordinate mid = computeMidPosition(e);
        Coordinate start = computeStartPosition1(e);
        MEDAnimation a = getActiveAnimation(e,time);
        if (a != null)
        {
            double stubExtensionLengthRatio = getStubExtensionLengthRatio(e,a,time);
            double currentX = start.getX() + (mid.getX()-start.getX())*stubExtensionLengthRatio;
            double currentY = start.getY() + (mid.getY()-start.getY())*stubExtensionLengthRatio;
            return new Coordinate(currentX,currentY);
        }
        else
        {
            return start;
        }
    }
    public Coordinate computeStubPosition2(MEDEdge e, long time)
    {
        Coordinate mid = computeMidPosition(e);
        Coordinate start = computeStartPosition2(e);
        MEDAnimation a = getActiveAnimation(e,time);
        if (a != null)
        {
            double stubExtensionLengthRatio = getStubExtensionLengthRatio(e,a,time);
            double currentX = start.getX() + (mid.getX()-start.getX())*stubExtensionLengthRatio;
            double currentY = start.getY() + (mid.getY()-start.getY())*stubExtensionLengthRatio;
            return new Coordinate(currentX,currentY);
        }
        else
        {
            return start;
        }
    }
    private Coordinate computeMidPosition(MEDEdge e)
    {
        double x1 = e.getV1().getX();
        double y1 = e.getV1().getY();
        double x2 = e.getV2().getX();
        double y2 = e.getV2().getY();
        double midX = (x1+x2)/2;
        double midY = (y1+y2)/2;
        return new Coordinate(midX,midY);
    }
    private Coordinate computeStartPosition1(MEDEdge e)
    {
        double x1 = e.getV1().getX();
        double y1 = e.getV1().getY();
        Coordinate mid = computeMidPosition(e);
        double startX1 = x1 + (mid.getX()-x1)*e.getMinLength();
        double startY1 = y1 + (mid.getY()-y1)*e.getMinLength();
        return new Coordinate(startX1,startY1);
    }
    private Coordinate computeStartPosition2(MEDEdge e)
    {
        double x2 = e.getV2().getX();
        double y2 = e.getV2().getY();
        Coordinate mid = computeMidPosition(e);
        double startX2 = x2 + (mid.getX()-x2)*e.getMinLength();
        double startY2 = y2 + (mid.getY()-y2)*e.getMinLength();
        return new Coordinate(startX2,startY2);
    }
    private MEDAnimation getActiveAnimation(MEDEdge e, long time)
    {
        MEDAnimation a = null;
        Iterator<MEDAnimation> a_it = e.getAnimations();
        while (a_it.hasNext())
        {
            MEDAnimation nextA = a_it.next();
            if (isActive(e, nextA, time) || (a == null))
            {
                a = nextA;
            }
        }
        return a;
    }
    private boolean isActive(MEDEdge e, MEDAnimation a,long time)
    {
        if (a.getMorphType().equals(MEDAnimation.MorphType.PED))
        {
            return true;
        }
        else
        {
            return (getStubExtensionLengthRatio(e,a,time)>0);
        }
    }
    private double getStubExtensionLengthRatio(MEDEdge e, MEDAnimation a, long time)
    {
        Coordinate mid = computeMidPosition(e);
        Coordinate start = computeStartPosition1(e);
        double morphDuration = Math.sqrt(Math.pow(mid.getX()-start.getX(),2)+Math.pow(mid.getY()-start.getY(),2))/a.getSpeed();
        double totalDuration = 2*morphDuration + a.getFullLengthTime();
        if (a.getMorphType().equals(MEDAnimation.MorphType.COMPLETE))
        {
            return 1;
        }
        if (a.getMorphType().equals(MEDAnimation.MorphType.PED))
        {
            return 0;
        }
        if (a.getMorphType().equals(MEDAnimation.MorphType.NONE))
        {
            return 0;
        }
        double timeForA = time%a.getPeriod() - a.getStartTime();
        if (timeForA >= 0 && timeForA <= totalDuration)
        {
            double linearStubExtensionLength;
            if (timeForA < morphDuration)
            {
                linearStubExtensionLength = (timeForA)/morphDuration;
            }
            else
            {
                if (timeForA > morphDuration + a.getFullLengthTime())
                {
                    linearStubExtensionLength = 1 - (timeForA - (morphDuration+a.getFullLengthTime()))/morphDuration;
                }
                else
                {
                    linearStubExtensionLength = 1;
                }
            }
            if (a.getMorphType().equals(MEDAnimation.MorphType.LINEAR))
            {
                return linearStubExtensionLength;
            }
            if (a.getMorphType().equals(MEDAnimation.MorphType.SINE))
            {
                return Math.sin(linearStubExtensionLength*Math.PI/2);
            }
            if (a.getMorphType().equals(MEDAnimation.MorphType.INVERSESINE))
            {
                return 1-Math.cos(linearStubExtensionLength*Math.PI/2);
            }
            if (a.getMorphType().equals(MEDAnimation.MorphType.COSINE))
            {
                return (1-Math.cos(linearStubExtensionLength*Math.PI))/2;
            }
        }
        return 0;
    }
}
