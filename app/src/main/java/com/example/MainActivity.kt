package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AiStoriesScreen
import com.example.ui.screens.GamesAndSongsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.ParentScreen
import com.example.ui.screens.ScreenLockOverlay
import com.example.ui.theme.BaraemTheme
import com.example.ui.viewmodel.AiTutorViewModel
import com.example.ui.viewmodel.LearningViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ParentViewModel
import com.example.util.rememberTextToSpeech

enum class NavigationItem(val title: String, val icon: ImageVector) {
    HOME("الرئيسية", Icons.Default.Home),
    LEARN("تعلم", Icons.Default.School),
    AI_STORIES("ذكاء اصطناعي", Icons.Default.AutoAwesome),
    GAMES("أناشيد وألعاب", Icons.Default.SportsEsports),
    PARENTS("الوالدين", Icons.Default.SupervisorAccount)
}

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val aiTutorViewModel: AiTutorViewModel by viewModels()
    private val learningViewModel: LearningViewModel by viewModels()
    private val parentViewModel: ParentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BaraemTheme {
                // Enforce Right-To-Left (RTL) Layout Direction for Arabic language!
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    BaraemAppContent(
                        mainViewModel = mainViewModel,
                        aiTutorViewModel = aiTutorViewModel,
                        learningViewModel = learningViewModel,
                        parentViewModel = parentViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun BaraemAppContent(
    mainViewModel: MainViewModel,
    aiTutorViewModel: AiTutorViewModel,
    learningViewModel: LearningViewModel,
    parentViewModel: ParentViewModel
) {
    val userProgress by mainViewModel.userProgress.collectAsStateWithLifecycle()
    val parentSettings by mainViewModel.parentSettings.collectAsStateWithLifecycle()
    val activityLogs by mainViewModel.activityLogs.collectAsStateWithLifecycle()
    val isScreenLocked by mainViewModel.isScreenLocked.collectAsStateWithLifecycle()
    val remainingTimeSeconds by mainViewModel.remainingTimeSeconds.collectAsStateWithLifecycle()

    val aiResponseState by aiTutorViewModel.aiResponseState.collectAsStateWithLifecycle()
    val savedStories by aiTutorViewModel.savedStories.collectAsStateWithLifecycle()

    val isParentAreaUnlocked by parentViewModel.isUnlocked.collectAsStateWithLifecycle()

    val ttsHelper = rememberTextToSpeech()

    var currentNavIndex by remember { mutableIntStateOf(0) }
    var selectedLearnTab by remember { mutableIntStateOf(0) }

    val selectedLetter by learningViewModel.selectedLetter.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationItem.values().forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = currentNavIndex == index,
                            onClick = { currentNavIndex = index },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (currentNavIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("nav_item_${item.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentNavIndex) {
                    0 -> HomeScreen(
                        progress = userProgress,
                        remainingTimeSeconds = remainingTimeSeconds,
                        activityLogs = activityLogs,
                        onNavigateToLearn = { tabIndex ->
                            selectedLearnTab = tabIndex
                            currentNavIndex = 1
                        },
                        onNavigateToAiTutor = { currentNavIndex = 2 },
                        onNavigateToGames = { currentNavIndex = 3 },
                        onNavigateToParents = { currentNavIndex = 4 }
                    )
                    1 -> LearnScreen(
                        initialTab = selectedLearnTab,
                        alphabets = learningViewModel.alphabets,
                        numbers = learningViewModel.numbers,
                        mathQuestions = learningViewModel.mathQuestions,
                        vocabularyCards = learningViewModel.vocabularyCards,
                        selectedLetter = selectedLetter,
                        onSelectLetter = { learningViewModel.selectLetter(it) },
                        ttsHelper = ttsHelper,
                        onAddStars = { count, name, category ->
                            mainViewModel.addStars(count, name, category)
                        }
                    )
                    2 -> AiStoriesScreen(
                        aiResponseState = aiResponseState,
                        savedStories = savedStories,
                        onAskQuestion = { aiTutorViewModel.askQuestion(it) },
                        onGenerateStory = { topic, charName -> aiTutorViewModel.generateStory(topic, charName) },
                        onDeleteStory = { aiTutorViewModel.deleteStory(it) },
                        ttsHelper = ttsHelper
                    )
                    3 -> GamesAndSongsScreen(
                        ttsHelper = ttsHelper,
                        onAddStars = { count, name, category ->
                            mainViewModel.addStars(count, name, category)
                        }
                    )
                    4 -> ParentScreen(
                        isUnlocked = isParentAreaUnlocked,
                        gateQuestionText = parentViewModel.gateQuestionText,
                        parentSettings = parentSettings,
                        progress = userProgress,
                        activityLogs = activityLogs,
                        onVerifyAnswer = { parentViewModel.verifyMathAnswer(it) },
                        onVerifyPin = { parentViewModel.verifyPin(it) },
                        onUpdateScreenTime = { parentViewModel.updateScreenTimeMinutes(it) },
                        onUpdateChildInfo = { name, age -> parentViewModel.updateChildName(name, age) },
                        onUpdatePin = { parentViewModel.updatePin(it) },
                        onToggleTimerEnabled = { parentViewModel.toggleTimerEnabled(it) },
                        onLockParentArea = { parentViewModel.lockParentArea() }
                    )
                }
            }
        }

        // Screen Lock Overlay for Kid Protection
        if (isScreenLocked) {
            ScreenLockOverlay(
                onUnlockWithPin = { pin ->
                    mainViewModel.unlockScreenWithPin(pin)
                }
            )
        }
    }
}
