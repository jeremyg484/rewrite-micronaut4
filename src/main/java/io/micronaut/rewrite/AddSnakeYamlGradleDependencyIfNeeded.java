package io.micronaut.rewrite;

import org.openrewrite.*;
import org.openrewrite.gradle.AddDependency;
import org.openrewrite.internal.lang.Nullable;

import java.util.List;

public class AddSnakeYamlGradleDependencyIfNeeded extends Recipe {

	private final Recipe addSnakeYamlRecipe = new AddDependency("org.yaml", "snakeyaml", null, null, "runtimeOnly", "io.micronaut.runtime.Micronaut",
			null, null, null);

	@Override
	public String getDisplayName() {
		return "Add `snakeyaml` dependency";
	}

	@Override
	public String getDescription() {
		return "Add `org.yaml:snakeyaml` Gradle dependency if using yaml config.";
	}

	@Override
	@Nullable
	protected TreeVisitor<?, ExecutionContext> getApplicableTest() {
		return Applicability.or(new HasSourcePath<>("**/application.yml"),
				new HasSourcePath<>("**/application.yaml"),
				new HasSourcePath<>("**/application-*.yml"),
				new HasSourcePath<>("**/application-*.yaml"),
				new HasSourcePath<>("**/bootstrap.yml"),
				new HasSourcePath<>("**/bootstrap.yaml"),
				new HasSourcePath<>("**/bootstrap-*.yml"),
				new HasSourcePath<>("**/bootstrap-*.yaml"));
	}

	@Override
	protected List<SourceFile> visit(List<SourceFile> before, ExecutionContext ctx) {
		if (getRecipeList().isEmpty()) {
			doNext(addSnakeYamlRecipe);
		}
		return before;
	}
}
