package com.elderease.app.data

/* ------------------------------------
   MEAL TYPES
------------------------------------ */
enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}

/* ------------------------------------
   FOOD ITEM
------------------------------------ */
data class FoodItem(
    val name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int
)

/* ------------------------------------
   MEAL ENTRY
------------------------------------ */
data class MealEntry(
    val mealType: MealType,
    val food: FoodItem,
    val timestamp: Long = System.currentTimeMillis()
)
