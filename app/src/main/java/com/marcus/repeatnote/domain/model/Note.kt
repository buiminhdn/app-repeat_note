package com.marcus.repeatnote.domain.model

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: Category,
    val createdAt: Long,
)
