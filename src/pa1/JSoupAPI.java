package pa1;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class JSoupAPI {

    int requestCount = 0;

    public String[] getLinks(String url){

        if (requestCount >= 50) {
            requestCount = 0;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            requestCount++;
        }
        catch (UnsupportedMimeTypeException e)
        {
            System.out.println("--unsupported document type, do nothing");
        }
        catch (HttpStatusException e)
        {
            System.out.println("--invalid link, do nothing");

        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elements = doc.select("a[href]");
        String[] result = new String[elements.size()];
        for (int i=0; i<elements.size(); i++) {
            result[i] = elements.get(i).attr("abs:href");
        }
        return result;
    }

    public String getBody(String url){
        if (requestCount >= 50) {
            requestCount = 0;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        requestCount++;
        String body = null;
        try {
            body = Jsoup.connect(url).get().body().text();
            requestCount++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

}
