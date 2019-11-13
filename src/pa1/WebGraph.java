package pa1;

import api.Graph;
import api.TaggedVertex;

import java.util.*;

public class WebGraph implements Graph {

    private HashMap<Integer, ArrayList<TaggedVertex>> adjList = new HashMap<>();
    private HashMap<Integer, ArrayList<TaggedVertex>> incoming = new HashMap<>();
    private HashSet<Integer> vertices = new HashSet<>();

    public WebGraph(int size){
        for(int i=0; i<size; i++){
            adjList.put(i, new ArrayList<>());
            incoming.put(i, new ArrayList<>());
        }
    }

    public void addEdge(TaggedVertex start, TaggedVertex end){
        if(start.getTagValue()>=adjList.size() || end.getTagValue()>=adjList.size()){
            return;
        }
        if(!vertices.contains(start.getTagValue())) {
            ArrayList adj = adjList.get(start.getTagValue());
            ArrayList in = incoming.get(start.getTagValue());
            adj.add(start);
            in.add(start);
            adjList.replace(start.getTagValue(), adj);
            incoming.replace(start.getTagValue(), in);
            vertices.add(start.getTagValue());
        }
        if(!vertices.contains(end.getTagValue()) && start.getTagValue()!=end.getTagValue()) {
            ArrayList adj = adjList.get(end.getTagValue());
            ArrayList in = incoming.get(end.getTagValue());
            adj.add(end);
            in.add(end);
            adjList.replace(end.getTagValue(), adj);
            incoming.replace(end.getTagValue(), in);
            vertices.add(end.getTagValue());
        }
        adjList.get(start.getTagValue()).add(end);
        incoming.get(end.getTagValue()).add(start);
    }


    @Override
    public ArrayList vertexData() {
        ArrayList<Object> data = new ArrayList<>();
        for(int i=0; i<adjList.size(); i++){
            if(adjList.get(i).size()>0)
             data.add(adjList.get(i).get(0).getVertexData());
        }
        return data;
    }

    @Override
    public ArrayList<TaggedVertex> vertexDataWithIncomingCounts() {
        ArrayList<TaggedVertex> list = new ArrayList<>();
        for(int i=0; i<incoming.size(); i++){
            if(incoming.get(i).size()>0) {
                Object data = incoming.get(i).get(0).getVertexData();
                int value = incoming.get(i).size() - 1;
                TaggedVertex vertex = new TaggedVertex(data, value);
                list.add(vertex);
            }
        }
        return list;
    }

    @Override
    public List<Integer> getNeighbors(int index) {
        List<Integer> list = new ArrayList<>();
        if(adjList.get(index).size()==0)
            return list;
        ArrayList<TaggedVertex> vertices = adjList.get(index);
        for(int i=1; i<vertices.size(); i++){
            list.add(vertices.get(i).getTagValue());
        }
        return list;
    }

    @Override
    public List<Integer> getIncoming(int index) {
        List<Integer> list = new ArrayList<>();
        if(incoming.get(index).size()<=1)
            return list;
        ArrayList<TaggedVertex> vertices = incoming.get(index);
        for(int i=1; i<vertices.size(); i++){
            list.add(vertices.get(i).getTagValue());
        }
        return list;
    }
}
