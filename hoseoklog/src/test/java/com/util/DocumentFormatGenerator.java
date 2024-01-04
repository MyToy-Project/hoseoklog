package com.util;

import static org.springframework.restdocs.snippet.Attributes.Attribute;
import static org.springframework.restdocs.snippet.Attributes.key;

public class DocumentFormatGenerator {

    public static Attribute getAttribute(final String key, final String value) {
        return key(key).value(value);
    }
}
