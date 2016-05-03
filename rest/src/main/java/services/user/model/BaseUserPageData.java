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
    private Integer lang;
    private Double tz;
    private String country;

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

    public Integer getLang() {
        return lang;
    }

    public void setLang(Integer lang) {
        this.lang = lang;
    }

    public Double getTz() {
        return tz;
    }

    public void setTz(Double tz) {
        this.tz = tz;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
                ", lang=" + lang +
                ", tz=" + tz +
                ", country='" + country + '\'' +
                ", avatar=" + avatar +
                ", thumbRect=" + thumbRect +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseUserPageData that = (BaseUserPageData) o;

        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (db != null ? !db.equals(that.db) : that.db != null) return false;
        if (mb != null ? !mb.equals(that.mb) : that.mb != null) return false;
        if (yb != null ? !yb.equals(that.yb) : that.yb != null) return false;
        if (hometown != null ? !hometown.equals(that.hometown) : that.hometown != null) return false;
        if (sex != null ? !sex.equals(that.sex) : that.sex != null) return false;
        if (favoriteGames != null ? !favoriteGames.equals(that.favoriteGames) : that.favoriteGames != null)
            return false;
        if (favoriteMusic != null ? !favoriteMusic.equals(that.favoriteMusic) : that.favoriteMusic != null)
            return false;
        if (aboutMe != null ? !aboutMe.equals(that.aboutMe) : that.aboutMe != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (lang != null ? !lang.equals(that.lang) : that.lang != null) return false;
        if (tz != null ? !tz.equals(that.tz) : that.tz != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (avatar != null ? !avatar.equals(that.avatar) : that.avatar != null) return false;
        return thumbRect != null ? thumbRect.equals(that.thumbRect) : that.thumbRect == null;

    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (db != null ? db.hashCode() : 0);
        result = 31 * result + (mb != null ? mb.hashCode() : 0);
        result = 31 * result + (yb != null ? yb.hashCode() : 0);
        result = 31 * result + (hometown != null ? hometown.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (favoriteGames != null ? favoriteGames.hashCode() : 0);
        result = 31 * result + (favoriteMusic != null ? favoriteMusic.hashCode() : 0);
        result = 31 * result + (aboutMe != null ? aboutMe.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        result = 31 * result + (tz != null ? tz.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        result = 31 * result + (thumbRect != null ? thumbRect.hashCode() : 0);
        return result;
    }
}
