package red.rabbit.models.event

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class EventResponse @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val id: Int? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val habitId: Int? = null
)