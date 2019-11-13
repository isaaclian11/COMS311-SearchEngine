package pa1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class JSoupAPI {
    public String[] getLinks(String url, PolitenessPolicy politenessPolicy) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select("a[href]");
        String[] result = new String[elements.size()];
        for (int i=0; i<elements.size(); i++) {
            result[i] = elements.get(i).attr("abs:href");
        }
        return result;
    }
}
