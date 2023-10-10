package avans.avd.routes

import avans.avd.dto.UserRegisterDto
import avans.avd.dto.UserSignInDto
import avans.avd.plugins.*
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

fun Route.userRouting() {
    authenticate {
        get("/validate") {
            val principal = call.principal<JWTPrincipal>()
            val email = principal?.get("email")
            val userType = principal?.get("userType")
            call.respondText("Hello, $email ($userType) you have validated access!")
        }
    }

    get("/") {
        call.respondText("Hello to FSA les 7!")
    }

    get("/users") {
        call.respond(dao.allUsers())
    }

    // In the example ktor-les-7 we use email as the unique key
    // only the signedin user can retrieve the user profile using the email claim
//    get("/users/{id}") <== removed function from ktor-les6

    post("/signin") {
        val user = call.receive<UserSignInDto>()
        val signedIn = dao.signInUser(user)
        if (signedIn != null) {
            val jwt = createJWT(signedIn.email, signedIn.userType.name)
            call.respond(message = mapOf("token" to jwt))
        } else {
            throw WrongSigninException()
        }
    }

    post("/signup") {
        val user = call.receive<UserRegisterDto>()
        val isRegistered = dao.registerUser(user)
        if (isRegistered) {
            val jwt = createJWT(user.email, user.userType.name)
            call.respond(message = mapOf("token" to jwt))
        } else {
            throw AlreadyExistsException(user.email)
        }
    }

}
