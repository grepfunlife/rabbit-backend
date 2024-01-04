package red.rabbit.models.event

import kotlinx.datetime.LocalDate
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@Serializable
data class EventResponse @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val id: Int? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val date: LocalDate? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val habitId: Int? = null
)