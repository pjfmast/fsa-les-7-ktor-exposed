package avans.avd.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterDto (
    val email: String,
    val userType: UserType,
    val password: String
)

enum class UserType {
    STAFF, CLIENT, OTHER
}