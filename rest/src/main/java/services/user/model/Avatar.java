package services.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * Created by Sergey Kobets on 21.04.2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Avatar {
    private String xxs;
    private String xs;
    private String s;
    private String m;
    private String l;


    public String getXxs() {
        return xxs;
    }

    public void setXxs(String xss) {
        this.xxs = xss;
    }

    public String getXs() {
        return xs;
    }

    public void setXs(String xs) {
        this.xs = xs;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "xxs='" + xxs + '\'' +
                ", xs='" + xs + '\'' +
                ", s='" + s + '\'' +
                ", m='" + m + '\'' +
                ", l='" + l + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avatar avatar = (Avatar) o;
        return Objects.equals(xxs, avatar.xxs) &&
                Objects.equals(xs, avatar.xs) &&
                Objects.equals(s, avatar.s) &&
                Objects.equals(m, avatar.m) &&
                Objects.equals(l, avatar.l);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xxs, xs, s, m, l);
    }
}
