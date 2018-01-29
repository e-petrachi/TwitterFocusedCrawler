package api.tagme4j.model;

public class Mention {

    private String spot;
    private double lp;
    private int start;
    private int end;

    public String getSpot() {
        return spot;
    }

    public void setSpot(String spot) {
        this.spot = spot;
    }

    public double getLp() {
        return lp;
    }

    public void setLp(double lp) {
        this.lp = lp;
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

    @Override
    public String toString() {
        return "Mention{" +
                "spot='" + spot + '\'' +
                ", lp=" + lp +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
