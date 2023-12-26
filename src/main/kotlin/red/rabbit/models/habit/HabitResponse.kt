package red.rabbit.models.habit

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class HabitResponse @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val id: Int? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val name: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val isGood: Boolean? = null
)