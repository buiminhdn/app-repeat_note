package com.marcus.repeatnote.domain.model

enum class Category {
    FAMILY, NUMBERS, STUDY, WORK, QUOTES, LANGUAGE, OTHER;

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}
