package red.rabbit.models.habit

import kotlinx.serialization.Serializable

@Serializable
data class HabitRequest(
    val name: String,
    val isGood: Boolean
)