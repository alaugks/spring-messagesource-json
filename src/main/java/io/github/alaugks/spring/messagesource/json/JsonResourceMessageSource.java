// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json;

import io.github.alaugks.spring.messagesource.base.AbstractBaseMessageSourceBuilder;
import io.github.alaugks.spring.messagesource.base.BaseMessageSourceBuilder;
import io.github.alaugks.spring.messagesource.base.resources.ResourceLoaderBuilder;
import io.github.alaugks.spring.messagesource.base.resources.TargetLocaleResolverInterface;
import java.util.List;
import java.util.Locale;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

/**
 * Entry point for building a Spring {@code MessageSource} backed by JSON
 * translation files.
 * <p>Use {@link #builder(Locale, String)} to obtain a {@link Builder}
 * and then call {@link Builder#build()} to assemble the resulting
 * {@link BaseMessageSourceBuilder}.
 */
public final class JsonResourceMessageSource {

	/**
	 * Utility class — not intended to be instantiated.
	 *
	 * @throws UnsupportedOperationException always.
	 */
	private JsonResourceMessageSource() {
		throw new UnsupportedOperationException("Utility class");
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
	 * {@link BaseMessageSourceBuilder}.
	 */
	public static final class Builder extends AbstractBaseMessageSourceBuilder<Builder> {

		private final List<String> locationPattern;

		private @Nullable TargetLocaleResolverInterface targetLocaleResolver;

		private @Nullable JsonCatalogInterface jsonCatalog;

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
		 * Assigns a custom implementation of {@link TargetLocaleResolverInterface} to resolve the
		 * target locale of JSON files.
		 *
		 * @param targetLocaleResolver an implementation of {@link TargetLocaleResolverInterface}
		 *                               used to resolve the target locale of JSON files; must
		 *                               not be null.
		 * @return this builder instance for method chaining.
		 */
		public Builder targetLocaleResolver(TargetLocaleResolverInterface targetLocaleResolver) {
			Assert.notNull(targetLocaleResolver, "targetLocaleResolver must not be null");

			this.targetLocaleResolver = targetLocaleResolver;
			return this;
		}

		/**
		 * Assigns a custom implementation of {@link JsonCatalogInterface} to resolve the
		 * translation units of JSON files.
		 *
		 * @param jsonCatalogInterface an implementation of {@link JsonCatalogInterface}
		 *                               used to resolve the translation units of JSON files; must
		 *                               not be null.
		 * @return this builder instance for method chaining.
		 */
		public Builder jsonCatalog(JsonCatalogInterface jsonCatalogInterface) {
			Assert.notNull(jsonCatalogInterface, "targetLocaleResolver must not be null");

			this.jsonCatalog = jsonCatalogInterface;
			return this;
		}

		/**
		 * Assembles the configured {@link BaseMessageSourceBuilder} backed
		 * by a {@link JsonCatalog} loaded from the configured location
		 * pattern.
		 *
		 * @return the configured message source builder.
		 */
		public BaseMessageSourceBuilder build() {
			ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder.builder(
				this.getDefaultLocale(),
				locationPattern
			).targetLocaleResolver(this.targetLocaleResolver)
				.fileExtensions(List.of("json"))
				.build();

			if (jsonCatalog == null) {
				this.jsonCatalog = new JsonCatalog();
			}

			return BaseMessageSourceBuilder
				.builder(this.getDefaultLocale(), this.jsonCatalog.getTransUnits(resourcesLoader.getTranslationFiles()))
				.parentMessageSource(this.getParentMessageSource())
				.useICU4j(this.isICU4jEnabled())
				.build();
		}
	}
}
