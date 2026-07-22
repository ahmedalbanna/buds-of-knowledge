package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LearningDataProvider
import com.example.data.model.RhymeItem
import com.example.util.TextToSpeechHelper
import kotlinx.coroutines.delay

data class MemoryCard(
    val id: Int,
    val content: String,
    val matchPairId: Int,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun GamesAndSongsScreen(
    ttsHelper: TextToSpeechHelper,
    onAddStars: (Int, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("🎵 أناشيد تعليمية", "🧩 لعبة المطابقة والذاكرة")

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
                    text = { Text(text = title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTabIndex) {
            0 -> RhymesTab(
                rhymes = LearningDataProvider.rhymesList,
                ttsHelper = ttsHelper,
                onAddStars = onAddStars
            )
            1 -> MemoryGameTab(
                ttsHelper = ttsHelper,
                onAddStars = onAddStars
            )
        }
    }
}

@Composable
fun RhymesTab(
    rhymes: List<RhymeItem>,
    ttsHelper: TextToSpeechHelper,
    onAddStars: (Int, String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(rhymes) { rhyme ->
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = rhyme.iconEmoji, style = MaterialTheme.typography.displaySmall)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = rhyme.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = rhyme.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = {
                                ttsHelper.speak("${rhyme.title}. ${rhyme.lyrics}")
                                onAddStars(2, "استماع لأنشودة ${rhyme.title}", "الأناشيد التعليمية")
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("play_rhyme_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استمع 🎶")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = rhyme.lyrics,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 30.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryGameTab(
    ttsHelper: TextToSpeechHelper,
    onAddStars: (Int, String, String) -> Unit
) {
    val cardsList = remember {
        mutableStateListOf(
            MemoryCard(1, "أ", 101), MemoryCard(101, "🦁", 1),
            MemoryCard(2, "ب", 102), MemoryCard(102, "🦆", 2),
            MemoryCard(3, "ت", 103), MemoryCard(103, "🍎", 3),
            MemoryCard(4, "١", 104), MemoryCard(104, "⭐", 4)
        ).apply { shuffle() }
    }

    var flippedIndices = remember { mutableStateListOf<Int>() }
    var matchesFound by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    fun restartGame() {
        cardsList.forEach {
            it.isFlipped = false
            it.isMatched = false
        }
        cardsList.shuffle()
        flippedIndices.clear()
        matchesFound = 0
        isGameOver = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "لعبة مطابقة البطاقات 🧩",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "طابق بين الحرف والصورة لتكسب النجوم!",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(onClick = { restartGame() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isGameOver) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎉 🏆 🌟", style = MaterialTheme.typography.displayMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "مبروك يا بطل! أنهيت اللعبة بنجاح!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "حصلت على 5 نجوم ذهبية 🌟",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { restartGame() }) {
                        Text("العب مرة أخرى 🚀", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(cardsList) { index, card ->
                    val isFlipped = card.isFlipped || card.isMatched
                    Box(
                        modifier = Modifier
                            .size(75.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isFlipped) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                            )
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                if (!card.isFlipped && !card.isMatched && flippedIndices.size < 2) {
                                    card.isFlipped = true
                                    flippedIndices.add(index)

                                    if (flippedIndices.size == 2) {
                                        val idx1 = flippedIndices[0]
                                        val idx2 = flippedIndices[1]
                                        val card1 = cardsList[idx1]
                                        val card2 = cardsList[idx2]

                                        if (card1.matchPairId == card2.id || card2.matchPairId == card1.id) {
                                            card1.isMatched = true
                                            card2.isMatched = true
                                            matchesFound += 1
                                            flippedIndices.clear()
                                            ttsHelper.speak("مطابقة ممتازة!")

                                            if (matchesFound >= 4) {
                                                isGameOver = true
                                                onAddStars(5, "إكمال لعبة المطابقة والذاكرة", "الألعاب والمسابقات")
                                            }
                                        } else {
                                            // Delay flip back
                                            // In compose simple state reset
                                            card1.isFlipped = false
                                            card2.isFlipped = false
                                            flippedIndices.clear()
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFlipped) {
                            Text(text = card.content, style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold))
                        } else {
                            Text(text = "❓", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}
