package MED.Engine;

import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;
import MED.Graph.MEDEdge;
import java2s.AnimatedGifEncoder;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class MEDDrawer
{
    private MEDGraph graph;
    private MEDEngine engine;
    private String pathToFile;
    public MEDDrawer(MEDGraph graph, String pathToFile)
    {
        this.graph = graph;
        this.pathToFile = pathToFile;
        this.engine = new MEDEngine();
    }
    public boolean draw(double time, int fps, int width, int height)
    {
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        if (!e.start(pathToFile))
        {
            return true;
        }
        int frameTime = 1000/fps;
        e.setDelay(frameTime);
        e.setRepeat(0);
        long currentFrame = 0;
        do
        {
            BufferedImage frameImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            drawFrame(frameImage,width,height,currentFrame);
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
        BasicStroke bs = new BasicStroke(2);
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
        Point2D.Double v1 = new Point2D.Double(e.getV1().getX(),e.getV1().getY());
        Coordinate stubPosition1 = engine.computeStubPosition1(e,currentFrame);
        Point2D.Double stubEnd1 = new Point2D.Double(stubPosition1.getX(),stubPosition1.getY());
        Line2D.Double stub1 = new Line2D.Double(v1,stubEnd1);
        frameDrawing.draw(stub1);
        Point2D.Double v2 = new Point2D.Double(e.getV2().getX(),e.getV2().getY());
        Coordinate stubPosition2 = engine.computeStubPosition2(e,currentFrame);
        Point2D.Double stubEnd2 = new Point2D.Double(stubPosition2.getX(),stubPosition2.getY());
        Line2D.Double stub2 = new Line2D.Double(v2,stubEnd2);
        frameDrawing.draw(stub2);
    }
    private void drawVertex(Graphics2D frameDrawing, MEDVertex v)
    {
        Point2D.Double pos = new Point2D.Double(v.getX(),v.getY());
        frameDrawing.setColor(Color.decode(v.getColor()));
        frameDrawing.setBackground(Color.decode(v.getColor()));
        Ellipse2D.Double vertex = new Ellipse2D.Double(pos.getX()-3,pos.getY()-3,6,6);
        frameDrawing.draw(vertex);
    }
}
