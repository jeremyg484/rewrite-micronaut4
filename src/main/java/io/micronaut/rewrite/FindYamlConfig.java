package io.micronaut.rewrite;

import org.openrewrite.*;

public class FindYamlConfig extends Recipe {

    @Override
    public String getDisplayName() {
        return "Find Micronaut yaml config";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return Applicability.or(new HasSourcePath<>("**/application.yml"),
                new HasSourcePath<>("**/application.yaml"),
                new HasSourcePath<>("**/application-*.yml"),
                new HasSourcePath<>("**/application-*.yaml"),
                new HasSourcePath<>("**/bootstrap.yml"),
                new HasSourcePath<>("**/bootstrap.yaml"),
                new HasSourcePath<>("**/bootstrap-*.yml"),
                new HasSourcePath<>("**/bootstrap-*.yaml"));
    }
}
