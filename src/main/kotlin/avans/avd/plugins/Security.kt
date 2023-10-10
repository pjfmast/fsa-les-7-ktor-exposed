package avans.avd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.time.Instant
import java.util.*

// Please read the jwt property from the config file if you are using EngineMain
val jwtAudience = "jwt-audience"
val jwtDomain = "https://jwt-provider-domain/"
val jwtRealm = "ktor sample app"
val jwtSecret = "secret"

fun Application.configureSecurity() {

    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}


fun createJWT(email: String, userType: String) = JWT.create()
    .withAudience(jwtAudience)
    .withIssuer(jwtDomain)
    .withClaim("email", email)
    .withClaim("userType", userType)
    .withExpiresAt(Date(System.currentTimeMillis() + 24*60*60000))
    .withIssuedAt(Instant.now())
    .sign(Algorithm.HMAC256(jwtSecret))