package avans.avd.dao

import avans.avd.dto.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class DAOInDatabase : DAOFacade {
    private fun resultRowToUser(row: User) = UserRegisterDto(
        email = row.email,
        userType = row.userType,
        password = row.password
    )

    private fun resultRowToUserProfile(row: User): UserProfileDto {
        val userDetails = UserDetails.decodeFromString(row.userDetails)
        return UserProfileDto(
            email = row.email,
            userType = row.userType,
            name = userDetails.name,
            phone = userDetails.phone,
            address = userDetails.address
        )
    }

    override suspend fun registerUser(user: UserRegisterDto): Boolean = transaction {
        val notExistsUser = User.find { Users.email eq user.email }.empty()

        if (notExistsUser)
            User.new {
                email = user.email
                userType = user.userType
                password = user.password
                userDetails = UserDetails.encodeToString(UserDetails()) // create empty details json string
            }

        notExistsUser
    }

    // For development test, normally a password would not be exposed
    override suspend fun allUsers(): List<UserRegisterDto> = transaction {
        User.all().map(::resultRowToUser)
    }

    override suspend fun signInUser(user: UserSignInDto): UserRegisterDto? = transaction {
        User.find { (Users.email eq user.email) and (Users.password eq user.password) }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun updateUser(userWithProfile: UserUpdateDto): Boolean = transaction {
        val newUserDetails = UserDetails(userWithProfile.name, userWithProfile.phone, userWithProfile.address)
        Users.update({ Users.email eq userWithProfile.email })
        {
            it[userType] = userWithProfile.userType
            it[userDetails] = UserDetails.encodeToString(newUserDetails)
        } > 0
    }

    override suspend fun findUserWithEmail(email: String): UserProfileDto? = transaction {
        User.find { Users.email eq email }
            .map { it.toUserProfileDto() }
            .singleOrNull()
    }

    override suspend fun deleteUserWithEmail(email: String): Boolean = transaction {
        Users.deleteWhere { Users.email eq email } > 0
    }

    override suspend fun allUserProfiles(): List<UserProfileDto> = transaction {
        User.all().map(::resultRowToUserProfile)
    }

}

