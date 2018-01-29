package api.tagme4j.model;

public class Relatedness {

    private String entity1;
    private String entity2;

    private double rel;
    private String err;

    public Relatedness(String entity1, String entity2, double rel) {
        this.entity1 = entity1;
        this.entity2 = entity2;
        this.rel = rel;
    }

    public Relatedness(String entity1, String entity2, String err) {
        this.entity1 = entity1;
        this.entity2 = entity2;
        this.err = err;
    }

    public String getEntity1() {
        return entity1;
    }

    public String getEntity2() {
        return entity2;
    }

    public double getRel() {
        return rel;
    }

    public String getErr() {
        return err;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relatedness that = (Relatedness) o;

        if (entity1 != null ? !entity1.equals(that.entity1) : that.entity1 != null) return false;
        return entity2 != null ? entity2.equals(that.entity2) : that.entity2 == null;

    }

    @Override
    public int hashCode() {
        int result = entity1 != null ? entity1.hashCode() : 0;
        result = 31 * result + (entity2 != null ? entity2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Relatedness{" +
                "entity1='" + entity1 + '\'' +
                ", entity2='" + entity2 + '\'' +
                ", rel=" + rel +
                ", err='" + err + '\'' +
                '}';
    }
}
