package red.rabbit.models.habit

import kotlinx.serialization.Serializable
import red.rabbit.models.BaseResponse

@Serializable
data class HabitResponse(
    val response: BaseResponse,
    val habitId: Int?,
    val habitName: String?,
    val habitIsGood: Boolean?
)