package api.tagme4j.json;

import com.google.gson.*;
import api.tagme4j.model.Relatedness;

import java.lang.reflect.Type;

public class RelatednessDeserializer implements JsonDeserializer<Relatedness> {

    public Relatedness deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        Relatedness relatedness = null;

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement coupleElem = jsonObject.get("couple");
        JsonElement relElem = jsonObject.get("rel");
        JsonElement errElem = jsonObject.get("err");

        if(coupleElem != null) {
            String couple = coupleElem.getAsString();

            if(couple.contains(" ")) {

                if(relElem != null) {
                    String[] entities = couple.split(" ");
                    double rel = relElem.getAsDouble();

                    relatedness = new Relatedness(entities[0], entities[1], rel);

                } else if(errElem != null) {
                    String[] entities = couple.split(" ");
                    String err = errElem.getAsString();

                    relatedness = new Relatedness(entities[0], entities[1], err);
                }
            }
        }

        return relatedness;
    }
}
