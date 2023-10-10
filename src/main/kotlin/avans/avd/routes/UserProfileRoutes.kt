package avans.avd.routes

import avans.avd.dto.UserType
import avans.avd.dto.UserUpdateDto
import avans.avd.plugins.AccessDeniedException
import avans.avd.plugins.InvalidUpdateMeException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// here we switch between different implementations of the DAO Interface:
// (but DI would be better)
import avans.avd.dao.daoInMemory as dao
//import avans.avd.dao.daoInDatabase as dao

// for stage 3/5
fun Route.userProfileRouting() {
    authenticate() {

        get("/userprofile") {
            val principal = call.principal<JWTPrincipal>()

            val userType = UserType.valueOf(principal?.payload?.getClaim("userType")?.asString() ?: UserType.CLIENT.name)
            if (userType != UserType.STAFF) throw AccessDeniedException()

            val userProfiles = dao.allUserProfiles()
            call.respond(userProfiles)
        }

        get("/me") {
            val principal = call.principal<JWTPrincipal>()
            val email = principal?.payload?.getClaim("email")?.asString() ?: ""
            val userWithProfile = dao.findUserWithEmail(email)
            if (userWithProfile == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                call.respond(userWithProfile)
            }
        }

        delete("/me") {
            val principal = call.principal<JWTPrincipal>()
            val email = principal?.payload?.getClaim("email")?.asString() ?: ""
            val isDeleted = dao.deleteUserWithEmail(email)
            if (isDeleted) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }


        put("/me") {
            val updateUserDto = call.receive<UserUpdateDto>()
            val principal = call.principal<JWTPrincipal>()
            val email = principal?.payload?.getClaim("email")?.asString() ?: ""

            if (updateUserDto.email == email) {
                val isUpdatedUser = dao.updateUser(updateUserDto)
                if (isUpdatedUser) {
                    call.respond(updateUserDto)
                } else {
                    throw InvalidUpdateMeException()
                }
            }
            else throw InvalidUpdateMeException()
        }
    }
}