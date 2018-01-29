package api.tagme4j.response;

import api.tagme4j.model.Mention;

import java.util.List;

public class SpotResponse extends TagMeResponse {

    private List<Mention> spots;

    public List<Mention> getSpots() {
        return spots;
    }

    public void setSpots(List<Mention> spots) {
        this.spots = spots;
    }
}
