package pa1;

import api.Graph;
import api.TaggedVertex;
import api.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
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
  }
  
  /**
   * Creates a web graph for the portion of the web obtained by a BFS of the 
   * web starting with the seed url for this object, subject to the restrictions
   * implied by maxDepth and maxPages.  
   * @return
   *   an instance of Graph representing this portion of the web
   */
  public Graph<String> crawl() throws IOException {
    // TODO

    WebGraph webGraph = new WebGraph(maxPages);
    if(maxPages==0){
      return webGraph;
    }

    HashSet<String> visited = new HashSet();
    visited.add(seedUrl);

    Queue<String> queue = new LinkedList();
    queue.add(seedUrl);

    int count = 0;
    int layer = 0;
    int requestCount = 0;

    while(!queue.isEmpty() && layer<=maxDepth) {
      String url = queue.poll();
      if(requestCount<=50) {
        requestCount=0;
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      Document doc = Jsoup.connect(url).get();
      requestCount++;
      Elements elements = doc.select("a[href]");
      TaggedVertex parent = new TaggedVertex(url, count);
      count++;
      for (Element link : elements) {
        String v = link.attr("abs:href");
        if(!Util.ignoreLink(url, v) && !visited.contains(v)){
          visited.add(v);
          TaggedVertex vertex = new TaggedVertex(v, count);
          webGraph.addEdge(parent, vertex);
          queue.add(v);
          count++;
        }
        if(count==maxPages)
          return webGraph;
      }
      layer++;
      if(layer==maxDepth)
        return webGraph;
    }

    return webGraph;
  }



}