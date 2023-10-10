package avans.avd.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateDto (
    val name: String,
    val userType: UserType,
    val phone: String,
    val email: String,
    val address: String
)