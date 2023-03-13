package MED.Data;

public class Interval
{
    private final double start;
    private final double end;
    public Interval(double start, double end)
    {
        this.start = start;
        this.end = end;
    }
    public double getStart()
    {
        return start;
    }
    public double getEnd()
    {
        return end;
    }
}