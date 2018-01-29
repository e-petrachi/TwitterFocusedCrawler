package api.tagme4j.response;

import api.tagme4j.model.Annotation;

import java.util.List;

public class TagResponse extends TagMeResponse {

    private String test;
    private List<Annotation> annotations;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }
}
