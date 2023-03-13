package MED.Algorithm;

import MED.Data.Interval;

import java.util.Comparator;

public class IntervalStartComparator implements Comparator<Interval>
{
    @Override
    public int compare(Interval a, Interval b)
    {
        return (int)(a.getStart() - b.getStart());
    }
}