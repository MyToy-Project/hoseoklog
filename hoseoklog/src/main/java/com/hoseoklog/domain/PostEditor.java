package com.hoseoklog.domain;

import java.util.Objects;

public record PostEditor(
        String title,
        String content
) {

    public static PostEditorBuilder builder() {
        return new PostEditor.PostEditorBuilder(null, null);
    }

    public record PostEditorBuilder(String title, String content) {

        public PostEditorBuilder title(final String title) {
            if (Objects.isNull(title)) {
                return this;
            }
            return new PostEditorBuilder(title, content);
        }

        public PostEditorBuilder content(final String content) {
            if (Objects.isNull(content)) {
                return this;
            }
            return new PostEditorBuilder(title, content);
        }

        public PostEditor build() {
            return new PostEditor(title, content);
        }
    }
}
