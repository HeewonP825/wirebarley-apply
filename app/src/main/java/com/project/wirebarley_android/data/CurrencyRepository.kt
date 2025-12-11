package com.project.wirebarley_android.data

import com.project.wirebarley_android.BuildConfig
import com.project.wirebarley_android.models.CurrencyApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class CurrencyRepository(
    private val client: OkHttpClient = OkHttpClient()
) {
    private val apiKey: String = BuildConfig.CURRENCY_API_KEY

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchLiveRates(): Result<CurrencyApiResponse> = withContext(Dispatchers.IO) {
        try {
            val url = "https://apilayer.net/api/live?access_key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute().use { resp ->
                val code = resp.code
                val bodyText = resp.body.string()

                if (!resp.isSuccessful) {
                    return@withContext Result.failure(IOException("HTTP $code - body: $bodyText"))
                }

                if (bodyText.isBlank()) {
                    return@withContext Result.failure(IOException("Empty body"))
                }

                val parsed = json.decodeFromString<CurrencyApiResponse>(bodyText)
                return@withContext Result.success(parsed)
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}