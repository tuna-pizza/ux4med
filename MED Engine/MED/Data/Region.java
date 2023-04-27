package MED.Data;

import java.awt.*;
import java.util.List;

public class Region
{
	final List<Integer> x;
	final List<Integer> y;
	final String color;
	public Region(List<Integer> x, List<Integer> y, String color)
	{
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public String getColor()
	{
		return color;
	}

	public Polygon getPolygon()
	{
		if (x.size() == y.size())
		{
			return new Polygon(x.stream().mapToInt(i->i).toArray(),y.stream().mapToInt(i->i).toArray(),x.size());
		}
		else
		{
			return null;
		}
	}
}
