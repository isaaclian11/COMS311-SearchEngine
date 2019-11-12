package pa1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import api.TaggedVertex;
import api.Util;
import org.jsoup.Jsoup;

/**
 * Implementation of an inverted index for a web graph.
 * 
 * @author Isaac Sanga, Justin Worley
 */
public class Index
{

  List<TaggedVertex<String>> urls;
  HashMap<String, HashMap<TaggedVertex, Integer>> list = new HashMap<>();

  /**
   * Constructs an index from the given list of urls.  The
   * tag value for each url is the indegree of the corresponding
   * node in the graph to be indexed.
   * @param urls
   *   information about graph to be indexed
   */
  public Index(List<TaggedVertex<String>> urls)
  {
    this.urls = urls;
  }
  
  /**
   * Creates the index.
   */
  public void makeIndex()
  {
    // TODO
    for(TaggedVertex vertex: urls){
      try {
        String url = (String) vertex.getVertexData();
        String body = Jsoup.connect(url).get().body().text();
        Scanner scanner = new Scanner(body);
        HashMap<String, Integer> wordCount = new HashMap<>();
        while(scanner.hasNext()){
          String current = scanner.next();
          if(wordCount.containsKey(current)){
            int count = wordCount.get(current);
            wordCount.replace(current, count++);
          }
          else{
            wordCount.put(current, 1);
          }
          if(!Util.isStopWord(current)){
            if(list.containsKey(current)){
              if(!list.get(current).containsKey(vertex))
                list.get(current).put(vertex, 1);
              else
                list.get(current).replace(vertex, wordCount.get(current));
            }
            else {
              HashMap<TaggedVertex, Integer> map = new HashMap<>();
              map.put(vertex, 1);
              list.put(current, map);
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Searches the index for pages containing keyword w.  Returns a list
   * of urls ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of the keyword multiplied by the indegree of its url in
   * the associated graph.  No pages with rank zero are included.
   * @param w
   *   keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> search(String w)
  {
    // TODO
    HashMap<TaggedVertex, Integer> unrankedMap = list.get(w);
    List<Ranked> unrankedList = new ArrayList<>();
    for(TaggedVertex vertex: unrankedMap.keySet()){
      unrankedList.add(new Ranked(vertex.getTagValue(), unrankedMap.get(vertex), (String) vertex.getVertexData()));
    }
    mergeSort(unrankedList, 0, unrankedList.size()-1);
    List<TaggedVertex<String>> output = new ArrayList<>();
    for(int i=0; i<unrankedList.size(); i++){
      output.add(new TaggedVertex<>(unrankedList.get(i).page, i));
    }
    return output;
  }


  /**
   * Searches the index for pages containing both of the keywords w1
   * and w2.  Returns a list of qualifying
   * urls ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of w1 plus number of occurrences of w2, all multiplied by the 
   * indegree of its url in the associated graph.
   * No pages with rank zero are included.
   * @param w1
   *   first keyword to search for
   * @param w2
   *   second keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> searchWithAnd(String w1, String w2)
  {
    // TODO
    return null;
  }
  
  /**
   * Searches the index for pages containing at least one of the keywords w1
   * and w2.  Returns a list of qualifying
   * urls ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of w1 plus number of occurrences of w2, all multiplied by the 
   * indegree of its url in the associated graph.
   * No pages with rank zero are included.
   * @param w1
   *   first keyword to search for
   * @param w2
   *   second keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> searchWithOr(String w1, String w2)
  {
    // TODO
    return null;
  }
  
  /**
   * Searches the index for pages containing keyword w1
   * but NOT w2.  Returns a list of qualifying urls
   * ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of w1, multiplied by the 
   * indegree of its url in the associated graph.
   * No pages with rank zero are included.
   * @param w1
   *   first keyword to search for
   * @param w2
   *   second keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> searchAndNot(String w1, String w2)
  {
    // TODO
    return null;
  }

  public void mergeSort(List<Ranked> output, int start,  int end){
    if(start<end){
      int middle = start+(end-1)/2;
      mergeSort(output, start, middle);
      mergeSort(output, middle+1, end);
      merge(output, start, middle, end);
    }
  }

  public void merge(List<Ranked> output, int start, int middle, int end){
    int i,j,k;
    int leftSize = middle - start + 1;
    int rightSize = end - middle;
    ArrayList<Ranked> left = new ArrayList<>();
    ArrayList<Ranked> right = new ArrayList<>();

    for (i=0; i<leftSize; i++){
      left.set(i, output.get(start+i));
    }
    for(j=0; j<rightSize; j++){
      right.set(j, output.get(middle+start+j));
    }
    i = 0;
    j = 0;
    k = 0;
    while(i<leftSize && j<rightSize){
      if(left.get(i).indegrees*left.get(i).wc >= right.get(i).indegrees*right.get(i).wc){
        output.set(k, left.get(i));
        i++;
      }
      else{
        output.set(k, right.get(j));
        j++;
      }
      k++;
    }
  }

  private class Ranked{
    int indegrees;
    int wc;
    String page;

    public Ranked(int indegrees, int wc, String page) {
      this.indegrees = indegrees;
      this.wc = wc;
    }
  }

}
