package red.rabbit.models.habit

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class HabitResponse @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val habitId: Int? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val habitName: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val habitIsGood: Boolean? = null
)