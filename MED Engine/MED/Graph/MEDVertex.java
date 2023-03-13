package MED.Graph;

public class MEDVertex
{
    private final String id;
    private double x;
    private double y;
    private final String color;

    public MEDVertex(String id,double x,double y)
    {
        this(id,x,y,"#000000");
    }
    public MEDVertex(String id,double x,double y,String color)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = color;
    }
    public String getID()
    {
        return this.id;
    }
    public double getX()
    {
        return this.x;
    }
    public double getY()
    {
        return this.y;
    }
    public void scale(double scaleRatio)
    {
        this.x = x*scaleRatio;
        this.y = y*scaleRatio;
    }
    public void shift(double xShift, double yShift)
    {
        this.x = x + xShift;
        this.y = y + yShift;
    }
    public String getColor()
    {
        return this.color;
    }
}
