package Test;

import MED.Algorithm.*;
import MED.Data.Region;
import MED.Engine.MEDDrawer;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;
import MED.Graph.MEDEdge;
import MED.Graph.MEDAnimation;
import MED.IO.BoardgameReader;
import MED.IO.JSONReader;
import MED.IO.MEDmlReader;
import MED.IO.CSVReader;
import MED.IO.MEDmlWriter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class main
{
    public static void main (String[] args)
    {
        boolean draw = true;
        if (!draw)
        {
            double speed = 0.1;
            double fullLengthTime = 100;
            double crossingDelay = 50;
            MEDAnimation.MorphType morphType = MEDAnimation.MorphType.LINEAR;
            double defaultEdgeLength = 0.5;
            double scaleRatio = 2;
            String graphFile = "/home/foersth/Dokumente/MED/Examples/Alessandra-Examples/kpop1.json";
            String coordinateFile = "/home/foersth/Dokumente/MED/Examples/Alessandra-Examples/kpop2.json";
            String outputFile = "/home/foersth/Dokumente/MED/Examples/Alessandra-Examples/kpop.medml";
            readJSONAndWriteToMEDml(graphFile, coordinateFile, outputFile, speed, fullLengthTime, crossingDelay, morphType, defaultEdgeLength, scaleRatio);
        }
        else
        {
            String inputFile = "/home/foersth/Dokumente/MED/Examples/Alessandra-Examples/Task1/kpop.medml";
            String outputFile = "/home/foersth/Dokumente/MED/Examples/Alessandra-Examples/Task1/kpop.mp4";
            double defaultEdgeLength = 0.5;
            readMEDmlAndDraw(inputFile,outputFile,defaultEdgeLength);
        }
        /*JSONReader r = new JSONReader(,"/home/foersth/Dokumente/MED/Examples/Alessandra-Examples/marvel2.json",0.5);
        MEDGraph g = r.read();
        g.normalize(2);
        Iterator<MEDVertex> v_it = g.getVertices();
        while (v_it.hasNext())
        {
            MEDVertex v = v_it.next();
            System.out.println(v.getID() + "\t" + v.getX() + "\t" + v.getY());
        }
       // Scheduler s = new GreedyEdgeScheduler(speed,fullLengthTime,crossingDelay,morphType,true,false);
        //s.schedule(g);
        Scheduler s = new ConstantScheduler(MEDAnimation.MorphType.PED);
        s.schedule(g);*/

        /*
        BoardgameReader r = new BoardgameReader("/home/foersth/Dokumente/MED/Examples/GDContest/boardgames_100.json",0.5);
        MEDGraph g = r.read();
        MEDmlWriter w = new MEDmlWriter("/home/foersth/Dokumente/MED/Examples/GDContest/boardgames100.graphml");
        w.write(g);
        /*CSVReader r = new CSVReader("/home/foersth/Dokumente/MED/Examples/GDContest/2020_K-pop_nodes.csv","/home/foersth/Dokumente/MED/Examples/GDContest/2020_K-pop_edges.csv",0.5);
        MEDGraph g = r.read();
        MEDmlWriter w = new MEDmlWriter("/home/foersth/Dokumente/MED/Examples/GDContest/2020_K-pop.graphml");
        w.write(g);*/
        //MEDmlReader r = new MEDmlReader("/home/foersth/Dokumente/MED/Examples/perugia.graphml", MEDmlReader.InputType.yEdNew, 0.25);
        //MEDGraph g = r.read();
//        Scheduler s = new GreedyEdgeScheduler(speed,fullLengthTime,crossingDelay,morphType,true,true);
        //s.schedule(g);
       // System.out.println("Finished scheduling!");
        //System.out.println("Total Time: " + (g.getLastEnd()-g.getFirstStart()));
        //MEDDrawer d = new MEDDrawer(g,"/home/foersth/Dokumente/MED/Examples/Variants/misue-planar.mp4");
        //d.setEdgeWidth(2);
        //d.setVertexRadius(5);
//        //MEDmlWriter w = new MEDmlWriter("/home/foersth/Dokumente/MED/Examples/GDContest/marvel.medml");
        //w.write(g);*/
    }
    static void readMEDmlAndDraw(String inputFile, String outputFile,double defaultEdgeLength)
    {
        MEDmlReader r = new MEDmlReader(inputFile, MEDmlReader.InputType.MEDml,defaultEdgeLength);
        MEDGraph g = r.read();
        MEDDrawer d = new MEDDrawer(g,outputFile);
        if (r.getRegions() != null)
        {
            for (Region reg : r.getRegions())
            {
                d.addRegion(reg);
            }
        }
        d.setEdgeWidth(2);
        d.setVertexRadius(7);
        d.draw(g.getLastEnd()-g.getFirstStart(),30,(int)(g.getMaxX()-g.getMinX()),(int)(g.getMaxY()-g.getMinY()),-g.getMinX(),-g.getMinY(), g.getFirstStart(),MEDDrawer.Mode.MPEG);
    }
    static void readJSONAndWriteToMEDml(String graphFile, String coordinateFile, String outputFile, double speed, double fullLengthTime, double crossingDelay, MEDAnimation.MorphType morphType, double defaultEdgeLength, double scaleRatio)
    {
        JSONReader r = new JSONReader(graphFile,coordinateFile,defaultEdgeLength);
        MEDGraph g = r.read();
        g.normalize(scaleRatio);
        Iterator<MEDVertex> v_it = g.getVertices();
        while (v_it.hasNext())
        {
            MEDVertex v = v_it.next();
            System.out.println(v.getID() + "\t" + v.getX() + "\t" + v.getY());
        }
        Scheduler s = new GreedyEdgeScheduler(speed,fullLengthTime,crossingDelay,morphType,true,false);
        s.schedule(g);
        MEDmlWriter w = new MEDmlWriter(outputFile);
        w.write(g);
    }
    static MEDGraph example2 (String style, double speed)
    {
        MEDGraph graph = new MEDGraph();
        MEDVertex v1 = new MEDVertex("1",200,25);
        MEDVertex v2 = new MEDVertex("2",323.7,76.2);
        MEDVertex v3 = new MEDVertex("3",375,200);
        MEDVertex v4 = new MEDVertex("4",323.7,323.7);
        MEDVertex v5 = new MEDVertex("5",200,375);
        MEDVertex v6 = new MEDVertex("6",76.2,323.7);
        MEDVertex v7 = new MEDVertex("7",25,200);
        MEDVertex v8 = new MEDVertex("8",76.2,76.2);
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);
        graph.addVertex(v5);
        graph.addVertex(v6);
        graph.addVertex(v7);
        graph.addVertex(v8);
        MEDEdge e1 = new MEDEdge(v1,v2,0.25);
        MEDEdge e2 = new MEDEdge(v1,v3,0.25);
        MEDEdge e3 = new MEDEdge(v1,v4,0.25);
        MEDEdge e4 = new MEDEdge(v1,v5,0.25);
        MEDEdge e5 = new MEDEdge(v1,v6,0.25);
        MEDEdge e6 = new MEDEdge(v1,v7,0.25);
        MEDEdge e7 = new MEDEdge(v1,v8,0.25);


        MEDEdge e8 = new MEDEdge(v2,v3,0.25);
        MEDEdge e9 = new MEDEdge(v2,v4,0.25);
        MEDEdge e10 = new MEDEdge(v2,v5,0.25);
        MEDEdge e11 = new MEDEdge(v2,v6,0.25);
        MEDEdge e12 = new MEDEdge(v2,v7,0.25);
        MEDEdge e13 = new MEDEdge(v2,v8,0.25);

        MEDEdge e14 = new MEDEdge(v3,v4,0.25);
        MEDEdge e15 = new MEDEdge(v3,v5,0.25);
        MEDEdge e16 = new MEDEdge(v3,v6,0.25);
        MEDEdge e17 = new MEDEdge(v3,v7,0.25);
        MEDEdge e18 = new MEDEdge(v3,v8,0.25);

        MEDEdge e19 = new MEDEdge(v4,v5,0.25);
        MEDEdge e20 = new MEDEdge(v4,v6,0.25);
        MEDEdge e21 = new MEDEdge(v4,v7,0.25);
        MEDEdge e22 = new MEDEdge(v4,v8,0.25);

        MEDEdge e23 = new MEDEdge(v5,v6,0.25);
        MEDEdge e24 = new MEDEdge(v5,v7,0.25);
        MEDEdge e25 = new MEDEdge(v5,v8,0.25);

        MEDEdge e26 = new MEDEdge(v6,v7,0.25);
        MEDEdge e27 = new MEDEdge(v6,v8,0.25);

        MEDEdge e28 = new MEDEdge(v7,v8,0.25);
        /*MEDAnimation a1 = new MEDAnimation(0,0.05*speed,100,20000/speed,style);
        MEDAnimation a2 = new MEDAnimation(2000/speed,0.05*speed,100,20000/speed,style);
        MEDAnimation a3 = new MEDAnimation(4000/speed,0.05*speed,100,20000/speed,style);
        MEDAnimation a4 = new MEDAnimation(6000/speed,0.05*speed,100,20000/speed,style);
        MEDAnimation a5 = new MEDAnimation(8000/speed,0.05*speed,100,20000/speed,style);
        MEDAnimation a6 = new MEDAnimation(10000/speed,0.05*speed,100,20000/speed,style);
        MEDAnimation a7 = new MEDAnimation(12000/speed,0.05*speed,100,20000/speed,style);
        e1.addAnimation(a1);
        e2.addAnimation(a1);
        e3.addAnimation(a1);
        e4.addAnimation(a1);
        e5.addAnimation(a1);
        e6.addAnimation(a1);
        e7.addAnimation(a1);
        e8.addAnimation(a5);
        e9.addAnimation(a5);
        e10.addAnimation(a5);
        e11.addAnimation(a5);
        e12.addAnimation(a5);
        e13.addAnimation(a5);
        e14.addAnimation(a3);
        e15.addAnimation(a3);
        e16.addAnimation(a3);
        e17.addAnimation(a3);
        e18.addAnimation(a3);
        e19.addAnimation(a7);
        e20.addAnimation(a7);
        e21.addAnimation(a7);
        e22.addAnimation(a7);
        e23.addAnimation(a2);
        e24.addAnimation(a2);
        e25.addAnimation(a2);
        e26.addAnimation(a6);
        e27.addAnimation(a6);
        e28.addAnimation(a4);*/
        graph.addEdge(e1);
        graph.addEdge(e2);
        graph.addEdge(e3);
        graph.addEdge(e4);
        graph.addEdge(e5);
        graph.addEdge(e6);
        graph.addEdge(e7);
        graph.addEdge(e8);
        graph.addEdge(e9);
        graph.addEdge(e10);
        graph.addEdge(e11);
        graph.addEdge(e12);
        graph.addEdge(e13);
        graph.addEdge(e14);
        graph.addEdge(e15);
        graph.addEdge(e16);
        graph.addEdge(e17);
        graph.addEdge(e18);
        graph.addEdge(e19);
        graph.addEdge(e20);
        graph.addEdge(e21);
        graph.addEdge(e22);
        graph.addEdge(e23);
        graph.addEdge(e24);
        graph.addEdge(e25);
        graph.addEdge(e26);
        graph.addEdge(e27);
        graph.addEdge(e28);
        return graph;
    }
}
