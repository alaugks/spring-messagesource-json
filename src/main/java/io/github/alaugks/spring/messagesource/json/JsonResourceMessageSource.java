/*
 * Copyright 2023-2025 André Laugks <alaugks@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alaugks.spring.messagesource.json;

import io.github.alaugks.spring.messagesource.catalog.CatalogMessageSourceBuilder;
import io.github.alaugks.spring.messagesource.catalog.catalog.Catalog;
import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import io.github.alaugks.spring.messagesource.catalog.resources.ResourcesLoader;
import java.util.List;
import java.util.Locale;

public class JsonResourceMessageSource {

	private JsonResourceMessageSource() {
		throw new IllegalStateException(JsonResourceMessageSource.class.toString());
	}

	public static Builder builder(Locale defaultLocale, LocationPattern locationPattern) {
		return new Builder(defaultLocale, locationPattern);
	}

	public static final class Builder {

		private final Locale defaultLocale;

		private final LocationPattern locationPattern;

		private String defaultDomain = Catalog.DEFAULT_DOMAIN;

		public Builder(Locale defaultLocale, LocationPattern locationPattern) {
			this.defaultLocale = defaultLocale;
			this.locationPattern = locationPattern;
		}

		public Builder defaultDomain(String defaultDomain) {
			this.defaultDomain = defaultDomain;
			return this;
		}

		public CatalogMessageSourceBuilder build() {
			ResourcesLoader resourcesLoader = new ResourcesLoader(
				this.defaultLocale,
				locationPattern,
				List.of("json")
			);

			return CatalogMessageSourceBuilder
				.builder(new JsonCatalog(resourcesLoader.getTranslationFiles()), this.defaultLocale)
				.defaultDomain(this.defaultDomain)
				.build();
		}
	}
}
