package com.example

import ch.qos.logback.core.subst.Token
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class User(
    val login: String,
    val password: String
)
@Serializable
data class AuthResponse @OptIn(ExperimentalUuidApi::class) constructor(
    val token: Uuid
)
@Serializable
data class ErrorResponse(
    val errorMessage: String
)
@OptIn(ExperimentalUuidApi::class)
fun Application.configureRouting() {
    routing {
        post("/auth") {
            try {
                val currentUser = User(login = "maxim199901", password = "Qwerty2015".md5())
                val user = Json.decodeFromString<User>(call.receiveText())
                println(user)
                if (currentUser.login == user.login &&
                    currentUser.password.md5() == user.password.md5()) {
                    println(user)
                    call.respond(HttpStatusCode.OK, AuthResponse(Uuid.random()))
                } else {
                     call.respond(HttpStatusCode.BadRequest, ErrorResponse(errorMessage = "Неверный логин/пароль"))
                }
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (ex: JsonConvertException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.toHexString()
}
