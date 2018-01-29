package api.tagme4j;

public class TagMeException extends Exception {

    public TagMeException(String message) {
        super(message);
    }

    public TagMeException(String message, Exception e) {
        super(message, e);
    }
}
