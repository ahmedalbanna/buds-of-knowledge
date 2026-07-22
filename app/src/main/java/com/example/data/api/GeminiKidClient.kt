package com.example.data.api

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class Part(
    val text: String? = null
)

data class Content(
    val parts: List<Part>
)

data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

data class Candidate(
    val content: Content? = null
)

data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

interface GeminiKidApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiKidClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val service: GeminiKidApiService = retrofit.create(GeminiKidApiService::class.java)

    private const val KID_SYSTEM_PROMPT = """
أنت المعلم الذكي واللطيف "سمسم" في تطبيق "براعم المعرفة". 
مهمتك: الشرح للأطفال في مرحلة التمهيدي والابتدائي (عمر 4 إلى 10 سنوات).
قواعد الإجابة:
1. استخدم لغة عربية بسيطة جداً، ممتعة، ومفعمة بالدفء والمرح.
2. اسرد الإجابات بأسلوب قصصي مشوق إذا أمكن، مع إيموجيات لطيفة مناسبة للأطفال (مثل 🦁, 🌟, 🚀, 🍎).
3. اجعل الإجابات قصيرة ومباشرة (لا تتجاوز 4-6 أسطر) لتناسب استيعاب الأطفال.
4. اذكر دائماً حكمة أو نصيحة تربوية لطيفة في نهاية القصة أو الإجابة.
5. شجّع الطفل بكلمات مثل "يا بطل"، "يا صغيّري المبدع"، "سؤال رائع جداً!".
"""

    suspend fun generateKidResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "مرحباً يا بطل! 🌟 أنا معلمك الذكي سمسم. يمكنك السؤال وتصفح ألعاب الحروف والأرقام الممتعة الآن! 🎈"
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = KID_SYSTEM_PROMPT)))
        )

        try {
            val response = service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            text ?: "قصة رائعة يا بطل! 🌟 دعنا نستكشف المزيد من العالم العجيب معاً!"
        } catch (e: Exception) {
            "مرحباً يا صغيّري! 🌟 حدث تعثر بسيط في الاتصال. يمكنك إعادة السؤال أو قراءة إحدى القصص الممتعة في مكتبتك! 📚"
        }
    }

    suspend fun generateCustomStory(topic: String, characterName: String): String = withContext(Dispatchers.IO) {
        val storyPrompt = "اكتب قصة قصيرة جداً وممتعة للأطفال عن موضوع ($topic) وبطل القصة هو ($characterName). اجعل القصة فيها أحداث لطيفة وحكمة تربوية جميلة، مع إيموجيات مشوقة."
        generateKidResponse(storyPrompt)
    }
}
