package com.futurion.apps.mathmingle.domain.model

enum class GameType(val displayName: String) {
    MISSING_NUMBER("Missing Number"),
    MISSING_OPERATOR("Missing Operator"),
    TRUE_FALSE("True/False"),
    REVERSE("Reverse Equation"),
    MIX("mix")
}