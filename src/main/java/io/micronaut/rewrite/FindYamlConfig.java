package io.micronaut.rewrite;

import org.openrewrite.*;

public class FindYamlConfig extends Recipe {

    @Override
    public String getDisplayName() {
        return "Find Micronaut yaml config";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new HasSourcePath<>("**/{application,application-*,bootstrap,bootstrap-*}.{yml,yaml}");
    }
}
