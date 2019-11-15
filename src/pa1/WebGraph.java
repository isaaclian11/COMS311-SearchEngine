package pa1;

import api.Graph;
import api.TaggedVertex;

import java.util.*;

public class WebGraph implements Graph {

    private HashMap<Integer, List<Integer>> adjList;
    private HashMap<Integer, List<Integer>> incoming;
    private HashMap<Integer, TaggedVertex> vertexData;

    public WebGraph(int size){
        adjList = new HashMap<>();
        incoming = new HashMap<>();
        vertexData = new HashMap<>();
    }

    public void addEdge(TaggedVertex start, TaggedVertex end){
        if(!vertexData.containsKey(start.getTagValue())) {
            List<Integer> adj = adjList.get(start.getTagValue());
            List<Integer> in = incoming.get(start.getTagValue());
            if(adj==null) {
                adj = new ArrayList();
                adjList.put(start.getTagValue(), adj);
            }
            if(in==null) {
                in = new ArrayList();
                incoming.put(start.getTagValue(), in);
            }
            adj.add(start.getTagValue());
            in.add(start.getTagValue());
            vertexData.put(start.getTagValue(), start);
            adjList.replace(start.getTagValue(), adj);
            incoming.replace(start.getTagValue(), in);
        }
        if(!vertexData.containsKey(end.getTagValue()) && start.getTagValue()!=end.getTagValue()) {
            List adj = adjList.get(end.getTagValue());
            List in = incoming.get(end.getTagValue());
            if(adj==null) {
                adj = new ArrayList();
                adjList.put(end.getTagValue(), adj);
            }
            if(in==null) {
                in = new ArrayList();
                incoming.put(end.getTagValue(), in);
            }
            adj.add(end);
            in.add(end);
            vertexData.put(end.getTagValue(), end);
            adjList.replace(end.getTagValue(), adj);
            incoming.replace(end.getTagValue(), in);
        }
        adjList.get(start.getTagValue()).add(end.getTagValue());
        incoming.get(end.getTagValue()).add(start.getTagValue());
    }


    @Override
    public ArrayList vertexData() {
        ArrayList<Object> data = new ArrayList<>();
        for(int i=0; i<vertexData.size(); i++){
            if(adjList.get(i).size()>0)
             data.add(vertexData.get(i).getVertexData());
        }
        return data;
    }

    @Override
    public ArrayList<TaggedVertex> vertexDataWithIncomingCounts() {
        ArrayList<TaggedVertex> list = new ArrayList<>();
        for(int i=0; i<incoming.size(); i++){
            if(incoming.get(i).size()>0) {
                Object data = vertexData.get(i).getVertexData();
                int value = incoming.get(i).size() - 1;
                if(i==0)
                    value = incoming.get(i).size();
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
        List<Integer> vertices = adjList.get(index);
        vertices.remove(0);
        return vertices;
    }

    @Override
    public List<Integer> getIncoming(int index) {
        List<Integer> list = new ArrayList<>();
        if(incoming.get(index).size()<=1)
            return list;
        List<Integer> vertices = incoming.get(index);
        vertices.remove(0);
        return vertices;
    }
}
