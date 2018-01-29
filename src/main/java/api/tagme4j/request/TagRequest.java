package api.tagme4j.request;

import api.tagme4j.TagMeClient;
import api.tagme4j.TagMeException;
import api.tagme4j.response.TagResponse;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TagRequest extends TagMeRequest<TagResponse> {

    private String text;

    public TagRequest(TagMeClient client) {
        super(client, "tagme/tag", TagResponse.class);
    }

    /**
     * required
     *
     * The text to be annotated, using UTF-8 encoding.
     * This implementation use POST as default to avoid any error for very long text.
     * The size of an HTTP request to this server is limited to 100Kb.
     * Anyway recall that TagMe's strength relies in its ability to annotate short texts
     * and it would be better to differently tune it when dealing with long texts.
     *
     * @param text the text to be annotated, using UTF-8 encoding
     * @return
     */
    public TagRequest text(String text) {
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
    public TagRequest lang(String lang) {
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
    public TagRequest tweet(boolean tweet) {
        builder.setQueryParameter("tweet", Boolean.toString(tweet));
        return this;
    }

    /**
     * optional
     *
     * If this option is enabled, for each disambiguated spot,
     * the response includes also the abstract of the related Wikipedia page.
     *
     * Supported values are "true" and "false", default is "false".
     *
     * @param includeAbstract includes also the abstract of the related Wikipedia page
     * @return
     */
    public TagRequest includeAbstract(boolean includeAbstract) {
        builder.setQueryParameter("include_abstract", Boolean.toString(includeAbstract));
        return this;
    }

    /**
     * optional
     *
     *- If this option is enabled, for each disambiguated spot,
     * the response includes also the list of categories which the related Wikipedia page belongs to.
     * The list of categories is provided by DBpedia (currently this feature is based on DBpedia version 3.8).
     *
     * Supported values are "true" and "false", default is "false".
     *
     * @param includeCategories includes also the list of categories which the related Wikipedia page belongs to
     * @return
     */
    public TagRequest includeCategories(boolean includeCategories) {
        builder.setQueryParameter("include_categories", Boolean.toString(includeCategories));
        return this;
    }

    /**
     * optional
     *
     * If this option is enabled, the response will contain information about all spots found in the input text,
     * including those ones that TagMe was not able to annotate with an entity.
     * In such cases, JSON objects for un-tagged spots do not contain details about topic, like id, title, etc...
     *
     * Supported values are "true" and "false", default is "false".
     *
     * @param includeAllSpots includes also the information about all spots found in the input text
     * @return
     */
    public TagRequest includeAllSpots(boolean includeAllSpots) {
        builder.setQueryParameter("include_all_spots", Boolean.toString(includeAllSpots));
        return this;
    }

    /**
     * optional
     *
     * TagMe is designed to annote short text but it resulted competitive on long texts too.
     * When annotating long texts, TagMe processes just a limited portion of the input text at once,
     * namely a window of spots, and annotates a spot only using the surrounding spots in that window.
     * This parameter lets you to specify the shifting window for long text.
     * If you want to disable this mechanism and force TagMe to always process
     * the whole text set this parameter to zero.
     * In this latter case please be aware that taking into account all spots in a long text
     * might induce dangerous topic drifts, which could jeopardise the effectiveness of the annotation.
     *
     * Supported values are integers starting from 0.
     *
     * @param longText includes also the information about all spots found in the input text
     * @return
     */
    public TagRequest longText(int longText) throws TagMeException {
        if(longText < 0) {
            throw new TagMeException("Not valid longText value. Received a negative integer: " + longText);
        }
        builder.setQueryParameter("long_text", String.valueOf(longText));
        return this;
    }

    /**
     * optional
     *
     * This parameter can be used to finely tune the disambiguation process:
     * an higher value will favor the most-common topics for a spot,
     * whereas a lower value will take more into account the context.
     * This parameter could be useful when annotating particularly fragmented text, like tweets,
     * where it would be better to favor most common topics
     * because the context is less reliable for disambiguation.
     *
     * Supported values are floats in the range [0,0.5], default is 0.3.
     *
     * @param epsilon fine tune the disambiguation process
     * @return
     */
    public TagRequest epsilon(float epsilon) throws TagMeException {
        if(epsilon < 0 || epsilon > 0.5) {
            throw new TagMeException("Not valid epsilon value. Accepted range [0, 0.5]. Received: " + epsilon);
        }
        builder.setQueryParameter("epsilon", Float.toString(epsilon));
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
