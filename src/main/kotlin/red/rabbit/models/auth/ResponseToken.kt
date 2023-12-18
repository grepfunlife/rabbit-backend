package red.rabbit.models.auth

import kotlinx.serialization.Serializable
import red.rabbit.models.BaseResponse

@Serializable
data class ResponseToken(
    val response: BaseResponse,
    val token: String?
)