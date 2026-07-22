package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SavedStoryEntity
import com.example.ui.viewmodel.AiResponseState
import com.example.util.TextToSpeechHelper

@Composable
fun AiStoriesScreen(
    aiResponseState: AiResponseState,
    savedStories: List<SavedStoryEntity>,
    onAskQuestion: (String) -> Unit,
    onGenerateStory: (String, String) -> Unit,
    onDeleteStory: (Long) -> Unit,
    ttsHelper: TextToSpeechHelper,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("🧙‍♂️ اسأل سمسم", "📖 صانع القصص", "📚 مكتبة قصصي")

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTabIndex) {
            0 -> AskAiTab(
                aiResponseState = aiResponseState,
                onAskQuestion = onAskQuestion,
                ttsHelper = ttsHelper
            )
            1 -> StoryGeneratorTab(
                aiResponseState = aiResponseState,
                onGenerateStory = onGenerateStory,
                ttsHelper = ttsHelper
            )
            2 -> StoryLibraryTab(
                savedStories = savedStories,
                onDeleteStory = onDeleteStory,
                ttsHelper = ttsHelper
            )
        }
    }
}

@Composable
fun AskAiTab(
    aiResponseState: AiResponseState,
    onAskQuestion: (String) -> Unit,
    ttsHelper: TextToSpeechHelper
) {
    var questionInput by remember { mutableStateOf("") }
    val sampleQuestions = listOf(
        "لماذا تشرق الشمس في الصباح؟ ☀️",
        "كيف تطير الطيور في السماء؟ 🐦",
        "من أين يأتي المطر والغيوم؟ 🌧️",
        "لماذا نحتاج إلى النوم بالليل؟ 🌙",
        "كيف تتنفس الأسماك تحت الماء؟ 🐟"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🧙‍♂️", style = MaterialTheme.typography.displaySmall)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "المعلم الذكي \"سِمْسِم\" 🦉",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "اسألني أي سؤال في البال، وسأشرحه لك بأسلوب قصصي ممتع ومبسط!",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Questions Presets
        item {
            Text(
                text = "أسئلة شائعة يحبها الأبطال 💡",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sampleQuestions) { question ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                questionInput = question
                                onAskQuestion(question)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = question,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        }

        // Custom Question Input
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = questionInput,
                        onValueChange = { questionInput = it },
                        placeholder = { Text("اكتب سؤالك هنا يا بطل (مثال: كيف يصنع العسل؟)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_question_input"),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (questionInput.isNotBlank()) {
                                onAskQuestion(questionInput)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("ai_ask_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اسأل المعلم الذكي 🪄", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // AI Output Response State
        item {
            when (aiResponseState) {
                is AiResponseState.Loading -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "المعلم سمسم يفكر ويحضر الإجابة السحرية... 🧙‍♂️✨",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
                is AiResponseState.Success -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        elevation = CardDefaults.cardElevation(3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "إجابة المعلم سمسم 🌟",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Button(
                                    onClick = { ttsHelper.speak(aiResponseState.text) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("استمع 🔊", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = aiResponseState.text,
                                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                is AiResponseState.Error -> {
                    Text(text = "حدث خطأ: ${aiResponseState.message}", color = MaterialTheme.colorScheme.error)
                }
                else -> {}
            }
        }
    }
}

@Composable
fun StoryGeneratorTab(
    aiResponseState: AiResponseState,
    onGenerateStory: (String, String) -> Unit,
    ttsHelper: TextToSpeechHelper
) {
    var selectedTopic by remember { mutableStateOf("الفضاء والأجرام السماوية") }
    var characterName by remember { mutableStateOf("سِمْسِم") }

    val topics = listOf(
        "الفضاء والأجرام السماوية 🚀",
        "حيوانات الغابة السحرية 🦁",
        "أعماق البحار والمحيطات 🐬",
        "أهمية النظافة والصحة 🧼",
        "الصداقة والتعاون 🤝",
        "رحلة إلى المستقبل 🤖"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "صانع القصص التفاعلية 📖✨",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اختر موضوع القصة واسم بطلها لتقوم تقنية الذكاء الاصطناعي بتأليف قصة خاصة بك!",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            Text(
                text = "1. اختر موضوع القصة 🎯",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                topics.forEach { topic ->
                    val isSelected = topic == selectedTopic
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTopic = topic }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (isSelected) "🟢" else "⚪", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = topic,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "2. اسم بطل القصة 🦸‍♂️",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = characterName,
                onValueChange = { characterName = it },
                label = { Text("اسم البطل/البطلة") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = { onGenerateStory(selectedTopic, characterName) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_story_button")
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("أنشئ القصة الآن 🪄", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        item {
            when (aiResponseState) {
                is AiResponseState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("جاري تأليف قصة ساحرة...")
                        }
                    }
                }
                is AiResponseState.Success -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        elevation = CardDefaults.cardElevation(3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "قصتك الجاهزة 📚",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Button(
                                    onClick = { ttsHelper.speak(aiResponseState.text) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("استمع للقصة")
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = aiResponseState.text,
                                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp)
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun StoryLibraryTab(
    savedStories: List<SavedStoryEntity>,
    onDeleteStory: (Long) -> Unit,
    ttsHelper: TextToSpeechHelper
) {
    if (savedStories.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "📚", style = MaterialTheme.typography.displayMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "لا توجد قصص محفوظة حتى الآن. أنشئ قصتك الأولى في صانع القصص!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(savedStories) { story ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = story.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            IconButton(onClick = { onDeleteStory(story.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف القصة",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = story.storyContent,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { ttsHelper.speak("${story.title}. ${story.storyContent}") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("استمع للقصة كاملة 🔊")
                        }
                    }
                }
            }
        }
    }
}
