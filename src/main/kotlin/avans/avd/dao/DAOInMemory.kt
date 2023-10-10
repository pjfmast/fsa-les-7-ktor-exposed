package avans.avd.dao

import avans.avd.dto.*

object daoInMemory : DAOFacade {
    override suspend fun registerUser(user: UserRegisterDto): Boolean {
        val isUniqueEmail =
            !users.containsKey(user.email) // changed les-6 (primary key is int index) to primary key is email
        if (isUniqueEmail) {
            users.put(user.email, User(user.email, user.userType, user.password))
        }
        return isUniqueEmail
    }

    override suspend fun allUsers(): List<UserRegisterDto> = users.values.map { it.toUserRegistrationDto() }

    override suspend fun signInUser(user: UserSignInDto): UserRegisterDto? {
        val userFound = users.values.firstOrNull { it.email == user.email }
        val validSignIn = userFound != null && userFound.password == user.password

        return if (validSignIn) {
            userFound?.toUserRegistrationDto()
        } else {
            null
        }
    }

    override suspend fun updateUser(userWithProfile: UserUpdateDto): Boolean {
        val userEmail = userWithProfile.email
        val oldUserInfo = users[userEmail]
        if (oldUserInfo != null) {
            val newUserDetails = UserDetails(userWithProfile.name, userWithProfile.phone, userWithProfile.address)
            val newUser = oldUserInfo.copy(userDetails = newUserDetails)
            users[userEmail] = newUser
        }

        return oldUserInfo != null
    }

    override suspend fun allUserProfiles(): List<UserProfileDto>
    = users.values.map { it.toUserProfileDto() }

    override suspend fun findUserWithEmail(email: String): UserProfileDto? {
        val found = users.values.firstOrNull { it.email == email }
        return found?.toUserProfileDto()
    }

    override suspend fun deleteUserWithEmail(email: String): Boolean {
        val removedUser = users.remove(email)
        return removedUser != null
    }


    private val users = mutableMapOf<String, User>(
        "John@gmail.com" to User("John@gmail.com", UserType.CLIENT),
        "Kate@gmail.com" to User("Kate@gmail.com", UserType.STAFF),
        "Mike@gmail.com" to User("Mike@gmail.com", UserType.CLIENT)
    )


    private data class User(
        val email: String,
        val userType: UserType,
        val password: String = "password123",
        val userDetails: UserDetails = UserDetails("", "", "")
    )


    private fun User.toUserRegistrationDto() = UserRegisterDto(this.email, this.userType, this.password)
    private fun User.toUserProfileDto() = UserProfileDto(
        this.userDetails.name,
        this.userType,
        this.userDetails.phone,
        this.email,
        this.userDetails.address
    )
}

