package pro.iamgamer.routing;

/**
 * Created by Sergey Kobets on 04.03.2016.
 */
public interface RouteOrchestratorRule {

    default String privateUrlPatch() {
        return "/private";
    }

    default String loginUrl() {
        return "/login";
    }
}
