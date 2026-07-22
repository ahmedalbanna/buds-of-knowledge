package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ActivityLogEntity
import com.example.data.entity.ParentSettingsEntity
import com.example.data.entity.UserProgressEntity

@Composable
fun ParentScreen(
    isUnlocked: Boolean,
    gateQuestionText: String,
    parentSettings: ParentSettingsEntity,
    progress: UserProgressEntity,
    activityLogs: List<ActivityLogEntity>,
    onVerifyAnswer: (String) -> Boolean,
    onVerifyPin: (String) -> Boolean,
    onUpdateScreenTime: (Int) -> Unit,
    onUpdateChildInfo: (String, String) -> Unit,
    onUpdatePin: (String) -> Unit,
    onToggleTimerEnabled: (Boolean) -> Unit,
    onLockParentArea: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isUnlocked) {
        ParentGateView(
            gateQuestionText = gateQuestionText,
            onVerifyAnswer = onVerifyAnswer,
            onVerifyPin = onVerifyPin,
            modifier = modifier
        )
    } else {
        ParentDashboardView(
            parentSettings = parentSettings,
            progress = progress,
            activityLogs = activityLogs,
            onUpdateScreenTime = onUpdateScreenTime,
            onUpdateChildInfo = onUpdateChildInfo,
            onUpdatePin = onUpdatePin,
            onToggleTimerEnabled = onToggleTimerEnabled,
            onLockParentArea = onLockParentArea,
            modifier = modifier
        )
    }
}

@Composable
fun ParentGateView(
    gateQuestionText: String,
    onVerifyAnswer: (String) -> Boolean,
    onVerifyPin: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var answerInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var usePinMode by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "منطقة إشراف الوالدين 👨‍👩‍👧",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = if (usePinMode) "أدخل الرمز السري للوالدين (الافتراضي 1234)" else "يرجى حل المسألة للتأكد من دخول ولي الأمر:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (!usePinMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF8E1), shape = RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = gateQuestionText,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = answerInput,
                        onValueChange = { answerInput = it },
                        label = { Text("الناتج") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_gate_math_input")
                    )
                } else {
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("رمز PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_gate_pin_input")
                    )
                }

                if (errorText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorText, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val success = if (usePinMode) onVerifyPin(pinInput) else onVerifyAnswer(answerInput)
                        if (!success) {
                            errorText = "إجابة أو رمز غير صحيح!"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("parent_gate_submit_button")
                ) {
                    Text("دخول منطقة الوالدين", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        usePinMode = !usePinMode
                        errorText = ""
                    },
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(if (usePinMode) "استخدم السؤال الحسابي" else "استخدم الرمز السري PIN")
                }
            }
        }
    }
}

@Composable
fun ParentDashboardView(
    parentSettings: ParentSettingsEntity,
    progress: UserProgressEntity,
    activityLogs: List<ActivityLogEntity>,
    onUpdateScreenTime: (Int) -> Unit,
    onUpdateChildInfo: (String, String) -> Unit,
    onUpdatePin: (String) -> Unit,
    onToggleTimerEnabled: (Boolean) -> Unit,
    onLockParentArea: () -> Unit,
    modifier: Modifier = Modifier
) {
    var screenTimeSlider by remember { mutableFloatStateOf(parentSettings.dailyScreenTimeMinutes.toFloat()) }
    var childNameInput by remember { mutableStateOf(parentSettings.childName) }
    var ageGroupInput by remember { mutableStateOf(parentSettings.childAgeGroup) }
    var newPinInput by remember { mutableStateOf("") }
    var pinSuccessText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "منطقة إشراف الوالدين 👨‍👩‍👧",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "لوحة التحكم وإدارة وقت الشاشة والتقدم", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Button(
                    onClick = onLockParentArea,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("قفل")
                }
            }
        }

        // Screen Time Management Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
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
                            Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "تحديد وقت الشاشة اليومي ⏳", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }

                        Switch(
                            checked = parentSettings.isTimerEnabled,
                            onCheckedChange = { onToggleTimerEnabled(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "الحد اليومي المحدد: ${screenTimeSlider.toInt()} دقيقة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Slider(
                        value = screenTimeSlider,
                        onValueChange = { screenTimeSlider = it },
                        valueRange = 10f..120f,
                        steps = 10,
                        modifier = Modifier.testTag("screen_time_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "10 دقائق", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "120 دقيقة (ساعتان)", fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onUpdateScreenTime(screenTimeSlider.toInt()) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("حفظ حد وقت الشاشة 💾", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Child Profile Settings
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "ملف الطفل والرمز السري 👦", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = childNameInput,
                        onValueChange = { childNameInput = it },
                        label = { Text("اسم الطفل") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ageGroupInput,
                        onValueChange = { ageGroupInput = it },
                        label = { Text("المرحلة التعليمية (تمهيدي / ابتدائي)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onUpdateChildInfo(childNameInput, ageGroupInput) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("حفظ معلومات الطفل ✏️")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "تغيير رمز PIN للوالدين:", fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { newPinInput = it },
                        label = { Text("رمز PIN الجديد (4 أرقام)") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (pinSuccessText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = pinSuccessText, color = Color(0xFF2E7D32), fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (newPinInput.length >= 4) {
                                onUpdatePin(newPinInput)
                                pinSuccessText = "تم تحديث رمز PIN بنجاح! 🎉"
                                newPinInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("تحديث رمز PIN 🔐")
                    }
                }
            }
        }

        // Child Activity Analytics
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "ملخص إنجازات الطفل 📊", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⭐ ${progress.starsCount}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            Text(text = "مجموع النجوم", fontSize = 12.sp, color = Color.Gray)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔥 ${progress.dailyStreakDays}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))
                            Text(text = "أيام الاستمرار", fontSize = 12.sp, color = Color.Gray)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⏱️ ${parentSettings.timeUsedTodaySeconds / 60}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = "دقائق الاستخدام اليوم", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // Parent Guidance Tip
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "💡 نصيحة تربوية لاستخدام آمن للتكنولوجيا:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• شجّع طفلك على مشاركتك القصص والإجابات التي يتعلمها من الذكاء الاصطناعي.\n• احرص على الجمع بين التعلم الرقمي والألعاب الحركية الخارجية.\n• يُنصح بألا تتجاوز جلسة التعلم 30 دقيقة متواصلة لحماية النظر والتشتت.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                        color = Color(0xFF1B5E20)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
