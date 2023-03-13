package MED.Algorithm;

import MED.Engine.MEDEngine;
import MED.Graph.MEDAnimation;
import MED.Graph.MEDGraph;

public abstract class Scheduler
{
    protected final double speed;
    protected final double fullLengthTime;
    protected final double crossingDelay;
    protected final MEDAnimation.MorphType morphType;
    public Scheduler(double speed, double fullLengthTime, double crossingDelay, MEDAnimation.MorphType morphType)
    {
        this.speed = speed;
        this.fullLengthTime = fullLengthTime;
        this.crossingDelay = crossingDelay;
        this.morphType = morphType;
    }
    public abstract void schedule(MEDGraph g);
}
