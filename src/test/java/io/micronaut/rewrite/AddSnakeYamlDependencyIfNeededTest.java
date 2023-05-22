package io.micronaut.rewrite;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.gradle.Assertions.buildGradle;
import static org.openrewrite.java.Assertions.*;
import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.properties.Assertions.properties;
import static org.openrewrite.yaml.Assertions.yaml;

public class AddSnakeYamlDependencyIfNeededTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.parser(JavaParser.fromJavaVersion().classpath("micronaut-context"));
        spec.recipeFromResource("/META-INF/rewrite/micronaut3-to-4.yml", "io.micronaut.rewrite.AddSnakeYamlDependencyIfNeeded");
    }

    @Language("java")
    private final String micronautApplication = """
            import io.micronaut.runtime.Micronaut;
            
            public class Application {
            
                public static void main(String[] args) {
                    Micronaut.run(Application.class, args);
                }
            }
      """;

    @Language("yml")
    private final String micronautConfig = """
        micronaut:
            application:
                name: testApp
        """;

    @Language("properties")
    private final String micronautPropertiesConfig = """
            micronaut.application.name=testApp
        """;

    @Language("groovy")
    private final String buildGradleWithDependency = """
            dependencies {
                runtimeOnly "org.yaml:snakeyaml"
            }
        """;

    @Language("xml")
    private final String initialPom = """
            <project>
                <groupId>com.mycompany.app</groupId>
                <artifactId>my-app</artifactId>
                <version>1</version>
                <parent>
                    <groupId>io.micronaut.platform</groupId>
                    <artifactId>micronaut-parent</artifactId>
                    <version>4.0.0-M2</version>
                </parent>
            </project>
        """;

    @Language("xml")
    private final String pomWithDependency = """
            <project>
                <groupId>com.mycompany.app</groupId>
                <artifactId>my-app</artifactId>
                <version>1</version>
                <parent>
                    <groupId>io.micronaut.platform</groupId>
                    <artifactId>micronaut-parent</artifactId>
                    <version>4.0.0-M2</version>
                </parent>
                <dependencies>
                    <dependency>
                        <groupId>org.yaml</groupId>
                        <artifactId>snakeyaml</artifactId>
                        <scope>runtime</scope>
                    </dependency>
                </dependencies>
            </project>
        """;

    @Test
    void testAddGradleDependencyForApplicationYml() {
        rewriteRun(mavenProject("project",
                        srcMainJava(java(micronautApplication)),
                        srcMainResources(yaml(micronautConfig, s -> s.path("application.yml"))),
                buildGradle("", buildGradleWithDependency)));
    }

    @Test
    void testAddMavenDependencyForApplicationYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("application.yml"))),
                pomXml(initialPom, pomWithDependency)));
    }

    @Test
    void testAddGradleDependencyForApplicationYaml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("application.yaml"))),
                buildGradle("", buildGradleWithDependency)));
    }

    @Test
    void testAddMavenDependencyForApplicationYaml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("application.yaml"))),
                pomXml(initialPom, pomWithDependency)));
    }


    @Test
    void testNoGradleDependencyForMissingApplicationYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("foo.yml"))),
                buildGradle("")));
    }

    @Test
    void testNoMavenDependencyForMissingApplicationYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("foo.yml"))),
                pomXml(initialPom)));
    }

    @Test
    void testNoGradleDependencyForApplicationProperties() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(properties(micronautPropertiesConfig, s -> s.path("application.properties"))),
                buildGradle("")));
    }

    @Test
    void testNoMavenDependencyForApplicationProperties() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(properties(micronautPropertiesConfig, s -> s.path("application.properties"))),
                pomXml(initialPom)));
    }

    @Test
    void testExistingGradleDependencyUnchanged() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("application.yml"))),
                buildGradle(buildGradleWithDependency)));
    }

    @Test
    void testExistingMavenDependencyUnchanged() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("application.yml"))),
                pomXml(pomWithDependency)));
    }

    @Test
    void testAddGradleDependencyForEnvironmentYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("application-foo.yml"))),
                buildGradle("", buildGradleWithDependency)));
    }

    @Test
    void testAddMavenDependencyForEnvironmentYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("application-foo.yml"))),
                pomXml(initialPom, pomWithDependency)));
    }

    @Test
    void testAddGradleDependencyForTestYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcTestResources(yaml(micronautConfig, s -> s.path("application-test.yml"))),
                buildGradle("", buildGradleWithDependency)));
    }

    @Test
    void testAddMavenDependencyForTestYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcTestResources(yaml(micronautConfig, s -> s.path("application-test.yml"))),
                pomXml(initialPom, pomWithDependency)));
    }

    @Test
    void testAddGradleDependencyForBootstrapYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("bootstrap.yml"))),
                buildGradle("", buildGradleWithDependency)));
    }

    @Test
    void testAddMavenDependencyForBootstrapYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("bootstrap.yml"))),
                pomXml(initialPom, pomWithDependency)));
    }

    @Test
    void testAddGradleDependencyForBootstrapEnvironmentYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("bootstrap-foo.yml"))),
                buildGradle("", buildGradleWithDependency)));
    }

    @Test
    void testAddMavenDependencyForBootstrapEnvironmentYml() {
        rewriteRun(mavenProject("project",
                srcMainJava(java(micronautApplication)),
                srcMainResources(yaml(micronautConfig, s -> s.path("bootstrap-foo.yml"))),
                pomXml(initialPom, pomWithDependency)));
    }
}
