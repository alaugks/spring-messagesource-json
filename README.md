# JSON MessageSource for Spring

This package provides a [MessageSource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/MessageSource.html) for using translations from JSON files.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=alaugks_spring-messagesource-json&metric=alert_status)](https://sonarcloud.io/summary/overall?id=alaugks_spring-messagesource-json)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.alaugks/spring-messagesource-json.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.alaugks/spring-messagesource-json/2.1.0)

## Table of Contents

- [Dependency](#dependency)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [MessageSource Configuration](#messagesource-configuration)
  - [Example](#example)
- [JSON Files](#json-files)
  - [Structure of the Translation Filename](#structure-of-the-translation-filename)
  - [Example with JSON Files](#example-with-json-files)
  - [Target value](#target-value)
  - [Custom Target Locale Resolver](#custom-target-locale-resolver)
  - [Custom JSON Catalog](#custom-json-catalog)
- [Message Formatting](#message-formatting)
  - [Default (java.text.MessageFormat)](#default-javatextmessageformat)
  - [ICU4J (com.ibm.icu.text.MessageFormat)](#icu4j-comibmicutextmessageformat)
    - [Plural](#plural)
    - [Select (and gender)](#select-and-gender)
- [Full Example](#full-example)
- [Related MessageSources and Examples](#related-messagesources-and-examples)
- [License](#license)

## Dependency

### Maven
```xml
<dependency>
    <groupId>io.github.alaugks</groupId>
    <artifactId>spring-messagesource-json</artifactId>
    <version>2.1.0</version>
</dependency>
```

### Gradle 

```text
implementation group: 'io.github.alaugks', name: 'spring-messagesource-json', version: '2.1.0'
```


## MessageSource Configuration

`builder(Locale defaultLocale, String locationPattern)` (***required***)<br>
`builder(Locale defaultLocale, List<String> locationPattern)` (***required***)
* Argument `Locale defaultLocale`: Defines the default locale.
* Argument `locationPattern` (a single pattern or a list of patterns):
  * Defines the pattern(s) used to select the JSON files.
  * The package uses the [PathMatchingResourcePatternResolver](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/io/support/PathMatchingResourcePatternResolver.html) to select the JSON files. So you can use the supported patterns.
  * Files with the extension `json` are filtered from the result list.

`enableICU4j()`

* Formats messages with ICU4J's [`com.ibm.icu.text.MessageFormat`](https://unicode-org.github.io/icu-docs/apidoc/released/icu4j/com/ibm/icu/text/MessageFormat.html) instead of the default [`java.text.MessageFormat`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/MessageFormat.html). This adds support for named arguments and ICU `plural`/`select` patterns. Disabled by default. For more information, see [Message Formatting](#message-formatting).

`parentMessageSource(MessageSource messageSource)`

* Sets a parent `MessageSource` to delegate to. When a code cannot be resolved from the JSON files, the lookup falls back to the parent source. See [Parent MessageSource](docs/README-Parent-MessageSource.md) for usage in either order.

`targetLocaleResolver(TargetLocaleResolverInterface targetLocaleResolver)`

* Overrides how the locale of a JSON file is determined. By default, the locale is derived from the filename (see [Structure of the Translation Filename](#structure-of-the-translation-filename)). See [Custom Target Locale Resolver](#custom-target-locale-resolver) for details and an example.

`jsonCatalog(JsonCatalogInterface jsonCatalog)`

* Overrides how the JSON files are parsed into translation codes and values. By default, each file's top-level keys are read as a flat code &rarr; value map (see [JSON Files](#json-files)). See [Custom JSON Catalog](#custom-json-catalog) for details and an example.


### Example

* Default locale is `en`.
* The JSON files are stored in `src/main/resources/translations`.

```java
import io.github.alaugks.spring.messagesource.json.JsonResourceMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Locale;

@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {
       return JsonResourceMessageSource
               .builder(
                   Locale.forLanguageTag("en"),
                   "translations/*" // or List.of(...)
               )
               .build();
    }

}
```

## JSON Files

* Translations can be split across multiple files; the key is always taken from the JSON key itself, the filename has no effect on it. Since the key is what's looked up, keys must be unique across all files.
* Translation files must be stored in the resource folder and have the extension `json`.

### Structure of the Translation Filename

The `<name>` part is freely choosable and has no functional meaning; it's not used to derive keys and files aren't otherwise linked by it (see [JSON Files](#json-files)). What matters is that the locale is recognised as a suffix of the filename:

```
# Default language
<name>.json    // <name>_<language>.json also works.

# Name + Language
<name>[-_]<language>.json

# Name + Language + Region
<name>[-_]<language>[-_]<region>.json
```

### Example with JSON Files

* Default locale is `en` without region.
* Translations are provided for the locale `en`, `de` and `en-US`.

```
[resources]
     |-[translations]
             |-messages.json   // messages_en.json also works.
             |-messages_de.json
             |-messages_en-US.json
```  

#### JSON Files

> [!TIP]
> Translations can be organized across multiple JSON files however you like (e.g. by feature or module); this example keeps everything in one file per locale. Only requirement: keys must be unique across all files, since it is the key.

##### messages.json

```json
{
  "headline": "Headline",
  "postcode": "Postcode",
  "payment.headline": "Payment",
  "payment.expiry_date": "Expiry date"
}
```

##### messages_de.json

```json
{
  "headline": "Überschrift",
  "postcode": "Postleitzahl",
  "payment.headline": "Zahlung",
  "payment.expiry_date": "Ablaufdatum"
}
```

##### messages_en-US.json

```json
{
  "postcode": "Zip code",
  "payment.expiry_date": "Expiration date"
}
```

#### Target value

The behaviour of resolving the target value based on the code is equivalent to the ResourceBundleMessageSource or ReloadableResourceBundleMessageSource.

<table>
  <thead>
  <tr>
    <th>id (code)</th>
    <th>en</th>
    <th>en-US</th>
    <th>de</th>
    <th>jp**</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td>headline</td>
    <td>Headline</td>
    <td>Headline*</td>
    <td>Überschrift</td>
    <td>Headline</td>
  </tr>
  <tr>
    <td>postcode</td>
    <td>Postcode</td>
    <td>Zip code</td>
    <td>Postleitzahl</td>
    <td>Postcode</td>
  </tr>
  <tr>
    <td>payment.headline</td>
    <td>Payment</td>
    <td>Payment*</td>
    <td>Zahlung</td>
    <td>Payment</td>
  </tr>
  <tr>
    <td>payment.expiry_date</td>
    <td>Expiry date</td>
    <td>Expiration date</td>
    <td>Ablaufdatum</td>
    <td>Expiry date</td>
  </tr>
  </tbody>
</table>

> *Example of a fallback from Language_Region (`en-US`) to Language (`en`). The `id` does not exist in `en-US`, so it tries to select the translation with locale `en`.
> 
> **There is no translation for Japanese (`jp`). The default locale translations (`en`) are selected.

### Custom Target Locale Resolver

By default, the locale of a JSON file is derived from its filename (see [Structure of the Translation Filename](#structure-of-the-translation-filename)). Pass a custom `TargetLocaleResolverInterface` implementation to `targetLocaleResolver(...)` to derive it differently instead, e.g. from a field inside the JSON file itself.

##### messages_de.json

```json
{
  "targetLocale": "de",
  "postcode": "Postleitzahl",
  "payment.headline": "Zahlung"
}
```

```java
@Bean
public MessageSource messageSource() {
    return JsonResourceMessageSource
            .builder(
                Locale.forLanguageTag("en"),
                "translations/*"
            )
            .targetLocaleResolver(resource -> {
                try (InputStream inputStream = resource.getInputStream()) {
                    JsonNode json = new ObjectMapper().readTree(inputStream);
                    Locale locale = Locale.forLanguageTag(json.path("targetLocale").asText());
                    return new TransFileTargetLocale(locale);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .build();
}
```

> [!NOTE]
> `targetLocale` is only read to resolve the locale, it is not removed from the file. Since a JSON file is loaded as a flat code &rarr; value map (see [JSON Files](#json-files)), `targetLocale` also shows up as an (unused) message code with the value `de`.

### Custom JSON Catalog

By default, a JSON file's top-level keys are read as a flat code &rarr; value map (see [JSON Files](#json-files)). Pass a custom `JsonCatalogInterface` implementation to `jsonCatalog(...)` to parse the files differently instead, e.g. to nest the translations under their own key and keep metadata, such as `targetLocale`, out of the message codes.

##### messages_de.json

```json
{
  "targetLocale": "de",
  "translation": {
    "postcode": "Postleitzahl",
    "payment.headline": "Zahlung"
  }
}
```

```java
@Bean
public MessageSource messageSource() {
    return JsonResourceMessageSource
            .builder(
                Locale.forLanguageTag("en"),
                "translations/*"
            )
            .targetLocaleResolver(resource -> {
                try (InputStream inputStream = resource.getInputStream()) {
                    JsonNode json = new ObjectMapper().readTree(inputStream);
                    Locale locale = Locale.forLanguageTag(json.path("targetLocale").asText());
                    return new TransFileTargetLocale(locale);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .jsonCatalog(translationFiles -> {
                List<TransUnitInterface> transUnits = new ArrayList<>();
                ObjectMapper objectMapper = new ObjectMapper();

                for (TransFileInterface file : translationFiles) {
                    try {
                        JsonNode translation = objectMapper.readTree(file.content()).path("translation");
                        translation.properties().forEach(entry -> transUnits.add(
                            new TransUnit(file.locale(), entry.getKey(), entry.getValue().asText())
                        ));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }

                return transUnits;
            })
            .build();
}
```

Combined with the [Custom Target Locale Resolver](#custom-target-locale-resolver) above, `targetLocale` now only drives the locale and no longer leaks into the message codes, since the catalog only reads the `translation` node.

> [!NOTE]
> `JsonCatalogInterface` has a single method, `getTransUnits(List<TransFileInterface>)`, so — like `TargetLocaleResolverInterface` — it can be implemented as a lambda or as a standalone class.

## Message Formatting

A resolved value is formatted before it is returned, applying the arguments passed to `getMessage(...)` to the message pattern. Two formatters are available: the default `java.text.MessageFormat` and, once `enableICU4j()` is set, `com.ibm.icu.text.MessageFormat`.

> [!IMPORTANT]
> Named arguments and ICU `plural`/`select` patterns (e.g. `{count, plural, …}`) cannot be resolved by the default `java.text.MessageFormat` and fail at `getMessage()` time. To use them you **must** enable ICU4J via `enableICU4j()`.
>
> ICU4J is the [`com.ibm.icu:icu4j`](https://central.sonatype.com/artifact/com.ibm.icu/icu4j) dependency, which is shipped transitively with this package — no extra dependency is required. Its `com.ibm.icu.text.MessageFormat` is a syntax superset of `java.text.MessageFormat`, so existing numeric-index patterns keep working.
>
> Note that the two are not fully output-compatible: ICU4J uses Unicode CLDR locale data, so the formatted result for a given locale can differ from the JDK's — for example the decimal and grouping separators in numbers (`.` vs `,`). Verify locale-sensitive output after enabling ICU4J.

### Default (java.text.MessageFormat)

Without `enableICU4j()`, values are formatted with [`java.text.MessageFormat`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/MessageFormat.html) — the same formatter Spring's `ResourceBundleMessageSource` uses. It only understands **numeric argument indices** (`{0}`, `{1}`, …), passed positionally as an `Object[]`. Numbers are formatted locale-aware (grouping separators differ per locale).

#### messages.json

```json
{
  "files": "There are {0,number,integer} files."
}
```

#### messages_de.json

```json
{
  "files": "Es gibt {0,number,integer} Dateien."
}
```

```java
messageSource.getMessage(
    "files",
    new Object[] { 10000 },
    Locale.forLanguageTag("de")
);
```

**Result:** `Es gibt 10.000 Dateien.`

### ICU4J (com.ibm.icu.text.MessageFormat)

Enable ICU4J on the builder to format with [ICU4J's `MessageFormat`](https://unicode-org.github.io/icu-docs/apidoc/released/icu4j/com/ibm/icu/text/MessageFormat.html):

```java
@Bean
public MessageSource messageSource() {
    return JsonResourceMessageSource
            .builder(
                Locale.forLanguageTag("en"),
                "translations/*" // or List.of(...)
            )
            .enableICU4j() // required for named arguments and plural/select
            .build();
}
```

With ICU4J enabled, patterns can use **named arguments** and the ICU `plural`/`select` constructs. Named arguments are passed as a single `Map` (not as positional `{0}` / `{1}` arguments); the underlying catalog detects a lone `Map` argument and formats the pattern with it.

#### Plural

A `plural` switch selects a variant based on a number. Each case is either an **exact number** — matched as `=N` — or a **CLDR plural keyword** (`zero`, `one`, `two`, `few`, `many`, `other`) that the locale's plural rules select from the number. The number itself is inserted into a case by referencing the argument name, `{count}`.

Which keywords a language uses, and how each number maps to one, is defined per language in the [Unicode CLDR Language Plural Rules](https://www.unicode.org/cldr/charts/latest/supplemental/language_plural_rules.html).

##### messages.json

```json
{
  "file_deleted": "{count, plural, =0 {You deleted no files.} =1 {You deleted one file.} other {You deleted {count} files.}}"
}
```

##### messages_de.json

```json
{
  "file_deleted": "{count, plural, =0 {Sie haben keine Dateien gelöscht.} =1 {Sie haben eine Datei gelöscht.} other {Sie haben {count} Dateien gelöscht.}}"
}
```

```java
messageSource.getMessage(
    "file_deleted",
    new Object[] { Map.of("count", 1000) },
    Locale.forLanguageTag("de")
);
```

**Result:** `Sie haben 1.000 Dateien gelöscht.`

#### Select (and gender)

A `select` switch picks the case whose value matches the argument. Use it for any value-based choice such as grammatical gender; a final `other` case acts as the fallback.

##### messages.json

```json
{
  "greeting": "{recipient_gender, select, feminine {How is she?} masculine {How is he?} other {How are they?}}"
}
```

##### messages_de.json

```json
{
  "greeting": "{recipient_gender, select, feminine {Wie geht es ihr?} masculine {Wie geht es ihm?} other {Wie geht es ihnen?}}"
}
```

```java
messageSource.getMessage(
    "greeting",
    new Object[] { Map.of("recipient_gender", "feminine") },
    Locale.forLanguageTag("de")
);
```

**Result:** `Wie geht es ihr?`

## Full Example

[https://github.com/alaugks/spring-messagesource-json-example](https://github.com/alaugks/spring-messagesource-json-example)

[//]: # (## Support)

[//]: # ()
[//]: # (If you have questions, comments or feature requests please use the [Discussions]&#40;https://github.com/alaugks/spring-xliff-translation/discussions&#41; section.)

## Related MessageSources and Examples

* [XLIFF MessageSource for Spring](https://github.com/alaugks/spring-messagesource-xliff)
* [Example: XLIFF MessageSource for Spring](https://github.com/alaugks/spring-messagesource-xliff-example)
* [Example: JSON MessageSource for Spring](https://github.com/alaugks/spring-messagesource-json-example)
* [Example: Database MessageSource for Spring](https://github.com/alaugks/spring-messagesource-db-example)

## License

Licensed under the [Apache License, Version 2.0](LICENSE).

