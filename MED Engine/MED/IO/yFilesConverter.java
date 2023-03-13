package MED.IO;

import MED.Graph.MEDEdge;
import MED.Graph.MEDGraph;
import MED.Graph.MEDVertex;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphComponent;

import java.util.HashMap;

public class yFilesConverter
{
    private final double minLength;
    public yFilesConverter(double minLength)
    {
        if (minLength > 1 || minLength < 0)
        {
            this.minLength = 0.5;
        }
        else
        {
            this.minLength = minLength;
        }
    }
    public MEDGraph convertToMED(IGraph yGraph, GraphComponent graphComponent)
    {
        MEDGraph med = new MEDGraph();
        int vIndex = 0;
        HashMap<INode,MEDVertex> yVertexToMEDVertex = new HashMap<>();
        for (INode yVertex : yGraph.getNodes())
        {
            String id;
            if (yVertex.getLabels().size() > 0)
            {
                id = yVertex.getLabels().first().getText();
            }
            else
                {
                id = "V" + vIndex++;
            }
            double x = yVertex.getLayout().toPoint2D().getX();
            double y = yVertex.getLayout().toPoint2D().getY();
            MEDVertex vertex = new MEDVertex(id,x,y);
            System.out.println(id + ":\t(" + x + "," + y + ")");
            yVertexToMEDVertex.put(yVertex,vertex);
            med.addVertex(vertex);
        }
        for (IEdge yEdge : yGraph.getEdges())
        {
            MEDVertex source = yVertexToMEDVertex.get(yEdge.getSourceNode());
            MEDVertex target = yVertexToMEDVertex.get(yEdge.getTargetNode());
            med.addEdge(new MEDEdge(source,target,minLength));
        }
        return med;
    }
}
