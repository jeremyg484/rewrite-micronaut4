package io.micronaut.rewrite;

import org.intellij.lang.annotations.Language;
import org.openrewrite.Recipe;
import org.openrewrite.yaml.CopyValue;
import org.openrewrite.yaml.DeleteKey;
import org.openrewrite.yaml.MergeYaml;
import org.openrewrite.yaml.cleanup.RemoveUnused;

public class UpdateSecurityYamlIfNeeded extends Recipe {

    @Language("yml")
    private final String newYamlKeysSnippet = """
            generator:
                access-token:
                    expiration:
            cookie:
                enabled:
                cookie-max-age:
                cookie-path:
                cookie-domain:
                cookie-same-site:
            bearer:
                enabled:
        """;

    private final String TOKEN_PATH = "$.micronaut.security.token";

    @Override
    public String getDisplayName() {
        return "Update relocated Micronaut Security config yaml keys";
    }

    @Override
    public String getDescription() {
        return "This recipe will update relocated security config keys in Micronaut configuration yaml files.";
    }

    public UpdateSecurityYamlIfNeeded() {
        addSingleSourceApplicableTest(new FindYamlConfig());

        doNext(new MergeYaml("$.micronaut.security.token", newYamlKeysSnippet, Boolean.TRUE, null, null));
        doNext(new CopyValue(TOKEN_PATH + ".jwt.generator.access-token.expiration", TOKEN_PATH + ".generator.access-token.expiration", null));
        doNext(new CopyValue(TOKEN_PATH + ".jwt.cookie.enabled", TOKEN_PATH + ".cookie.enabled", null));
        doNext(new CopyValue(TOKEN_PATH + ".jwt.cookie.cookie-max-age", TOKEN_PATH + ".cookie.cookie-max-age", null));
        doNext(new CopyValue(TOKEN_PATH + ".jwt.cookie.cookie-path", TOKEN_PATH + ".cookie.cookie-path", null));
        doNext(new CopyValue(TOKEN_PATH + ".jwt.cookie.cookie-domain", TOKEN_PATH + ".cookie.cookie-domain", null));
        doNext(new CopyValue(TOKEN_PATH + ".jwt.cookie.cookie-same-site", TOKEN_PATH + ".cookie.cookie-same-site", null));
        doNext(new CopyValue(TOKEN_PATH + ".jwt.bearer.enabled", TOKEN_PATH + ".bearer.enabled", null));
        doNext(new DeleteKey(TOKEN_PATH + ".jwt.generator", null));
        doNext(new DeleteKey(TOKEN_PATH + ".jwt.cookie", null));
        doNext(new DeleteKey(TOKEN_PATH + ".jwt.bearer", null));
        doNext(new RemoveUnused());
    }
}
