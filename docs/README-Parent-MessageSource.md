# Parent MessageSource

A `MessageSource` can delegate to a **parent** `MessageSource`. When a code cannot be resolved in the primary source, the lookup falls back to the parent. This lets you combine the JSON translations with another source (for example a `ResourceBundleMessageSource`) and decide which one is asked first.

This works in **either order**:

1. [JSON first, other source as fallback](#1-json-first-other-source-as-fallback) — the JSON `MessageSource` is primary and delegates to the other source.
2. [Other source first, JSON as fallback](#2-other-source-first-json-as-fallback) — the other source is primary and delegates to the JSON `MessageSource`.

See the main [README](../README.md) for keys, filenames and the full `MessageSource` configuration.

## How resolution works

Spring resolves a code against a source and only asks the parent if the code is **not found**:

```
getMessage(code)
    │
    ▼
primary source ──found──► return message
    │
    │ not found
    ▼
parent source ──found──► return message
    │
    │ not found
    ▼
NoSuchMessageException
```

The source that is asked **first wins** for codes that exist in both. Pick the order based on which set of translations should take precedence.

## Table of Contents

- [1. JSON first, other source as fallback](#1-json-first-other-source-as-fallback)
- [2. Other source first, JSON as fallback](#2-other-source-first-json-as-fallback)
- [Which order should I use?](#which-order-should-i-use)

## 1. JSON first, other source as fallback

The JSON `MessageSource` is the primary source. A code is looked up in the JSON translations first; if it is missing, the lookup falls back to the parent (here a `ResourceBundleMessageSource`).

Use `parentMessageSource(...)` on the builder:

```java
import io.github.alaugks.spring.messagesource.json.JsonResourceMessageSource;
import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource parent = new ResourceBundleMessageSource();
        parent.setBasename("classpath:messages/messages");
        parent.setDefaultEncoding(StandardCharsets.UTF_8.name());
        parent.setFallbackToSystemLocale(false);

        return JsonResourceMessageSource
            .builder(
                Locale.forLanguageTag("en"),
                new LocationPattern("translations/*")
            )
            .parentMessageSource(parent)
            .build();
    }
}
```

Lookup order: **JSON translations → `messages` ResourceBundle**.

## 2. Other source first, JSON as fallback

The other `MessageSource` is the primary source. A code is looked up there first; if it is missing, the lookup falls back to the JSON translations.

Build the JSON `MessageSource` and set it as the parent of the primary source via `setParentMessageSource(...)`:

```java
import io.github.alaugks.spring.messagesource.json.JsonResourceMessageSource;
import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {
        MessageSource jsonMessageSource = JsonResourceMessageSource
            .builder(
                Locale.forLanguageTag("en"),
                new LocationPattern("translations/*")
            )
            .build();

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setParentMessageSource(jsonMessageSource);

        return messageSource;
    }
}
```

Lookup order: **`messages` ResourceBundle → JSON translations**.

## Which order should I use?

| Goal                                                                                              | Order                                                |
|---------------------------------------------------------------------------------------------------|------------------------------------------------------|
| JSON holds the translations; the other source only provides a few extra/legacy codes.             | [1. JSON first](#1-json-first-other-source-as-fallback) |
| An existing `ResourceBundle` setup stays authoritative; JSON adds or gradually replaces codes.    | [2. Other source first](#2-other-source-first-json-as-fallback) |

> [!NOTE]
> The parent chain is not limited to two sources. A parent can itself have a parent, so several `MessageSource`s can be chained; each level is asked only when the code was not resolved at the level before it.
