package MED.Graph;

import java.util.HashSet;
import java.util.Iterator;

public class MEDEdge
{
    private final MEDVertex v1;
    private final MEDVertex v2;
    private final double minLength;
    private String color;
    private HashSet<MEDAnimation> animations;
    public MEDEdge(MEDVertex v1,MEDVertex v2,double minLength,String color)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.color = color;
        this.minLength = minLength;
        this.animations = new HashSet<>();

    }
    public MEDEdge(MEDVertex v1,MEDVertex v2,double minLength)
    {
        this(v1,v2,minLength,"#000000");
    }
    public String getColor()
    {
        return color;
    }
    public void addAnimation(MEDAnimation animation)
    {
        this.animations.add(animation);
    }
    public MEDVertex getV1()
    {
        return this.v1;
    }
    public MEDVertex getV2()
    {
        return this.v2;
    }
    public double getMinLength()
    {
        return this.minLength;
    }
    public Iterator<MEDAnimation> getAnimations()
    {
        return this.animations.iterator();
    }
    protected void clearAnimations()
    {
        this.animations = new HashSet<>();
    }
}
