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
import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import io.github.alaugks.spring.messagesource.catalog.resources.ResourcesLoader;
import java.util.List;
import java.util.Locale;

public class JsonResourceMessageSource {

	/**
	 * Utility class — not intended to be instantiated.
	 *
	 * @throws IllegalStateException always.
	 */
	private JsonResourceMessageSource() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Creates a new {@link Builder} for assembling a JSON-backed Spring
	 * {@code MessageSource}.
	 *
	 * @param defaultLocale   the locale to fall back to when a translation is
	 *                        not available in the requested locale.
	 * @param locationPattern Spring resource pattern(s) describing where the
	 *                        JSON files are located.
	 * @return a new builder pre-configured with the given defaults.
	 */
	public static Builder builder(Locale defaultLocale, LocationPattern locationPattern) {
		return new Builder(defaultLocale, locationPattern);
	}

	public static final class Builder {

		private final Locale defaultLocale;

		private final LocationPattern locationPattern;

		private String defaultDomain = CatalogMessageSourceBuilder.DEFAULT_DOMAIN;

		/**
		 * Creates a new builder with the given default locale and JSON file
		 * location pattern.
		 *
		 * @param defaultLocale   the locale to fall back to when a translation
		 *                        is not available in the requested locale.
		 * @param locationPattern Spring resource pattern(s) describing where
		 *                        the JSON files are located.
		 */
		public Builder(Locale defaultLocale, LocationPattern locationPattern) {
			this.defaultLocale = defaultLocale;
			this.locationPattern = locationPattern;
		}

		/**
		 * Sets the default domain on the underlying
		 * {@link CatalogMessageSourceBuilder}. Codes whose domain matches this
		 * value are accessible by their bare code; codes from other domains
		 * must be looked up as {@code <domain>.<code>}.
		 * <p>The domain itself is always parsed from the JSON file name; this
		 * setting only controls which domain is treated as "default" when
		 * resolving codes.
		 *
		 * @param defaultDomain the new default domain.
		 * @return this builder for chaining.
		 */
		public Builder defaultDomain(String defaultDomain) {
			this.defaultDomain = defaultDomain;
			return this;
		}

		/**
		 * Assembles the configured {@link CatalogMessageSourceBuilder} backed
		 * by a {@link JsonCatalog} loaded from the configured location
		 * pattern.
		 *
		 * @return the configured message source builder.
		 */
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