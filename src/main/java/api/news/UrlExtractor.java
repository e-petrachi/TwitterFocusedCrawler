package api.news;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlExtractor {

    public UrlExtractor(){ }

    public String getUrlOneLinerContent(String url) throws MalformedURLException, BoilerpipeProcessingException {
        URL uri = new URL(url);

        String result = "";
        result = ArticleExtractor.INSTANCE.getText(uri);

        return result.replace("\n", " ");
    }

}
