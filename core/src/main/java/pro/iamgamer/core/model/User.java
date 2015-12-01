package pro.iamgamer.core.model;

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
    private final Long id = null;
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
}
