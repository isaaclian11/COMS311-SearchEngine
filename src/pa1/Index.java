package pa1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import api.TaggedVertex;
import api.Util;


/**
 * Implementation of an inverted index for a web graph.
 * 
 * @author Isaac Sanga, Justin Worley
 */
public class Index
{

  List<TaggedVertex<String>> urls;
  HashMap<String, HashMap<TaggedVertex, Integer>> list = new HashMap<>();
  HashMap<String, List<TaggedVertex<String>>> memo = new HashMap<>();
  HashMap<String, HashMap<String,List<Ranked>>> rankedMemo = new HashMap<>();
  JSoupAPI jSoupAPI;

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
    jSoupAPI = new JSoupAPI();
  }
  
  /**
   * Creates the index.
   */
  public void makeIndex()
  {
    // TODO
    for(TaggedVertex vertex: urls){
        String url = (String) vertex.getVertexData();
        String body = jSoupAPI.getBody(url);
        Scanner scanner = new Scanner(body);
        HashMap<String, Integer> wordCount = new HashMap<>();
        while(scanner.hasNext()){
          String current = scanner.next();
          current = Util.stripPunctuation(current);
          if(wordCount.containsKey(current) && !Util.isStopWord(current)){
            int count = wordCount.get(current);
            count++;
            wordCount.replace(current, count);
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
    if(memo.containsKey(w)){
      return memo.get(w);
    }
    w = Util.stripPunctuation(w);
    HashMap<TaggedVertex, Integer> unrankedMap = list.get(w);
    List<Ranked> unrankedList = new ArrayList<>();
    for(TaggedVertex vertex: unrankedMap.keySet()){
      unrankedList.add(new Ranked(vertex.getTagValue(), unrankedMap.get(vertex), (String) vertex.getVertexData()));
    }
    mergeSort(unrankedList, 0, unrankedList.size()-1, Operators.NONE);
    List<TaggedVertex<String>> output = new ArrayList<>();
    for(int i=0; i<unrankedList.size(); i++){
      output.add(new TaggedVertex<>(unrankedList.get(i).page, i));
    }
    memo.put(w, output);
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
    List<TaggedVertex<String>> output = twoWords(w1, w2, Operators.AND);

    return output;
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
    return twoWords(w1, w2, Operators.OR);
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
    return twoWords(w1, w2, Operators.NOT);
  }

  public void mergeSort(List<Ranked> output, int start,  int end, Operators operator){
    if(start<end){
      int middle = start+(end-start)/2;
      mergeSort(output, start, middle, operator);
      mergeSort(output, middle+1, end, operator);
      merge(output, start, middle, end, operator);
    }
  }

  public void merge(List<Ranked> output, int start, int middle, int end, Operators operator){
    int i,j,k;
    int leftSize = middle - start + 1;
    int rightSize = end - middle;
    ArrayList<Ranked> left = new ArrayList<>();
    ArrayList<Ranked> right = new ArrayList<>();

    for (i=0; i<leftSize; i++){
      left.add(output.get(start+i));
    }
    for(j=0; j<rightSize; j++){
      right.add(output.get(middle+1+j));
    }
    i = 0;
    j = 0;
    k = start;

    if(operator==Operators.NONE) {
      while (i < leftSize && j < rightSize) {
        if (left.get(i).indegrees * left.get(i).wc >= right.get(j).indegrees * right.get(j).wc) {
          output.set(k, left.get(i));
          i++;
        } else {
          output.set(k, right.get(j));
          j++;
        }
        k++;
      }
      while(i<leftSize){
        output.set(k, left.get(i));
        i++;
        k++;
      }
      while(j<rightSize){
        output.set(k, right.get(j));
        j++;
        k++;
      }
    }
    else if(operator==Operators.AND || operator==Operators.OR){
      while (i < leftSize && j < rightSize) {
        if(left.get(i).indegrees*left.get(i).wc + left.get(i).indegrees*left.get(i).wc2 >=
                right.get(j).indegrees*right.get(j).wc + right.get(j).indegrees*right.get(j).wc2){
          output.set(k, left.get(i));
          i++;
        }
        else{
          output.set(k, right.get(j));
          j++;
        }
        k++;
      }
      while(i<leftSize){
        output.set(k, left.get(i));
        i++;
        k++;
      }
      while(j<rightSize){
        output.set(k, right.get(j));
        j++;
        k++;
      }
    }
    else if(operator==Operators.NOT){
      while (i < leftSize && j < rightSize) {
        if(left.get(i).indegrees*left.get(i).wc >= right.get(j).indegrees*right.get(j).wc){
          output.set(k, left.get(i));
          i++;
        }
        else{
          output.set(k, right.get(j));
          j++;
        }
      }
      k++;
    }
    while(i<leftSize){
      output.set(k, left.get(i));
      i++;
      k++;
    }
    while(j<rightSize){
      output.set(k, right.get(j));
      j++;
      k++;
    }
  }

  private List<TaggedVertex<String>> twoWords(String w1, String w2, Operators operators){
    HashMap<TaggedVertex, Integer> unrankedMapW1 = list.get(w1);
    HashMap<TaggedVertex, Integer> unrankedMapW2 = list.get(w2);

    HashMap<String, Integer> indicesHolder = new HashMap<>();
    List<Ranked> combined = new ArrayList<>();

    if(rankedMemo.containsKey(w1)){
      if(rankedMemo.get(w1).containsKey(w2)){
        combined = rankedMemo.get(w1).get(w2);
      }
    }
    else{
      int i = 0;

      for(TaggedVertex vertex: unrankedMapW1.keySet()){
          indicesHolder.put((String) vertex.getVertexData(), i);
          Ranked toInsert = new Ranked(vertex.getTagValue(), unrankedMapW1.get(vertex), (String) vertex.getVertexData());
          toInsert.setWc2(0);
          combined.add(toInsert);
          i++;
      }

      for(TaggedVertex vertex: unrankedMapW2.keySet()){
        if(indicesHolder.containsKey(vertex.getVertexData())){
          int index = indicesHolder.get(vertex.getVertexData());
          combined.get(index).setWc2(unrankedMapW2.get(vertex));
        }
        else{
          Ranked toInsert = new Ranked(vertex.getTagValue(), 0, (String) vertex.getVertexData());
          combined.add(toInsert);
          toInsert.setWc2(unrankedMapW2.get(vertex));
          indicesHolder.put((String) vertex.getVertexData(), i);
          i++;
        }
      }
      if(operators==Operators.AND) {
        for (int k = combined.size() - 1; k >= 0; k--) {
          if (combined.get(k).wc2 == 0 || combined.get(k).wc == 0) {
            combined.remove(k);
          }
        }
      }
      else if(operators==Operators.NOT){
        for (int k = combined.size() - 1; k >= 0; k--) {
          if (combined.get(k).wc == 0 && combined.get(k).wc2 != 0) {
            combined.remove(k);
          }
        }
      }
    }
    mergeSort(combined, 0, combined.size()-1, operators);

    HashMap<String, List<Ranked>> ranked = new HashMap<>();
    ranked.put(w2, combined);
    rankedMemo.put(w1, ranked);

    List<TaggedVertex<String>> output = new ArrayList<>();
    for(int i=0; i<combined.size(); i++){
      output.add(new TaggedVertex<>(combined.get(i).page, i));
    }
    return output;
  }

  private class Ranked{
    int indegrees;
    int wc;
    String page;
    int wc2;

    public Ranked(int indegrees, int wc, String page) {
      this.indegrees = indegrees;
      this.wc = wc;
      this.page = page;
    }

    public void setWc2(int wc2){
      this.wc2 = wc2;
    }

  }



  private enum Operators{
    NONE,
    AND,
    OR,
    NOT
  }

}
