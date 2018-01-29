package api.tagme4j.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Annotation {

    private long id;
    private String title;
    private int start;
    private int end;
    private double rho;
    private String spot;
    @SerializedName("link_probability")
    private double linkProbability;
    @SerializedName("dbpedia_categories")
    private List<String> dbpediaCategories;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public double getRho() {
        return rho;
    }

    public void setRho(double rho) {
        this.rho = rho;
    }

    public String getSpot() {
        return spot;
    }

    public void setSpot(String spot) {
        this.spot = spot;
    }

    public double getLinkProbability() {
        return linkProbability;
    }

    public void setLinkProbability(double linkProbability) {
        this.linkProbability = linkProbability;
    }

    public List<String> getDbpediaCategories() {
        return dbpediaCategories;
    }

    public void setDbpediaCategories(List<String> dbpediaCategories) {
        this.dbpediaCategories = dbpediaCategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Annotation that = (Annotation) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", rho=" + rho +
                ", spot='" + spot + '\'' +
                ", linkProbability=" + linkProbability +
                ", dbpediaCategories=" + dbpediaCategories +
                '}';
    }
}
