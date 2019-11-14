package pa1;

import api.Graph;
import api.TaggedVertex;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CrawlerJUnit {

    @Mock
    JSoupAPI jsoupMock = mock(JSoupAPI.class);


    List<String> vertexList;
    List<String[]> internet;
    List<String> body;

    @Before
    public void initializeMockito(){
        MockitoAnnotations.initMocks(this);

        //Set up our "internet" with its linked webpages
        //This is pulled directly from the PDF: 4.1 Crawler
        vertexList = new ArrayList<>(Arrays.asList(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"
        ));
        internet = new ArrayList<>();
        internet.add(0, new String[]{"B", "C", "D"});       //A
        internet.add(1, new String[]{"C", "I", "J"});       //B
        internet.add(2, new String[]{"E", "F", "B", "D"});  //C
        internet.add(3, new String[]{"G", "H", "A"});       //D
        internet.add(4, new String[]{"A"});                 //E
        internet.add(5, new String[]{});                    //F
        internet.add(6, new String[]{});                    //G
        internet.add(7, new String[]{});                    //H
        internet.add(8, new String[]{});                    //I
        internet.add(9, new String[]{});                    //J

        body = new ArrayList<>();

        body.add("Hello, this is a test happy happy happy " +
                "happy happy happy happy happy happy happy");                      //A

        body.add("graph graph graph graph happy happy happy " +
                "happy happy happy happy happy");                                  //B

        body.add("The an a in of happy happy happy " +
                "happy happy happy happy");                                        //C

        body.add("One one one one tasks " +
                "tasks tasks tasks happy happy " +
                "happy happy happy");                                              //D

        body.add("A; bunch. of, (punctuation)");                                   //E

        body.add("When searching bottle, this " +
                "should be the first result " +
                "bottle bottle bottle bottle " +
                "bottle bottle bottle " +
                "happy happy happy happy");                                        //F

        body.add("When searching bottle, this " +
                "should be the second result " +
                "bottle bottle bottle bottle " +
                "happy happy happy");                                              //G

        body.add("Other random words happy happy");                                //H
        body.add("bottle Bottle BoTTle BottlE happy");                             //I
        body.add("bottle bottle bottle");                                          //J




    }

    @Test
    public void crawlerTestDepth6() {

        String seed = "A";
        Crawler crawler = new Crawler(seed, 4, 6);
        crawler.jSoupAPI = jsoupMock;
        List<String> finalGraph = new ArrayList<>(Arrays.asList(
                "A", "B", "C", "D", "I", "J"
        ));

        //Replace normal JSoupAPI calls to actual webpages with our fake stuff below
        when(jsoupMock.getLinks(anyString())).thenAnswer(
                (Answer<String[]>) invoc -> getFakeLinkLists((String) invoc.getArguments()[0]));

        Graph<String> graph = crawler.crawl();
        assertEquals(graph.vertexData(), finalGraph);

        //Check incoming edges
        assertEquals(graph.getIncoming(0), new ArrayList<>(Arrays.asList(
                3
        )));    //A incoming edges should be D
        assertEquals(graph.getIncoming(1), new ArrayList<>(Arrays.asList(
                0, 2
        )));    //B incoming edges should be A, C
        assertEquals(graph.getIncoming(2), new ArrayList<>(Arrays.asList(
                0, 1
        )));    //C incoming edges should be A, B
        assertEquals(graph.getIncoming(3), new ArrayList<>(Arrays.asList(
                0, 2
        )));    //D incoming edges should be A, C
        assertEquals(graph.getIncoming(4), new ArrayList<>(Arrays.asList(
                1
        )));    //I incoming edges should be B
        assertEquals(graph.getIncoming(5), new ArrayList<>(Arrays.asList(
                1
        )));    //J incoming edges should be B


        //Check outgoing edges
        assertEquals(graph.getNeighbors(0), new ArrayList<>(Arrays.asList(
                1, 2, 3
        )));    //A outgoing edges should be B, C, D
        assertEquals(graph.getNeighbors(1), new ArrayList<>(Arrays.asList(
                2, 4, 5
        )));    //B outgoing edges should be C, I, J
        assertEquals(graph.getNeighbors(2), new ArrayList<>(Arrays.asList(
                1, 3
        )));    //C outgoing edges should be B, D
        assertEquals(graph.getNeighbors(3), new ArrayList<>(Arrays.asList(
                0
        )));    //D outgoing edges should be A
        assertEquals(graph.getNeighbors(4), new ArrayList<>(Arrays.asList(

        )));    //I outgoing edges should be []
        assertEquals(graph.getNeighbors(5), new ArrayList<>(Arrays.asList(

        )));    //J outgoing edges should be []

    }

    @Test
    public void crawlerTestDepth1() {

        String seed = "A";
        Crawler crawler = new Crawler(seed, 1, 6);
        crawler.jSoupAPI = jsoupMock;

        List<String> finalGraph = new ArrayList<>(Arrays.asList(
                "A", "B", "C", "D"
        ));

        //Replace normal JSoupAPI calls to actual webpages with our fake stuff below
        when(jsoupMock.getLinks(anyString())).thenAnswer(
                (Answer<String[]>) invoc -> getFakeLinkLists((String) invoc.getArguments()[0]));

        Graph<String> graph = crawler.crawl();
        assertEquals(graph.vertexData(), finalGraph);

        //Check incoming edges (As depth = 1, they should only be from A)
        assertEquals(graph.getIncoming(0), new ArrayList<>(Arrays.asList(
            3
        )));    //A incoming edges should be [D]
        assertEquals(graph.getIncoming(1), new ArrayList<>(Arrays.asList(
                0,2
        )));    //B incoming edges should be [A,C]
        assertEquals(graph.getIncoming(2), new ArrayList<>(Arrays.asList(
                0,1
        )));    //C incoming edges should be A
        assertEquals(graph.getIncoming(3), new ArrayList<>(Arrays.asList(
                0,2
        )));    //D incoming edges should be A


//        //Check outgoing edges
        assertEquals(graph.getNeighbors(0), new ArrayList<>(Arrays.asList(
                1, 2, 3
        )));    //A outgoing edges should be B, C, D
        assertEquals(graph.getNeighbors(1), new ArrayList<>(Arrays.asList(
            2
        )));    //B outgoing edges should be 2
        assertEquals(graph.getNeighbors(2), new ArrayList<>(Arrays.asList(
            1,3
        )));    //C outgoing edges should be []
        assertEquals(graph.getNeighbors(3), new ArrayList<>(Arrays.asList(
            0
        )));    //D outgoing edges should be []

    }

    @Test
    public void vertexdDataTest() throws IOException {
        String seed = "A";
        Crawler crawler = new Crawler(seed, 4, 6);
        crawler.jSoupAPI = jsoupMock;

        when(jsoupMock.getLinks(anyString())).thenAnswer(
                (Answer<String[]>) invoc -> getFakeLinkLists((String) invoc.getArguments()[0]));

        Graph<String> graph = crawler.crawl();

        ArrayList<TaggedVertex<String>> actual;

        actual = graph.vertexDataWithIncomingCounts();

        TaggedVertex<String> a = new TaggedVertex<>("A", 2);
        TaggedVertex<String> b = new TaggedVertex<>("B", 2);
        TaggedVertex<String> c = new TaggedVertex<>("C", 2);
        TaggedVertex<String> d = new TaggedVertex<>("D", 2);
        TaggedVertex<String> i = new TaggedVertex<>("I", 1);
        TaggedVertex<String> j = new TaggedVertex<>("J", 1);

        ArrayList<TaggedVertex<String>> expected = new ArrayList<>(Arrays.asList(a,b,c,d,i,j));

        //Tests incoming counts
        for(int index=0; index<expected.size(); index++){
            System.out.println("Testing " + index);
            assertEquals(expected.get(index).getTagValue(), actual.get(index).getTagValue());
            assertEquals(expected.get(index).getVertexData(), actual.get(index).getVertexData());
        }

    }

    @Test
    public void vertexDataTest2() throws IOException {
        String seed = "A";
        Crawler crawler = new Crawler(seed, 4, 12);
        crawler.jSoupAPI = jsoupMock;

        when(jsoupMock.getLinks(anyString())).thenAnswer(
                (Answer<String[]>) invoc -> getFakeLinkLists((String) invoc.getArguments()[0]));

        Graph<String> graph = crawler.crawl();

        ArrayList<TaggedVertex<String>> actual;

        actual = graph.vertexDataWithIncomingCounts();

        TaggedVertex<String> a = new TaggedVertex<>("A", 3);
        TaggedVertex<String> b = new TaggedVertex<>("B", 2);
        TaggedVertex<String> c = new TaggedVertex<>("C", 2);
        TaggedVertex<String> d = new TaggedVertex<>("D", 2);
        TaggedVertex<String> e = new TaggedVertex<>("E", 1);
        TaggedVertex<String> f = new TaggedVertex<>("F", 1);
        TaggedVertex<String> g = new TaggedVertex<>("G", 1);
        TaggedVertex<String> h = new TaggedVertex<>("H", 1);
        TaggedVertex<String> i = new TaggedVertex<>("I", 1);
        TaggedVertex<String> j = new TaggedVertex<>("J", 1);

        ArrayList<TaggedVertex<String>> expected = new ArrayList<>(Arrays.asList(a,b,c,d,i,j,e,f,g,h));

        //Tests whether the graph returns the correct vertices with the right incoming counts
        for(int index=0; index<expected.size(); index++){
            System.out.println("Testing " + index);
            assertEquals(expected.get(index).getTagValue(), actual.get(index).getTagValue());
            assertEquals(expected.get(index).getVertexData(), actual.get(index).getVertexData());
        }

    }

    //Tests if output is correctly ranked
    @Test
    public void searchTest(){

        TaggedVertex<String> a = new TaggedVertex<>("A", 3);
        TaggedVertex<String> b = new TaggedVertex<>("B", 2);
        TaggedVertex<String> c = new TaggedVertex<>("C", 2);
        TaggedVertex<String> d = new TaggedVertex<>("D", 2);
        TaggedVertex<String> e = new TaggedVertex<>("E", 1);
        TaggedVertex<String> f = new TaggedVertex<>("F", 1);
        TaggedVertex<String> g = new TaggedVertex<>("G", 1);
        TaggedVertex<String> h = new TaggedVertex<>("H", 1);
        TaggedVertex<String> i = new TaggedVertex<>("I", 1);
        TaggedVertex<String> j = new TaggedVertex<>("J", 1);

        ArrayList<TaggedVertex<String>> urls = new ArrayList<>(Arrays.asList(a,b,c,d,e,f,g,h,i,j));

        Index testIndex = new Index(urls);
        testIndex.jSoupAPI = jsoupMock;

        when(jsoupMock.getBody(anyString())).thenAnswer(
                (Answer<String>) invoc -> getFakeBody((String) invoc.getArguments()[0]));

        testIndex.makeIndex();

        List<TaggedVertex<String>> expected = new ArrayList<>(Arrays.asList(f,g,i,j));
        List<TaggedVertex<String>> actual = testIndex.search("bottle");

        //Should return f,g,i,j in that order
        for(int index=0; index<expected.size(); index++){
            assertEquals(expected.get(index).getVertexData(), actual.get(index).getVertexData());
        }

    }

    //Tests if word in root page gets searched
    @Test
    public void searchTest2(){

        TaggedVertex<String> a = new TaggedVertex<>("A", 1);

        ArrayList<TaggedVertex<String>> urls = new ArrayList<>(Arrays.asList(a));

        Index testIndex = new Index(urls);
        testIndex.jSoupAPI = jsoupMock;

        when(jsoupMock.getBody(anyString())).thenAnswer(
                (Answer<String>) invoc -> getFakeBody((String) invoc.getArguments()[0]));

        testIndex.makeIndex();

        List<TaggedVertex<String>> expected = new ArrayList<>(Arrays.asList(a));
        List<TaggedVertex<String>> actual = testIndex.search("hello");

        //Should just return a
        for(int index=0; index<expected.size(); index++){
            assertEquals(expected.get(index).getVertexData(), actual.get(index).getVertexData());
        }

    }

    @Test
    public void searchTest3(){

        String seed = "A";
        Crawler crawler = new Crawler(seed, 4, 11);
        crawler.jSoupAPI = jsoupMock;
        List<String> finalGraph = new ArrayList<>(Arrays.asList(
                "A", "B", "C", "D", "I", "J", "E", "F", "G", "H"
        ));

        //Replace normal JSoupAPI calls to actual webpages with our fake stuff below
        when(jsoupMock.getLinks(anyString())).thenAnswer(
                (Answer<String[]>) invoc -> getFakeLinkLists((String) invoc.getArguments()[0]));

        Graph<String> graph = crawler.crawl();
        assertEquals(graph.vertexData(), finalGraph);

        ArrayList<TaggedVertex<String>> urls = graph.vertexDataWithIncomingCounts();

        Index testIndex = new Index(urls);
        testIndex.jSoupAPI = jsoupMock;

        when(jsoupMock.getBody(anyString())).thenAnswer(
                (Answer<String>) invoc -> getFakeBody((String) invoc.getArguments()[0]));

        testIndex.makeIndex();

        TaggedVertex<String> a = new TaggedVertex<>("A", 3);
        TaggedVertex<String> b = new TaggedVertex<>("B", 2);
        TaggedVertex<String> c = new TaggedVertex<>("C", 2);
        TaggedVertex<String> d = new TaggedVertex<>("D", 2);
        TaggedVertex<String> e = new TaggedVertex<>("E", 1);
        TaggedVertex<String> f = new TaggedVertex<>("F", 1);
        TaggedVertex<String> g = new TaggedVertex<>("G", 1);
        TaggedVertex<String> h = new TaggedVertex<>("H", 1);
        TaggedVertex<String> i = new TaggedVertex<>("I", 1);

        List<TaggedVertex<String>> expected = new ArrayList<>(Arrays.asList(a,b,c,d,f,g,h,i));
        List<TaggedVertex<String>> actual = testIndex.search("happy");

        for(int index=0; index<expected.size(); index++){
            //Should return a,b,c,d,e,f,g,h,i in that order
            assertEquals(expected.get(index).getVertexData(), actual.get(index).getVertexData());
        }

    }

    @Test
    public void searchANDTest(){

        TaggedVertex<String> a = new TaggedVertex<>("A", 2);
        TaggedVertex<String> b = new TaggedVertex<>("B", 2);
        TaggedVertex<String> c = new TaggedVertex<>("C", 2);
        TaggedVertex<String> d = new TaggedVertex<>("D", 2);
        TaggedVertex<String> e = new TaggedVertex<>("E", 1);
        TaggedVertex<String> f = new TaggedVertex<>("F", 1);
        TaggedVertex<String> g = new TaggedVertex<>("G", 1);
        TaggedVertex<String> h = new TaggedVertex<>("H", 1);
        TaggedVertex<String> i = new TaggedVertex<>("I", 1);
        TaggedVertex<String> j = new TaggedVertex<>("J", 1);

        ArrayList<TaggedVertex<String>> urls = new ArrayList<>(Arrays.asList(a,b,c,d,e,f,g,h,i,j));

        Index testIndex = new Index(urls);
        testIndex.jSoupAPI = jsoupMock;

        when(jsoupMock.getBody(anyString())).thenAnswer(
                (Answer<String>) invoc -> getFakeBody((String) invoc.getArguments()[0]));

        testIndex.makeIndex();

        List<TaggedVertex<String>> expected = new ArrayList<>(Arrays.asList(f,g,i));
        List<TaggedVertex<String>> actual = testIndex.searchWithAnd("bottle", "happy");

        //Should return f,g,i in that order
        for(int index=0; index<expected.size(); index++){
            assertEquals(expected.get(index).getVertexData(), actual.get(index).getVertexData());
        }

    }

    @Test
    public void searchORTest(){

        TaggedVertex<String> a = new TaggedVertex<>("A", 2);
        TaggedVertex<String> b = new TaggedVertex<>("B", 2);
        TaggedVertex<String> c = new TaggedVertex<>("C", 2);
        TaggedVertex<String> d = new TaggedVertex<>("D", 2);
        TaggedVertex<String> e = new TaggedVertex<>("E", 1);
        TaggedVertex<String> f = new TaggedVertex<>("F", 1);
        TaggedVertex<String> g = new TaggedVertex<>("G", 1);
        TaggedVertex<String> h = new TaggedVertex<>("H", 1);
        TaggedVertex<String> i = new TaggedVertex<>("I", 1);
        TaggedVertex<String> j = new TaggedVertex<>("J", 1);

        ArrayList<TaggedVertex<String>> urls = new ArrayList<>(Arrays.asList(a,b,c,d,e,f,g,h,i,j));

        Index testIndex = new Index(urls);
        testIndex.jSoupAPI = jsoupMock;

        when(jsoupMock.getBody(anyString())).thenAnswer(
                (Answer<String>) invoc -> getFakeBody((String) invoc.getArguments()[0]));

        testIndex.makeIndex();

        List<TaggedVertex<String>> expected = new ArrayList<>(Arrays.asList(a,b,c,f,d,g,i,j,h));
        List<TaggedVertex<String>> actual = testIndex.searchWithOr("bottle", "happy");

        //Should return a,b,c,d,g,i,j,h in that order
        for(int index=0; index<expected.size(); index++){
            assertEquals(expected.get(index).getVertexData(), actual.get(index).getVertexData());
        }

    }

    @Test
    public void searchNOTTest(){

        TaggedVertex<String> a = new TaggedVertex<>("A", 2);
        TaggedVertex<String> b = new TaggedVertex<>("B", 2);
        TaggedVertex<String> c = new TaggedVertex<>("C", 2);
        TaggedVertex<String> d = new TaggedVertex<>("D", 2);
        TaggedVertex<String> e = new TaggedVertex<>("E", 1);
        TaggedVertex<String> f = new TaggedVertex<>("F", 1);
        TaggedVertex<String> g = new TaggedVertex<>("G", 1);
        TaggedVertex<String> h = new TaggedVertex<>("H", 1);
        TaggedVertex<String> i = new TaggedVertex<>("I", 1);
        TaggedVertex<String> j = new TaggedVertex<>("J", 1);

        ArrayList<TaggedVertex<String>> urls = new ArrayList<>(Arrays.asList(a,b,c,d,e,f,g,h,i,j));

        Index testIndex = new Index(urls);
        testIndex.jSoupAPI = jsoupMock;

        when(jsoupMock.getBody(anyString())).thenAnswer(
                (Answer<String>) invoc -> getFakeBody((String) invoc.getArguments()[0]));

        testIndex.makeIndex();

        List<TaggedVertex<String>> expected = new ArrayList<>(Arrays.asList(j));
        List<TaggedVertex<String>> actual = testIndex.searchAndNot("bottle", "happy");

        for(int index=0; index<expected.size(); index++){
            assertEquals(expected.get(index).getVertexData(), actual.get(index).getVertexData()); //Should return just j
        }

    }

    @Test
    public void testSearch(){
        String seed = "http://web.cs.iastate.edu/~smkautz/cs311f19/temp/a.html";
        Crawler crawler = new Crawler(seed, 4, 25);
        Graph<String> graph = crawler.crawl();

        List<TaggedVertex<String>> urls = graph.vertexDataWithIncomingCounts();
        Index index = new Index(urls);
        index.makeIndex();

        List<TaggedVertex<String>> actual = index.search("link");

        TaggedVertex<String> a = new TaggedVertex<>("http://web.cs.iastate.edu/~smkautz/cs311f19/temp/a.html", 9);
        TaggedVertex<String> c = new TaggedVertex<>("http://web.cs.iastate.edu/~smkautz/cs311f19/temp/c.html", 8);
        TaggedVertex<String> b = new TaggedVertex<>("http://web.cs.iastate.edu/~smkautz/cs311f19/temp/b.html", 6);
        TaggedVertex<String> d = new TaggedVertex<>("http://web.cs.iastate.edu/~smkautz/cs311f19/temp/d.html", 6);
        TaggedVertex<String> e = new TaggedVertex<>("http://web.cs.iastate.edu/~smkautz/cs311f19/temp/e.html", 1);

        List<TaggedVertex<String>> expected = new ArrayList<>(Arrays.asList(a,c,b,d,e));

        for(int i=0; i<expected.size(); i++){
            assertEquals(expected.get(i).getVertexData(), actual.get(i).getVertexData());
        }

    }

    @Test
    public void testSearch2(){
        String seed = "http://web.cs.iastate.edu/~smkautz/cs311f19/temp/a.html";
        Crawler crawler = new Crawler(seed, 4, 25);
        Graph<String> graph = crawler.crawl();

        List<TaggedVertex<String>> urls = graph.vertexDataWithIncomingCounts();
        Index index = new Index(urls);
        index.makeIndex();

        List<TaggedVertex<String>> actual = index.searchWithAnd("link", "Chicken");

        TaggedVertex<String> a = new TaggedVertex<>("http://web.cs.iastate.edu/~smkautz/cs311f19/temp/a.html", 9);
        TaggedVertex<String> c = new TaggedVertex<>("http://web.cs.iastate.edu/~smkautz/cs311f19/temp/c.html", 8);
        TaggedVertex<String> e = new TaggedVertex<>("http://web.cs.iastate.edu/~smkautz/cs311f19/temp/e.html", 1);

        List<TaggedVertex<String>> expected = new ArrayList<>(Arrays.asList(c,a,e));

        for(int i=0; i<expected.size(); i++){
            assertEquals(expected.get(i).getVertexData(), actual.get(i).getVertexData());
        }


    }


    /**
     * Hacky for loop to return our fake internet links.
     * Given a 'url', return a list of all outgoing urls.
     * @param link url to return outgoing edges for
     * @return String[] of outgoing edges
     */
    public String[] getFakeLinkLists(String link){
        System.out.println("Asking for links from "+link);

        //Do a search through our tiny setup to find the rest of the links
        int i;
        for(i = 0; i < vertexList.size(); i++){
            if(vertexList.get(i).equals(link))
                break;
        }

        if(i == vertexList.size()){
            System.out.println("Asked for string not within vertexList!");
            return new String[]{};
        }

        return internet.get(i);
    }

    public String getFakeBody(String link){
        System.out.println("Asking for body from "+link);

        switch (link){
            case "A":
                return body.get(0);
            case "B":
                return body.get(1);
            case "C":
                return body.get(2);
            case "D":
                return body.get(3);
            case "E":
                return body.get(4);
            case "F":
                return body.get(5);
            case "G":
                return body.get(6);
            case "H":
                return body.get(7);
            case "I":
                return body.get(8);
            case "J":
                return body.get(9);

        }
        return null;
    }
}
