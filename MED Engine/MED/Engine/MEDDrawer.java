package MED.Engine;

import MED.Data.Coordinate;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;
import MED.Graph.MEDEdge;

//Required package, check http://www.java2s.com/Code/Java/2D-Graphics-GUI/AnimatedGifEncoder.htm
import java2s.AnimatedGifEncoder;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class MEDDrawer
{
    private final MEDGraph graph;
    private final MEDEngine engine;
    private final String pathToFile;
    private int edgeWidth;
    private int vertexRadius;
    private int margin;
    public enum Mode {GIF,MPEG};
    public MEDDrawer(MEDGraph graph, String pathToFile)
    {
        this.graph = graph;
        this.pathToFile = pathToFile;
        this.engine = new MEDEngine();
        this.edgeWidth = 2;
        this.vertexRadius = 3;
        this.margin = 25;
    }
    public void setEdgeWidth(int edgeWidth)
    {
        this.edgeWidth = edgeWidth;
    }
    public void setVertexRadius(int vertexRadius)
    {
        this.vertexRadius = vertexRadius;
    }
    public void setMargin(int margin)
    {
        this.margin = margin;
    }
    public boolean draw(double time, int fps, int width, int height)
    {
        return draw(time,fps,width,height,Mode.GIF);
    }
    public boolean draw(double time, int fps, int width, int height, Mode mode)
    {
        switch (mode)
        {
            case GIF:
            {
                return drawGif(time, fps, width, height);
            }
            case MPEG:
            {
                return drawMpeg(time,fps,width,height);
            }
            default:
            {
                return false;
            }
        }
    }
    private boolean drawMpeg(double time, int fps, int width, int height)
    {
        return false;
    }
    private boolean drawGif(double time, int fps, int width, int height)
    {
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        if (!e.start(pathToFile))
        {
            return false;
        }
        int frameTime = 1000/fps;
        e.setDelay(frameTime);
        e.setRepeat(0);
        long currentFrame = 0;
        do
        {
            BufferedImage frameImage = new BufferedImage(width+2*margin,height+2*margin,BufferedImage.TYPE_INT_RGB);
            drawFrame(frameImage,width+2*margin,height+2*margin,currentFrame);
            e.addFrame(frameImage);
            currentFrame += frameTime;
        }
        while(currentFrame<time);
        e.finish();
        return true;
    }
    private void drawFrame(BufferedImage frameImage, int width, int height, long currentFrame)
    {
        Graphics2D frameDrawing = frameImage.createGraphics();
        frameDrawing.setBackground(Color.WHITE);
        frameDrawing.fillRect(0, 0, width, height);
        BasicStroke bs = new BasicStroke(edgeWidth);
        frameDrawing.setStroke(bs);
        Iterator<MEDEdge> edge_it = graph.getEdges();
        while (edge_it.hasNext())
        {
            MEDEdge e = edge_it.next();
            drawEdge(frameDrawing, e, currentFrame);
        }
        Iterator<MEDVertex> vertex_it = graph.getVertices();
        while (vertex_it.hasNext())
        {
            MEDVertex v = vertex_it.next();
            drawVertex(frameDrawing, v);
        }
    }
    private void drawEdge(Graphics2D frameDrawing, MEDEdge e, long currentFrame)
    {
        frameDrawing.setColor(Color.BLACK);
        Point2D.Double v1 = new Point2D.Double(e.getV1().getX()+margin,e.getV1().getY()+margin);
        Coordinate stubPosition1 = engine.computeStubPosition1(e,currentFrame);
        Point2D.Double stubEnd1 = new Point2D.Double(stubPosition1.getX()+margin,stubPosition1.getY()+margin);
        Line2D.Double stub1 = new Line2D.Double(v1,stubEnd1);
        frameDrawing.draw(stub1);
        Point2D.Double v2 = new Point2D.Double(e.getV2().getX()+margin,e.getV2().getY()+margin);
        Coordinate stubPosition2 = engine.computeStubPosition2(e,currentFrame);
        Point2D.Double stubEnd2 = new Point2D.Double(stubPosition2.getX()+margin,stubPosition2.getY()+margin);
        Line2D.Double stub2 = new Line2D.Double(v2,stubEnd2);
        frameDrawing.draw(stub2);
    }
    private void drawVertex(Graphics2D frameDrawing, MEDVertex v)
    {
        Point2D.Double pos = new Point2D.Double(v.getX()+margin,v.getY()+margin);
        frameDrawing.setColor(Color.decode(v.getColor()));
        frameDrawing.setBackground(Color.decode(v.getColor()));
        Ellipse2D.Double vertex = new Ellipse2D.Double(pos.getX()-vertexRadius,pos.getY()-vertexRadius,2*vertexRadius,2*vertexRadius);
        frameDrawing.draw(vertex);
        frameDrawing.fill(vertex);
    }
}
