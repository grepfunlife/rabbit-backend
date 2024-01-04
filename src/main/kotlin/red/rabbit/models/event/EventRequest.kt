package red.rabbit.models.event

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class EventRequest(
    val id: Int,
    val date: LocalDate,
    val habitId: Int
)

@Serializable
data class EventRequestBulk(
    val dates: List<LocalDate>,
    val habitId: Int
)
