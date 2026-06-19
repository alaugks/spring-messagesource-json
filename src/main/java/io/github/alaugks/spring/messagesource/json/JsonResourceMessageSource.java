// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json;

import io.github.alaugks.spring.messagesource.catalog.AbstractCatalogMessageSourceBuilder;
import io.github.alaugks.spring.messagesource.catalog.CatalogMessageSourceBuilder;
import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import io.github.alaugks.spring.messagesource.catalog.resources.ResourcesLoader;
import java.util.List;
import java.util.Locale;

/**
 * Entry point for building a Spring {@code MessageSource} backed by JSON
 * translation files.
 * <p>Use {@link #builder(Locale, LocationPattern)} to obtain a {@link Builder}
 * and then call {@link Builder#build()} to assemble the resulting
 * {@link CatalogMessageSourceBuilder}.
 */
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

	/**
	 * Fluent builder for configuring and assembling a JSON-backed
	 * {@link CatalogMessageSourceBuilder}.
	 */
	public static final class Builder extends AbstractCatalogMessageSourceBuilder<Builder> {

		private final LocationPattern locationPattern;

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
			super(defaultLocale);
			this.locationPattern = locationPattern;
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
				this.getDefaultLocale(),
				locationPattern,
				List.of("json")
			);

			return CatalogMessageSourceBuilder
				.builder(new JsonCatalog(resourcesLoader.getTranslationFiles()), this.getDefaultLocale())
				.defaultDomain(this.getDefaultDomain())
				.parentMessageSource(this.getParentMessageSource())
				.setUseICU4j(this.isICU4jEnabled())
				.build();
		}
	}
}
