package MED.Engine;

import MED.Data.Coordinate;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;
import MED.Graph.MEDEdge;

//Required package, check http://www.java2s.com/Code/Java/2D-Graphics-GUI/AnimatedGifEncoder.htm
import java2s.AnimatedGifEncoder;
//Required package, import jcodec and jcodec.javase via maven
import org.jcodec.common.io.ByteBufferSeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.api.awt.AWTSequenceEncoder;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

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
    public boolean draw(double time, int fps, int width, int height, double xOffset, double yOffset)
    {
        return draw(time,fps,width,height,xOffset,yOffset,Mode.GIF);
    }
    public boolean draw(double time, int fps, int width, int height, double xOffset, double yOffset, Mode mode)
    {
        switch (mode)
        {
            case GIF:
            {
                return drawGif(time, fps, width, height, xOffset, yOffset);
            }
            case MPEG:
            {
                return drawMpeg(time,fps,width,height, xOffset, yOffset);
            }
            default:
            {
                return false;
            }
        }
    }
    private boolean drawMpeg(double time, int fps, int width, int height, double xOffset, double yOffset)
    {
        SeekableByteChannel out = null;
        try
        {
            out = NIOUtils.writableFileChannel(pathToFile);
            // for Android use: AndroidSequenceEncoder
            AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(fps, 1));
            double frameTime = 1000.0/fps;
            double currentFrame = 0;
            do
            {
                BufferedImage frameImage = new BufferedImage(width+width%2+2*margin,height+height%2+2*margin,BufferedImage.TYPE_3BYTE_BGR);
                drawFrame(frameImage,width+width%2+2*margin,height+height%2+2*margin, xOffset, yOffset, (long)currentFrame);
                encoder.encodeImage(frameImage);
                currentFrame += frameTime;
            }
            while(currentFrame<time);
            encoder.finish();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            NIOUtils.closeQuietly(out);
            return false;
        }
        NIOUtils.closeQuietly(out);
        return true;
    }
    private boolean drawGif(double time, int fps, int width, int height, double xOffset, double yOffset)
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
            drawFrame(frameImage,width+2*margin,height+2*margin, xOffset, yOffset, currentFrame);
            e.addFrame(frameImage);
            currentFrame += frameTime;
        }
        while(currentFrame<time);
        e.finish();
        return true;
    }
    private void drawFrame(BufferedImage frameImage, int width, int height, double xOffset, double yOffset, long currentFrame)
    {
        Graphics2D frameDrawing = frameImage.createGraphics();
        frameDrawing.setBackground(Color.WHITE);
        frameDrawing.fillRect(0, 0, width, height);
        frameDrawing.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        BasicStroke bs = new BasicStroke(edgeWidth);
        frameDrawing.setStroke(bs);
        Iterator<MEDEdge> edge_it = graph.getEdges();
        while (edge_it.hasNext())
        {
            MEDEdge e = edge_it.next();
            drawEdge(frameDrawing, e, xOffset, yOffset, currentFrame);
        }
        Iterator<MEDVertex> vertex_it = graph.getVertices();
        while (vertex_it.hasNext())
        {
            MEDVertex v = vertex_it.next();
            drawVertex(frameDrawing, v, xOffset, yOffset);
        }
    }
    private void drawEdge(Graphics2D frameDrawing, MEDEdge e, double xOffset, double yOffset, long currentFrame)
    {
        frameDrawing.setColor(Color.BLACK);
        Point2D.Double v1 = new Point2D.Double(e.getV1().getX()+margin+xOffset,e.getV1().getY()+margin+yOffset);
        Coordinate stubPosition1 = engine.computeStubPosition1(e,currentFrame);
        Point2D.Double stubEnd1 = new Point2D.Double(stubPosition1.getX()+margin+xOffset,stubPosition1.getY()+margin+yOffset);
        Line2D.Double stub1 = new Line2D.Double(v1,stubEnd1);
        frameDrawing.draw(stub1);
        Point2D.Double v2 = new Point2D.Double(e.getV2().getX()+margin+xOffset,e.getV2().getY()+margin+yOffset);
        Coordinate stubPosition2 = engine.computeStubPosition2(e,currentFrame);
        Point2D.Double stubEnd2 = new Point2D.Double(stubPosition2.getX()+margin+xOffset,stubPosition2.getY()+margin+yOffset);
        Line2D.Double stub2 = new Line2D.Double(v2,stubEnd2);
        frameDrawing.draw(stub2);
    }
    private void drawVertex(Graphics2D frameDrawing, MEDVertex v, double xOffset, double yOffset)
    {
        Point2D.Double pos = new Point2D.Double(v.getX()+margin+xOffset,v.getY()+margin+yOffset);
        frameDrawing.setColor(Color.decode(v.getColor()));
        frameDrawing.setBackground(Color.decode(v.getColor()));
        Ellipse2D.Double vertex = new Ellipse2D.Double(pos.getX()-vertexRadius,pos.getY()-vertexRadius,2*vertexRadius,2*vertexRadius);
        frameDrawing.draw(vertex);
        frameDrawing.fill(vertex);
    }
}
