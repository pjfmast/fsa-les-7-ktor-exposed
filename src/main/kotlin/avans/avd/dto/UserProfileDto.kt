package avans.avd.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto (
    // the (unlogic) order of the field is according to the given example in:
    //     https://hyperskill.org/projects/284/stages/1482/implement
    val name: String,
    val userType: UserType,
    val phone: String,
    val email: String,
    val address: String,
)