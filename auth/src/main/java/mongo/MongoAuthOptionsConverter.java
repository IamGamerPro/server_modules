package mongo;

import io.vertx.core.json.JsonObject;

public class MongoAuthOptionsConverter {

    public static void fromJson(JsonObject json, MongoAuthOptions obj) {
        if (json.getValue("collectionName") instanceof String) {
            obj.setCollectionName((String) json.getValue("collectionName"));
        }
        if (json.getValue("config") instanceof JsonObject) {
            obj.setConfig(((JsonObject) json.getValue("config")).copy());
        }
        if (json.getValue("datasourceName") instanceof String) {
            obj.setDatasourceName((String) json.getValue("datasourceName"));
        }
        if (json.getValue("passwordField") instanceof String) {
            obj.setPasswordField((String) json.getValue("passwordField"));
        }
        if (json.getValue("permissionField") instanceof String) {
            obj.setPermissionField((String) json.getValue("permissionField"));
        }
        if (json.getValue("roleField") instanceof String) {
            obj.setRoleField((String) json.getValue("roleField"));
        }
        if (json.getValue("saltField") instanceof String) {
            obj.setSaltField((String) json.getValue("saltField"));
        }
        if (json.getValue("saltStyle") instanceof String) {
            obj.setSaltStyle(HashSaltStyle.valueOf((String) json.getValue("saltStyle")));
        }
        if (json.getValue("shared") instanceof Boolean) {
            obj.setShared((Boolean) json.getValue("shared"));
        }
        if (json.getValue("usernameCredentialField") instanceof String) {
            obj.setUsernameCredentialField((String) json.getValue("usernameCredentialField"));
        }
        if (json.getValue("usernameField") instanceof String) {
            obj.setUsernameField((String) json.getValue("usernameField"));
        }
    }

    public static void toJson(MongoAuthOptions obj, JsonObject json) {
        if (obj.getCollectionName() != null) {
            json.put("collectionName", obj.getCollectionName());
        }
        if (obj.getConfig() != null) {
            json.put("config", obj.getConfig());
        }
        if (obj.getDatasourceName() != null) {
            json.put("datasourceName", obj.getDatasourceName());
        }
        if (obj.getPasswordField() != null) {
            json.put("passwordField", obj.getPasswordField());
        }
        if (obj.getPermissionField() != null) {
            json.put("permissionField", obj.getPermissionField());
        }
        if (obj.getRoleField() != null) {
            json.put("roleField", obj.getRoleField());
        }
        if (obj.getSaltField() != null) {
            json.put("saltField", obj.getSaltField());
        }
        if (obj.getSaltStyle() != null) {
            json.put("saltStyle", obj.getSaltStyle().name());
        }
        json.put("shared", obj.getShared());
        if (obj.getUsernameCredentialField() != null) {
            json.put("usernameCredentialField", obj.getUsernameCredentialField());
        }
        if (obj.getUsernameField() != null) {
            json.put("usernameField", obj.getUsernameField());
        }
    }
}