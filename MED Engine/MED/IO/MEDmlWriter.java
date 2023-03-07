package MED.IO;

import MED.Graph.MEDGraph;
import MED.Graph.MEDAnimation;
import MED.Graph.MEDEdge;
import MED.Graph.MEDVertex;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;


public class MEDmlWriter
{
    private final String file;
    public MEDmlWriter(String file)
    {
        this.file = file;
    }
    public void write(MEDGraph g)
    {
        Document document;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.newDocument();

            Element root = document.createElement("graphml");
            root.setAttribute("xmlns","http://graphml.graphdrawing.org/xmlns");
            root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xsi:schemaLocation","http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");

            // create data elements and place them under root
            Element graph = document.createElement("graph");
            graph.setAttribute("id","G");
            graph.setAttribute("edgedefault","undirected");
            //e.appendChild(document.createTextNode(role1));
            root.appendChild(graph);

            Iterator<MEDVertex> v_it = g.getVertices();
            while (v_it.hasNext())
            {
                MEDVertex v = v_it.next();
                Element vertex = document.createElement("node");
                vertex.setAttribute("id","V"+v.getID());
                vertex.setAttribute("x",Double.toString(v.getX()));
                vertex.setAttribute("y",Double.toString(v.getY()));
                graph.appendChild(vertex);
            }
            Iterator<MEDEdge> e_it = g.getEdges();
            int edgeID = 0;
            while (e_it.hasNext())
            {
                MEDEdge e = e_it.next();
                Element edge = document.createElement("edge");
                edge.setAttribute("id","E"+edgeID++);
                edge.setAttribute("source","V"+e.getV1().getID());
                edge.setAttribute("target","V"+e.getV2().getID());
                edge.setAttribute("minLength",Double.toString(e.getMinLength()));
                Iterator<MEDAnimation> a_it = e.getAnimations();
                while (a_it.hasNext())
                {
                    MEDAnimation a = a_it.next();
                    Element animation = document.createElement("animation");
                    animation.setAttribute("startTime",Double.toString(a.getStartTime()));
                    animation.setAttribute("speed",Double.toString(a.getSpeed()));
                    animation.setAttribute("fullLengthTime",Double.toString(a.getFullLengthTime()));
                    animation.setAttribute("period",Double.toString(a.getPeriod()));
                    switch(a.getMorphType())
                    {
                        case COMPLETE:
                        {
                            animation.setAttribute("morphType","COMPLETE");
                            break;
                        }
                        case PED:
                        {
                            animation.setAttribute("morphType","PED");
                            break;
                        }
                        case LINEAR:
                        {
                            animation.setAttribute("morphType","LINEAR");
                            break;
                        }
                        case SINE:
                        {
                            animation.setAttribute("morphType","SINE");
                            break;
                        }
                        case INVERSESINE:
                        {
                            animation.setAttribute("morphType","INVERSESINE");
                            break;
                        }
                        case COSINE:
                        {
                            animation.setAttribute("morphType","COSINE");
                            break;
                        }
                        default:
                        {
                            animation.setAttribute("morphType","N/A");
                            break;
                        }
                    }
                    edge.appendChild(animation);
                }
                graph.appendChild(edge);
            }
            document.appendChild(root);

            try
            {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(document), new StreamResult(new FileOutputStream(file)));

            }
            catch (TransformerException | IOException ex)
            {
                System.err.println(ex.getMessage());
            }
        }
        catch (ParserConfigurationException pce)
        {
            System.err.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }
}
