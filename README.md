# JSON MessageSource for Spring

This package provides a [MessageSource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/MessageSource.html) for using translations from JSON files.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=alaugks_spring-messagesource-json&metric=alert_status)](https://sonarcloud.io/summary/overall?id=alaugks_spring-messagesource-json)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.alaugks/spring-messagesource-json.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.alaugks/spring-messagesource-json/0.1.0)

## Dependency

### Maven
```xml
<dependency>
    <groupId>io.github.alaugks</groupId>
    <artifactId>spring-messagesource-json</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle 

```text
implementation group: 'io.github.alaugks', name: 'spring-messagesource-json', version: '0.1.0'
```


## MessageSource Configuration

`builder(Locale defaultLocale, LocationPattern locationPattern)` (***required***)
* Argument `Locale locale`: Defines the default locale.
* Argument `String locationPattern` | `List<String> locationPatterns`:
  * Defines the pattern used to select the JSON files.
  * The package uses the [PathMatchingResourcePatternResolver](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/io/support/PathMatchingResourcePatternResolver.html) to select the JSON files. So you can use the supported patterns.
  * Files with the extension `xliff` and `json` are filtered from the result list.

`defaultDomain(String defaultDomain)`

* Defines the default domain. Default is `messages`. For more information, see [JSON Files](#xliff-files).


### Example

* Default locale is `en`.
* The JSON files are stored in `src/main/resources/translations`.

```java
import io.github.alaugks.spring.messagesource.json.JsonResourceMessageSource;
import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
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
                   new LocationPattern("translations/*")
               )
               .build();
    }

}
```

## JSON Files

* Translations can be separated into different files (domains). The default domain is `messages`.
* The default domain can be defined.
* Translation files must be stored in the resource folder and have the extension `json`.

### Structure of the Translation Filename

```
# Default language
<domain>.json    // <domain>_<language>.json also works.

# Domain + Language
<domain>[-_]<language>.json

# Domain + Language + Region
<domain>[-_]<language>[-_]<region>.json
```

### Example with JSON Files


* Default domain is `messages`.

* Default locale is `en` without region.

* Translations are provided for the locale `en`, `de` and `en-US`.

```
[resources]
     |-[translations]
             |-messages.json           // Default domain and default language. messages_en.json also works.
             |-messages_de.json
             |-messages_en-US.json
             |-payment.json            // Default language. payment_en.json also works.
             |-payment_de.json
             |-payment_en-US.json     
```  

#### JSON Files

Mixing JSON versions is possible. Here is an example using JSON 1.2 and JSON 2.1.

##### messages.json

```json
{
  "headline": "Headline",
  "postcode": "Postcode"
}
```

##### messages_de.json

```json
{
  "headline": "Überschrift",
  "postcode": "Postleitzahl"
}
```

##### messages_en-US.json

```json
{
  "postcode": "Zip code"
}
```

##### payment.json

```json
{
  "headline": "Payment",
  "expiry_date": "Expire date"
}
```

##### payment_de.json

```json
{
  "headline": "Zahlung",
  "expiry_date": "Ablaufdatum"
}
```

##### payment_en-US.json

```json
{
  "expiry_date": "Expiration date"
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
    <th>jp***</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td>headline*<br>messages.headline</td>
    <td>Headline</td>
    <td>Headline**</td>
    <td>Überschrift</td>
    <td>Headline</td>
  </tr>
  <tr>
    <td>postcode*<br>messages.postcode</td>
    <td>Postcode</td>
    <td>Zip code</td>
    <td>Postleitzahl</td>
    <td>Postcode</td>
  </tr>
  <tr>
    <td>payment.headline</td>
    <td>Payment</td>
    <td>Payment**</td>
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

> *Default domain is `messages`.
>
> **Example of a fallback from Language_Region (`en-US`) to Language (`en`). The `id` does not exist in `en-US`, so it tries to select the translation with locale `en`.
> 
> ***There is no translation for Japanese (`jp`). The default locale translations (`en`) are selected.

[//]: # (## Full Example)

[//]: # ()
[//]: # (A Full Example using Spring Boot, mixing JSON 1.2 and JSON 2.1 translation files:)

[//]: # ()
[//]: # (Repository: https://github.com/alaugks/spring-messagesource-json-example<br>)

[//]: # (Website: https://spring-boot-xliff-example.alaugks.dev)

## Support

If you have questions, comments or feature requests please use the [Discussions](https://github.com/alaugks/spring-xliff-translation/discussions) section.

## Related MessageSources 

* [spring-messagesource-db-example](https://github.com/alaugks/spring-messagesource-db-example): Example custom Spring MessageSource from database
* [spring-messagesource-json-example](https://github.com/alaugks/spring-messagesource-json-example): Example custom Spring MessageSource from JSON
