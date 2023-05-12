/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.micronaut.rewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.properties.Assertions.properties;

public class UpgradeMicronautGradlePropertiesVersionTest implements RewriteTest {

    @Test
    void upgradeToMicronaut4() {
		rewriteRun(
			spec -> spec.recipe(new UpgradeMicronautGradlePropertiesVersion("4.0.0-M2")),
			properties("""
					micronautVersion=3.9.0	
				""",
				"""
    				micronautVersion=4.0.0-M2
				""",
				s -> s.path("gradle.properties")
			)
		);
    }

	@Test
	void upgradeOlderVersion() {
		rewriteRun(
				spec -> spec.recipe(new UpgradeMicronautGradlePropertiesVersion("2.x")),
				properties("""
					micronautVersion=2.0.3	
				""",
						"""
							micronautVersion=2.5.13
						""",
						s -> s.path("gradle.properties")
				)
		);
	}

}
