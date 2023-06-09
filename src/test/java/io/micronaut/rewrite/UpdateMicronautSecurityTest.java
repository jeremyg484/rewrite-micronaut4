package io.micronaut.rewrite;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.java.Assertions.srcMainResources;
import static org.openrewrite.properties.Assertions.properties;
import static org.openrewrite.yaml.Assertions.yaml;

public class UpdateMicronautSecurityTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipeFromResource("/META-INF/rewrite/micronaut3-to-4.yml", "io.micronaut.rewrite.UpdateMicronautSecurity");
    }

    @Language("properties")
    private final String initialSecurityProps = """
            micronaut.security.token.jwt.generator.access-token.expiration=1d
            micronaut.security.token.jwt.cookie.enabled=true
            micronaut.security.token.jwt.cookie.cookie-max-age=1d
            micronaut.security.token.jwt.cookie.cookie-path=foo
            micronaut.security.token.jwt.cookie.cookie-domain=bar.com
            micronaut.security.token.jwt.cookie.cookie-same-site=true
            micronaut.security.token.jwt.bearer.enabled=true
        """;

    @Language("properties")
    private final String expectedSecurityProps = """
            micronaut.security.token.generator.access-token.expiration=1d
            micronaut.security.token.cookie.enabled=true
            micronaut.security.token.cookie.cookie-max-age=1d
            micronaut.security.token.cookie.cookie-path=foo
            micronaut.security.token.cookie.cookie-domain=bar.com
            micronaut.security.token.cookie.cookie-same-site=true
            micronaut.security.token.bearer.enabled=true
        """;

    @Language("yml")
    private final String initialSecurityYaml = """
            micronaut:
                security:
                    token:
                        jwt:
                            generator:
                                access-token:
                                    expiration: 1d
                            cookie: 
                                enabled: true
                                cookie-max-age: 1d
                                cookie-path: foo
                                cookie-domain: bar.com
                                cookie-same-site: true
                            bearer:
                                enabled: true
        """;

    @Language("yml")
    private final String expectedSecurityYaml = """
            micronaut:
                security:
                    token:
                        generator:
                            access-token:
                                expiration: 1d
                        cookie: 
                            enabled: true
                            cookie-max-age: 1d
                            cookie-path: foo
                            cookie-domain: bar.com
                            cookie-same-site: true
                        bearer:
                            enabled: true
        """;

    @Language("yml")
    private final String initialSecurityYamlPartial = """
            micronaut:
                security:
                    token:
                        jwt:
                            generator:
                                access-token:
                                    expiration: 1d
                            cookie: 
                                enabled: false
                            bearer:
                                enabled: true
        """;

    @Language("yml")
    private final String expectedSecurityYamlPartial = """
            micronaut:
                security:
                    token:
                        generator:
                            access-token:
                                expiration: 1d
                        cookie: 
                            enabled: false
                        bearer:
                            enabled: true
        """;

    @Language("yml")
    private final String noJwtConfig = """
            micronaut:
                application:
                    name: foo
        """;



    @Test
    void updatePropertyConfig() {
        rewriteRun(mavenProject("project", srcMainResources(properties(initialSecurityProps, expectedSecurityProps, s -> s.path("application.properties")))));
    }

    @Test
    void updateYamlConfig() {
        rewriteRun(mavenProject("project", srcMainResources(yaml(initialSecurityYaml, expectedSecurityYaml, s -> s.path("application.yml")))));
    }

    @Test
    void updatePartialYamlConfig() {
        rewriteRun(spec -> spec.expectedCyclesThatMakeChanges(2), mavenProject("project", srcMainResources(yaml(initialSecurityYamlPartial, expectedSecurityYamlPartial, s -> s.path("application.yml")))));
    }

    @Test
    void noJwtConfig() {
        rewriteRun(mavenProject("project", srcMainResources(yaml(noJwtConfig, s -> s.path("application.yml")))));
    }


}
