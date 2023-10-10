package avans.avd.dao

import avans.avd.dto.UserProfileDto
import avans.avd.dto.UserRegisterDto
import avans.avd.dto.UserSignInDto
import avans.avd.dto.UserUpdateDto

interface DAOFacade {
    suspend fun registerUser(user: UserRegisterDto): Boolean
    suspend fun allUsers(): List<UserRegisterDto>
    suspend fun signInUser(user: UserSignInDto): UserRegisterDto?

    // stage 3/5
    suspend fun updateUser(userWithProfile: UserUpdateDto): Boolean
    suspend fun findUserWithEmail(email: String): UserProfileDto?
    suspend fun deleteUserWithEmail(email: String): Boolean

    suspend fun allUserProfiles(): List<UserProfileDto>
}