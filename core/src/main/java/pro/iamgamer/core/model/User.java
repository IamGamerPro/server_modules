package pro.iamgamer.core.model;

import com.orientechnologies.orient.core.id.ORID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Optional;


/**
 * Created by Sergey Kobets on 30.08.2015.
 * Сушность пользователя
 */
public final class User implements Entity {
    /**Системный идентификатор сущности */
    private ORID id;
    /**Версия схема */
    private final Integer shemaVersion = null;
    /**Дата посдедней модификации коллекции*/
    private Date modDate;
    /**Числовой индекс версии коллекции (после каждого редактирования увеличивается на 1)*/
    private Integer modVersion;
    /**Статус удаления аккаунта*/
    private Boolean delete = Boolean.FALSE;
    /**Дата удаления аккаунта*/
    private Optional<Date> deleteDate;
    private Boolean ban = Boolean.FALSE;
    private Date banExpiredDate;



    @NotNull
    @Size(min = 2, max = 50)
    private String login;

    public User(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", shemaVersion=").append(shemaVersion);
        sb.append(", modDate=").append(modDate);
        sb.append(", modVersion=").append(modVersion);
        sb.append(", delete=").append(delete);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", login='").append(login).append('\'');
        sb.append('}');
        return sb.toString();
    }



    public String getLogin() {
        return login;
    }

    public ORID getId() {
        return id;
    }

    public void setId(ORID id) {
        this.id = id;
    }

    public Integer getShemaVersion() {
        return shemaVersion;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    public Integer getModVersion() {
        return modVersion;
    }

    public void setModVersion(Integer modVersion) {
        this.modVersion = modVersion;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Optional<Date> getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Optional<Date> deleteDate) {
        this.deleteDate = deleteDate;
    }

    public Boolean getBan() {
        return ban;
    }

    public void setBan(Boolean ban) {
        this.ban = ban;
    }

    public Date getBanExpiredDate() {
        return banExpiredDate;
    }

    public void setBanExpiredDate(Date banExpiredDate) {
        this.banExpiredDate = banExpiredDate;
    }
}
