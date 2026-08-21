// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json;

import io.github.alaugks.spring.messagesource.catalog.AbstractCatalogMessageSourceBuilder;
import io.github.alaugks.spring.messagesource.catalog.CatalogMessageSourceBuilder;
import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import io.github.alaugks.spring.messagesource.catalog.resources.ResourceLoaderBuilder;
import java.util.List;
import java.util.Locale;

/**
 * Entry point for building a Spring {@code MessageSource} backed by JSON
 * translation files.
 * <p>Use {@link #builder(Locale, String)} to obtain a {@link Builder}
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
	 * @deprecated since 1.0.0, use {@link #builder(Locale, String)} or
	 *             {@link #builder(Locale, List)} instead.
	 */
	@Deprecated(since = "1.0.0")
	public static Builder builder(Locale defaultLocale, LocationPattern locationPattern) {
		return builder(defaultLocale, locationPattern.getLocationPatterns());
	}

	/**
	 * Creates a new {@link Builder} for assembling a JSON-backed Spring
	 * {@code MessageSource}.
	 *
	 * @param defaultLocale   the locale to fall back to when a translation is
	 *                        not available in the requested locale.
	 * @param locationPattern a Spring resource pattern describing where the
	 *                        JSON files are located.
	 * @return a new builder pre-configured with the given defaults.
	 */
	public static Builder builder(Locale defaultLocale, String locationPattern) {
		return builder(defaultLocale, List.of(locationPattern));
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
	public static Builder builder(Locale defaultLocale, List<String> locationPattern) {
		return new Builder(defaultLocale, locationPattern);
	}

	/**
	 * Fluent builder for configuring and assembling a JSON-backed
	 * {@link CatalogMessageSourceBuilder}.
	 */
	public static final class Builder extends AbstractCatalogMessageSourceBuilder<Builder> {

		private final List<String> locationPattern;

		/**
		 * Creates a new builder with the given default locale and JSON file
		 * location pattern.
		 *
		 * @param defaultLocale   the locale to fall back to when a translation
		 *                        is not available in the requested locale.
		 * @param locationPattern Spring resource pattern(s) describing where
		 *                        the JSON files are located.
		 */
		public Builder(Locale defaultLocale, List<String> locationPattern) {
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
			ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder.builder(
				this.getDefaultLocale(),
				locationPattern
			).fileExtensions(List.of("json")).build();

			return CatalogMessageSourceBuilder
				.builder(this.getDefaultLocale(), new JsonCatalog(resourcesLoader.getTranslationFiles()))
				.defaultDomain(this.getDefaultDomain())
				.parentMessageSource(this.getParentMessageSource())
				.useICU4j(this.isICU4jEnabled())
				.domainDivider(this.getDomainDivider())
				.build();
		}
	}
}
