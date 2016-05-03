package services.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * Created by Sergey Kobets on 17.04.2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseUserPageData {
    private String firstName;
    private String lastName;
    /*дата рождения день, месяц, год*/
    private Integer db;
    private Integer mb;
    private Integer yb;
    private String hometown;
    private Integer sex;
    private String favoriteGames;
    private String favoriteMusic;
    private String aboutMe;
    private String status;
    private Avatar avatar;
    private ThumbRectangle thumbRect;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getFavoriteGames() {
        return favoriteGames;
    }

    public void setFavoriteGames(String favoriteGames) {
        this.favoriteGames = favoriteGames;
    }

    public String getFavoriteMusic() {
        return favoriteMusic;
    }

    public void setFavoriteMusic(String favoriteMusic) {
        this.favoriteMusic = favoriteMusic;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDb() {
        return db;
    }

    public void setDb(Integer db) {
        this.db = db;
    }

    public Integer getMb() {
        return mb;
    }

    public void setMb(Integer mb) {
        this.mb = mb;
    }

    public Integer getYb() {
        return yb;
    }

    public void setYb(Integer yb) {
        this.yb = yb;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public ThumbRectangle getThumbRect() {
        return thumbRect;
    }

    public void setThumbRect(ThumbRectangle thumbRect) {
        this.thumbRect = thumbRect;
    }

    @Override
    public String toString() {
        return "BaseUserPageData{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", db=" + db +
                ", mb=" + mb +
                ", yb=" + yb +
                ", hometown='" + hometown + '\'' +
                ", sex=" + sex +
                ", favoriteGames='" + favoriteGames + '\'' +
                ", favoriteMusic='" + favoriteMusic + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", status='" + status + '\'' +
                ", avatar=" + avatar +
                ", thumbRect=" + thumbRect +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseUserPageData that = (BaseUserPageData) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(db, that.db) &&
                Objects.equals(mb, that.mb) &&
                Objects.equals(yb, that.yb) &&
                Objects.equals(hometown, that.hometown) &&
                Objects.equals(sex, that.sex) &&
                Objects.equals(favoriteGames, that.favoriteGames) &&
                Objects.equals(favoriteMusic, that.favoriteMusic) &&
                Objects.equals(aboutMe, that.aboutMe) &&
                Objects.equals(status, that.status) &&
                Objects.equals(avatar, that.avatar) &&
                Objects.equals(thumbRect, that.thumbRect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, db, mb, yb, hometown, sex, favoriteGames, favoriteMusic, aboutMe, status, avatar, thumbRect);
    }
}
