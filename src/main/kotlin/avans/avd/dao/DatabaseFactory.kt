package avans.avd.dao

import avans.avd.dto.UserRegisterDto
import avans.avd.dto.UserType
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

val daoInDatabase: DAOFacade = DAOInDatabase().apply {
    runBlocking {
        if (allUsers().isEmpty()) {
            registerUser(UserRegisterDto("henk@avans.nl", UserType.OTHER, "henk12345"))
            registerUser(UserRegisterDto("kate@avans.nl", UserType.STAFF, "kate12345"))
            registerUser(UserRegisterDto("frans@avans.nl", UserType.CLIENT, "frans12345"))
        }
    }
}

object DatabaseFactory {
    fun init() {
        // using postgresql (default for Hot Kitchen project):
//        val driverClassName = "org.postgresql.Driver"
//        val jdbcURL = "jdbc:postgresql://localhost:5432/mealplanner"
//        val user = "postgres"
//        val password = "admin"
//        val database = Database.connect(url = jdbcURL, driver = driverClassName, user, password)

        // using H2 (used in Hyperskill topics and in Ktor persistence documentation
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db" // save to a file
        val database = Database.connect(jdbcURL, driverClassName)

        transaction(database) {
            // for the Hyperskill tests: keep the inserted data!
            // in test stage 5, test 6 it is checked that a meal is added twice

            // when the schema is changed:
            SchemaUtils.drop(Users)

            SchemaUtils.create(Users)
        }
    }
}