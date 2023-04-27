package MED.Graph;

public class MEDAnimation
{
    public enum MorphType{COMPLETE, PED, LINEAR, SINE, INVERSESINE, COSINE, EASING, NONE}
    private double start_time;
    private double speed;
    private double fullLengthTime;
    private double period;
    private MorphType morphType;

    public MEDAnimation(double start_time,double speed,double fullLengthTime, double period,MorphType morph_type)
    {
        this.start_time = start_time;
        this.speed = speed;
        this.fullLengthTime = fullLengthTime;
        this.period = period;
        this.morphType = morph_type;
    }
    public MEDAnimation(double start_time,double speed,double fullLengthTime,double period,String morph_type)
    {

        this(start_time,speed,fullLengthTime,period,MorphType.NONE);
        switch (morph_type)
        {
            case "COMPLETE":
            {
                this.morphType = MorphType.COMPLETE;
                break;
            }
            case "PED":
            {
                this.morphType = MorphType.PED;
                break;
            }
            case "LINEAR":
            {
                this.morphType = MorphType.LINEAR;
                break;
            }
            case "SINE":
            {
                this.morphType = MorphType.SINE;
                break;
            }
            case "INVERSESINE":
            {
                this.morphType = MorphType.INVERSESINE;
                break;
            }
            case "COSINE":
            {
                this.morphType = MorphType.COSINE;
                break;
            }
            default:
            {
                this.morphType = MorphType.NONE;
            }
        }
    }
    public double getStartTime()
    {
        return this.start_time;
    }
    public double getSpeed()
    {
        return this.speed;
    }
    public double getFullLengthTime()
    {
        return this.fullLengthTime;
    }
    public double getPeriod()
    {
        return this.period;
    }
    public MorphType getMorphType()
    {
        return this.morphType;
    }
}
