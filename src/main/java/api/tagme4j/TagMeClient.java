package api.tagme4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import api.tagme4j.json.RelatednessDeserializer;
import api.tagme4j.model.Relatedness;
import api.tagme4j.request.RelRequest;
import api.tagme4j.request.SpotRequest;
import api.tagme4j.request.TagRequest;
import okhttp3.*;

public class TagMeClient {

    private final static String scheme = "https";
    private final static String host   = "tagme.d4science.org";

    // TODO cambia le apikey
    private String apikey = "147bbd8a-9831-4544-8017-7dc60ad94031";
    private OkHttpClient client;
    private Gson gson;

    public TagMeClient() {
        this.client = new OkHttpClient();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Relatedness.class, new RelatednessDeserializer())
                .create();
    }

    public TagRequest tag() {
        return new TagRequest(this);
    }

    public SpotRequest spot() {
        return new SpotRequest(this);
    }

    public RelRequest rel() { return new RelRequest(this); }

    public static String getScheme() {
        return scheme;
    }

    public static String getHost() {
        return host;
    }

    public String getApikey() {
        return apikey;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public Gson getGson() {
        return gson;
    }
}
