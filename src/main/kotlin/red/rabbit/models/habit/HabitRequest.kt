package red.rabbit.models.habit

import kotlinx.serialization.Serializable

@Serializable
data class HabitRequest(
    val id: Int,
    val name: String,
    val isGood: Boolean
)