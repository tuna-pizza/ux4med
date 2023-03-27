package MED.Algorithm;

import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;

import java.util.Iterator;

public class ConstantScheduler extends Scheduler
{
    public ConstantScheduler(MEDAnimation.MorphType morphType)
    {
        super(100,0,0,morphType,false,false);
    }
    @Override
    public void schedule(MEDGraph g)
    {
        Iterator<MEDEdge> e_it = g.getEdges();
        MEDAnimation a = new MEDAnimation(0.0,speed,fullLengthTime,0.0,morphType);
        while (e_it.hasNext())
        {
            e_it.next().addAnimation(a);
        }
    }
}
