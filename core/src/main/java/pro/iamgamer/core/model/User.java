package pro.iamgamer.core.model;

import pro.iamgamer.core.security.PasswordUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private LocalDateTime modDate;
    /**Числовой индекс версии коллекции (после каждого редактирования увеличивается на 1)*/
    private AtomicInteger modVersion;
    /**Статус удаления аккаунта*/
    private AtomicBoolean delete = new AtomicBoolean(false);
    /**Дата удаления аккаунта*/
    private Optional<LocalDateTime> deleteDate;

    @NotNull
    @Size(min = 2, max = 50)
    private String loginName;
    private byte[] salt;
    private byte[] password;

    public User(String loginName, String password) {
        this.loginName = loginName;
        this.salt = PasswordUtils.randomSalt();
        this.password = PasswordUtils.hash(password.toCharArray(), this.salt);
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
        sb.append(", loginName='").append(loginName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getLoginName() {
        return loginName;
    }

    public byte[] getPassword(){
        return this.password;
    }
    public byte[] getSalt() {
        return salt;
    }
}
