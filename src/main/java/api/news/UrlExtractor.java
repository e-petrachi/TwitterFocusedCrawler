package api.news;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlExtractor {
    private int extractor = 1;

    public UrlExtractor(){ }
    public UrlExtractor(int extractor){
        this.extractor = extractor;
    }
    
    // TODO potrei eliminarlo
    public String getUrlTextContent(String url) throws IOException,SAXException,BoilerpipeProcessingException{
        URL uri = new URL(url);

        final InputSource is = HTMLFetcher.fetch(uri).toInputSource();

        final BoilerpipeSAXInput in = new BoilerpipeSAXInput(is);

        final TextDocument doc = in.getTextDocument();
        String result = ArticleExtractor.INSTANCE.getText(doc);

        if(this.extractor == 0) {
            return DefaultExtractor.INSTANCE.getText(doc);
        }
        return ArticleExtractor.INSTANCE.getText(doc);
    }

    /**
     * Sembra il migliore metodo se uso ArticleExtractor
     **/
    public String getUrlOneLinerContent(String url) throws MalformedURLException, BoilerpipeProcessingException {
        URL uri = new URL(url);

        String result = "";

        if(this.extractor == 0) {
            result =  DefaultExtractor.INSTANCE.getText(uri);
        }
        result = ArticleExtractor.INSTANCE.getText(uri);

        return result.replace("\n", " ");

    }

}
