package pa1;

import api.Graph;
import api.TaggedVertex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Implementation of a basic web crawler that creates a graph of some
 * portion of the world wide web.
 *
 * @author Isaac Sanga, Justin Worley
 */
public class Crawler
{
  private String seedUrl;
  private int maxDepth, maxPages;
  private JSoupAPI jSoupAPI;
  /**
   * Constructs a Crawler that will start with the given seed url, including
   * only up to maxPages pages at distance up to maxDepth from the seed url.
   * @param seedUrl
   * @param maxDepth
   * @param maxPages
   */
  public Crawler(String seedUrl, int maxDepth, int maxPages)
  {
    this.seedUrl = seedUrl;
    this.maxDepth = maxDepth;
    this.maxPages = maxPages;
    this.jSoupAPI = new JSoupAPI();
  }
  
  /**
   * Creates a web graph for the portion of the web obtained by a BFS of the 
   * web starting with the seed url for this object, subject to the restrictions
   * implied by maxDepth and maxPages.  
   * @return
   *   an instance of Graph representing this portion of the web
   */
  public Graph<String> crawl(){
    // TODO
    WebGraph webGraph = new WebGraph(maxPages);
    if(maxPages==0){
      return webGraph;
    }

    HashMap<String, Integer> visited = new HashMap<>();
    visited.put(seedUrl, 0);

    Queue<String> queue = new LinkedList();
    queue.add(seedUrl);
    queue.add(null); //Separates depth

    int count = 1;
    int layer = 0;

    while(!queue.isEmpty() && layer<=maxDepth) {
      String url = queue.poll();
      if (url == null) {
        layer++;
        queue.add(null);
        if(queue.peek()==null)
          break;
        else
          continue;
      } else{
        TaggedVertex parent = new TaggedVertex(url, visited.get(url));
        String[] links = jSoupAPI.getLinks(url);
        for (String v : links) {
          TaggedVertex vertex = new TaggedVertex(v, count);
          if (!visited.containsKey(v) && count != maxPages && layer < maxDepth) {
            visited.put(v, count);
            webGraph.addEdge(parent, vertex);
            queue.add(v);
            count++;
          } else if(visited.containsKey(v)) {
            TaggedVertex endVertex = new TaggedVertex(v, visited.get(v));
            webGraph.addEdge(parent, endVertex);
          }
        }
      }
    }

    return webGraph;
  }


}