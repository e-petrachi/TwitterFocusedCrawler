package api.tagme4j.request;

import api.tagme4j.TagMeClient;
import api.tagme4j.response.SpotResponse;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SpotRequest extends TagMeRequest<SpotResponse> {

    private String text;

    public SpotRequest(TagMeClient client) {
        super(client, "tagme/spot", SpotResponse.class);
    }

    /**
     * required
     *
     * The input text, in UTF-8 encoding, where spots have to be identified.*
     * This implementation use POST as default to avoid any error for very long text.
     * The size of an HTTP request to this server is limited to 100Kb.
     *
     * @param text the input text, in UTF-8 encoding
     * @return
     */
    public SpotRequest text(String text) {
        this.text = text;
        return this;
    }

    /**
     * optional
     *
     * The language of the text to be annotated.
     *
     * Accepted values are "de" for German, "en" for English and "it" for Italian. Default is "en".
     *
     * @param lang the language of the text to be annotated
     * @return
     */
    public SpotRequest lang(String lang) {
        builder.setQueryParameter("lang", lang);
        return this;
    }

    /**
     * optional
     *
     * Enable the special parser for Twitter messages.
     * This parser has been designed to better handle with usual
     * entities in tweets like url, user mentions and hash-tag.
     * When this option is enabled, text parameter can contain
     * the JSON dump of the tweet as directly retrieved from Twitter.
     * Refer to Twitter API for further details.
     *
     * Supported values are "true" and "false", default is "false".
     *
     * @param tweet enable the special parser for Twitter messages
     * @return
     */
    public SpotRequest tweet(boolean tweet) {
        builder.setQueryParameter("tweet", Boolean.toString(tweet));
        return this;
    }

    @Override
    protected Request getRequest() {
        RequestBody formBody = new FormBody.Builder()
                .add("text", text)
                .build();

        return new Request.Builder()
                .url(getUrl())
                .post(formBody)
                .build();
    }
}
