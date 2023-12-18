package red.rabbit.models

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse(
    val status: String,
    val message: String
)
