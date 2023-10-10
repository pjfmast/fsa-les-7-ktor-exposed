package avans.avd.dao

import avans.avd.dto.UserProfileDto
import avans.avd.dto.UserType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Column

class User(id: EntityID<Int>): IntEntity(id) {
    fun toUserProfileDto(): UserProfileDto {
        val json = Json {  }
        val userDetails: UserDetails = json.decodeFromString<UserDetails>(this.userDetails)

        return UserProfileDto (
            name = userDetails.name,
            userType = this.userType,
            phone =  userDetails.phone,
            email = this.email,
            address = userDetails.address
        )
    }

    companion object : IntEntityClass<User>(Users)
    // We get the fields used from the table using delegates.
    var email by Users.email
    var userType by Users.userType
    var password by Users.password
    var userDetails by Users.userDetails
}

object Users : IntIdTable() {
    val email: Column<String> = text("email").uniqueIndex("unique_email")
    val userType: Column<UserType> = Users.enumerationByName("user_type", 80, UserType::class)
    val password: Column<String> = text("password")
    val userDetails: Column<String> = text("userDetails").default(UserDetails.encodeToString(UserDetails()))
}

@Serializable
data class UserDetails(
    val name: String = "",
    val phone: String = "",
    val address: String = ""
) {
    companion object {
        private val json = Json {}
        // The following are so called 'Static Factory Methods'
        fun decodeFromString(jsonString: String) = json.decodeFromString<UserDetails>(jsonString)
        fun encodeToString(userDetails: UserDetails) = json.encodeToString(userDetails)
    }
}