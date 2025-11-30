package org.strawberryfoundations.wear.replicity.core

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class Preferences(
    val username: String = "",
    val email: String = "",
    val fullName: String = "",
    val profilePictureUrl: String = "",
    val token: String = ""
)

object UserPreferencesSerializer : Serializer<Preferences> {
    override val defaultValue: Preferences = Preferences()
    override suspend fun readFrom(input: InputStream): Preferences {
        return try {
            val json = input.readBytes().decodeToString()
            Json.decodeFromString<Preferences>(json)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read UserPreferences.", e)
        }
    }
    override suspend fun writeTo(t: Preferences, output: OutputStream) {
        output.write(Json.encodeToString(t).encodeToByteArray())
    }
}