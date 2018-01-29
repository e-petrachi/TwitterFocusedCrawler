package api.tagme4j.request;

import com.google.gson.Gson;
import api.tagme4j.TagMeClient;
import api.tagme4j.TagMeException;
import api.tagme4j.response.TagMeResponse;
import okhttp3.*;

public abstract class TagMeRequest<T extends TagMeResponse> {

    private TagMeClient tagMeClient;
    protected HttpUrl.Builder builder;
    private String path;
    private Class<T> clazz;

    public TagMeRequest(TagMeClient tagMeClient, String path, Class<T> clazz) {
        this.tagMeClient = tagMeClient;
        this.path = path;
        this.clazz = clazz;
        this.builder = new HttpUrl.Builder();
    }

    protected HttpUrl getUrl() {
        return builder.scheme(TagMeClient.getScheme())
                .host(TagMeClient.getHost())
                .addPathSegments(path)
                .setQueryParameter("gcube-token", tagMeClient.getApikey())
                .build();
    }

    protected abstract Request getRequest();

    public T execute() throws TagMeException {
        T tagMeResponse;

        try {
            OkHttpClient client = tagMeClient.getClient();
            Gson gson = tagMeClient.getGson();

            Response response = client.newCall(getRequest()).execute();

            if (response.isSuccessful()) {
                String json = response.body().string();
                tagMeResponse = gson.fromJson(json, clazz);

            } else {
                throw new TagMeException(
                        String.format("Request to TagMe failed with HTTP code %d, message: %s",
                                response.code(),
                                response.body().string()
                        )
                );
            }

        } catch (TagMeException e) {
            throw e;

        } catch (Exception e) {
            throw new TagMeException("Error executing TagMeRequest", e);
        }

        return tagMeResponse;
    }

}
