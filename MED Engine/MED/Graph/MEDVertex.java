package MED.Graph;

public class MEDVertex
{
    private String id;
    private double x;
    private double y;
    private String color;

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
    public String getColor()
    {
        return this.color;
    }
}
