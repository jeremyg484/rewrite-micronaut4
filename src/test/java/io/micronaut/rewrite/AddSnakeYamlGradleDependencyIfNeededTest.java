package io.micronaut.rewrite;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import static org.openrewrite.yaml.Assertions.yaml;
import static org.openrewrite.gradle.Assertions.buildGradle;
import static org.openrewrite.java.Assertions.*;
public class AddSnakeYamlGradleDependencyIfNeededTest implements RewriteTest {

	@Override
	public void defaults(RecipeSpec spec) {
		spec.parser(JavaParser.fromJavaVersion().classpath("micronaut-context"));
		spec.recipe(new AddSnakeYamlGradleDependencyIfNeeded());
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

	@Test
	void testAddDependencyForApplicationYml() {
		rewriteRun(mavenProject("project",
				srcMainJava(java(micronautApplication)),
				srcMainResources(yaml("""
					micronaut:
					   application:
					     name: testApp				
				""", s -> s.path("application.yml"))),
				buildGradle("",
			"""
    				dependencies {
    				    runtimeOnly "org.yaml:snakeyaml"
    				}
				"""
			)
		));
	}

	@Test
	void testAddDependencyForApplicationYaml() {
		rewriteRun(mavenProject("project",
				srcMainJava(java(micronautApplication)),
				srcMainResources(yaml("""
					micronaut:
					   application:
					     name: testApp				
				""", s -> s.path("application.yaml"))),
				buildGradle("",
						"""
								dependencies {
								    runtimeOnly "org.yaml:snakeyaml"
								}
							"""
				)
		));
	}

	@Test
	void testNoDependencyForMissingApplicationYml() {
		rewriteRun(mavenProject("project",
				srcMainJava(java(micronautApplication)),
				srcMainResources(yaml("""
					micronaut:
					   application:
					     name: testApp				
				""", s -> s.path("foo.yml"))),
				buildGradle("")
		));
	}

	@Test
	void testExistingDependencyUnchanged() {
		rewriteRun(mavenProject("project",
				srcMainJava(java(micronautApplication)),
				srcMainResources(yaml("""
					micronaut:
					   application:
					     name: testApp				
				""", s -> s.path("application.yml"))),
				buildGradle("""
								dependencies {
									runtimeOnly("org.yaml:snakeyaml")
								}
							"""
				)
		));
	}

	@Test
	void testAddDependencyForEnvironmentYml() {
		rewriteRun(mavenProject("project",
				srcMainJava(java(micronautApplication)),
				srcMainResources(yaml("""
					micronaut:
					   application:
					     name: testApp				
				""", s -> s.path("application-foo.yml"))),
				buildGradle("",
						"""
								dependencies {
								    runtimeOnly "org.yaml:snakeyaml"
								}
							"""
				)
		));
	}

	@Test
	void testAddDependencyForTestYml() {
		rewriteRun(mavenProject("project",
				srcMainJava(java(micronautApplication)),
				srcTestResources(yaml("""
					micronaut:
					   application:
					     name: testApp				
				""", s -> s.path("application-test.yml"))),
				buildGradle("",
						"""
								dependencies {
								    runtimeOnly "org.yaml:snakeyaml"
								}
							"""
				)
		));
	}

	@Test
	void testAddDependencyForBootstrapYml() {
		rewriteRun(mavenProject("project",
				srcMainJava(java(micronautApplication)),
				srcMainResources(yaml("""
					micronaut:
					   application:
					     name: testApp				
				""", s -> s.path("bootstrap.yml"))),
				buildGradle("",
						"""
								dependencies {
								    runtimeOnly "org.yaml:snakeyaml"
								}
							"""
				)
		));
	}

	@Test
	void testAddDependencyForBootstrapEnvironmentYml() {
		rewriteRun(mavenProject("project",
				srcMainJava(java(micronautApplication)),
				srcMainResources(yaml("""
					micronaut:
					   application:
					     name: testApp				
				""", s -> s.path("bootstrap-foo.yml"))),
				buildGradle("",
						"""
								dependencies {
								    runtimeOnly "org.yaml:snakeyaml"
								}
							"""
				)
		));
	}
}
