package com.yashpawar.hopeai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yashpawar.hopeai.data.AuthSession
import com.yashpawar.hopeai.data.ChatMessage
import com.yashpawar.hopeai.data.HopeBackendClient
import com.yashpawar.hopeai.data.HopeLanguage
import com.yashpawar.hopeai.data.HopeStore
import com.yashpawar.hopeai.data.MemoryItem
import com.yashpawar.hopeai.data.NetworkResult
import com.yashpawar.hopeai.data.TaskItem
import com.yashpawar.hopeai.notifications.NotificationCenter
import com.yashpawar.hopeai.ui.ElectricBlue
import com.yashpawar.hopeai.ui.HopeTheme
import com.yashpawar.hopeai.ui.HopeViolet
import com.yashpawar.hopeai.ui.Midnight
import com.yashpawar.hopeai.ui.MutedText
import com.yashpawar.hopeai.ui.SurfaceNavy
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { HopeTheme { HopeApp() } }
    }
}

private enum class Route(val value: String, val label: String) {
    HOME("home", "Home"), CHAT("chat", "Chat"), VOICE("voice", "Voice"), TASKS("tasks", "Tasks"), SETTINGS("settings", "Settings"),
    LOGIN("login", "Login"), MEMORY("memory", "Memory"), ABOUT("about", "About")
}

private val mainRoutes = listOf(Route.HOME, Route.CHAT, Route.VOICE, Route.TASKS, Route.SETTINGS)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HopeApp() {
    val context = LocalContext.current
    val store = remember { HopeStore(context) }
    val backend = remember { HopeBackendClient() }
    val nav = rememberNavController()
    var demoMode by remember { mutableStateOf(!backend.supabaseConfigured) }
    var session by remember { mutableStateOf(store.session) }
    val start = if (session != null || demoMode) Route.HOME.value else Route.LOGIN.value
    val backStack by nav.currentBackStackEntryAsState()
    val destination = backStack?.destination
    val current = Route.entries.firstOrNull { it.value == destination?.route }
    val showShell = current in mainRoutes

    Scaffold(
        containerColor = Midnight,
        topBar = {
            if (showShell) TopAppBar(
                title = { Text("HOPE AI", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Midnight, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            if (showShell) NavigationBar(containerColor = SurfaceNavy) {
                mainRoutes.forEach { route ->
                    NavigationBarItem(
                        selected = destination?.hierarchy?.any { it.route == route.value } == true,
                        onClick = { nav.navigate(route.value) { popUpTo(nav.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                        icon = { Icon(routeIcon(route), contentDescription = route.label) },
                        label = { Text(route.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = start, modifier = Modifier.padding(padding)) {
            composable(Route.LOGIN.value) {
                LoginScreen(backend, onAuthenticated = { newSession -> session = newSession; store.session = newSession; nav.navigate(Route.HOME.value) { popUpTo(Route.LOGIN.value) { inclusive = true } } }, onDemo = { demoMode = true; nav.navigate(Route.HOME.value) { popUpTo(Route.LOGIN.value) { inclusive = true } } })
            }
            composable(Route.HOME.value) { HomeScreen(store.displayName, backend.aiConfigured, demoMode, navigate = { nav.navigate(it.value) }) }
            composable(Route.CHAT.value) { ChatScreen(store, backend, session) }
            composable(Route.VOICE.value) { VoiceScreen(store, backend, session) }
            composable(Route.TASKS.value) { TasksScreen(store) }
            composable(Route.SETTINGS.value) { SettingsScreen(store, backend, demoMode, onMemory = { nav.navigate(Route.MEMORY.value) }, onAbout = { nav.navigate(Route.ABOUT.value) }, onLogout = { store.session = null; session = null; demoMode = false; nav.navigate(Route.LOGIN.value) { popUpTo(nav.graph.id) { inclusive = true } } }) }
            composable(Route.MEMORY.value) { MemoryScreen(store) }
            composable(Route.ABOUT.value) { AboutScreen() }
        }
    }
}

@Composable
private fun LoginScreen(backend: HopeBackendClient, onAuthenticated: (AuthSession) -> Unit, onDemo: () -> Unit) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    ScreenContainer {
        HopeLogo(150)
        Text("Welcome to HOPE AI", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Your personal AI assistant", color = MutedText)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(password, { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
        if (message.isNotBlank()) Text(message, color = if (message.startsWith("Account")) ElectricBlue else Color(0xFFFFA6A6))
        Button(enabled = !loading && email.isNotBlank() && password.length >= 6, onClick = {
            scope.launch { loading = true; when (val result = backend.signIn(email, password)) { is NetworkResult.Success -> onAuthenticated(result.value); is NetworkResult.Failure -> message = result.message }; loading = false }
        }, modifier = Modifier.fillMaxWidth()) { if (loading) CircularProgressIndicator(Modifier.size(20.dp)) else Text("Sign in") }
        OutlinedButton(enabled = !loading && email.isNotBlank() && password.length >= 6, onClick = {
            scope.launch { loading = true; message = when (val result = backend.signUp(email, password)) { is NetworkResult.Success -> result.value; is NetworkResult.Failure -> result.message }; loading = false }
        }, modifier = Modifier.fillMaxWidth()) { Text("Create account") }
        OutlinedButton(onClick = onDemo, modifier = Modifier.fillMaxWidth()) { Text("Continue in local demo") }
        if (!backend.supabaseConfigured) Text("Supabase is not configured in this build. Local demo remains available.", color = MutedText, textAlign = TextAlign.Center)
    }
}

@Composable
private fun HomeScreen(name: String, aiConfigured: Boolean, demoMode: Boolean, navigate: (Route) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HopeLogo(110)
                Spacer(Modifier.width(14.dp))
                Column { Text("Hi, $name", fontSize = 28.sp, fontWeight = FontWeight.Bold); Text(if (aiConfigured) "HOPE is ready" else "Connect the AI backend to start", color = if (aiConfigured) ElectricBlue else MutedText) }
            }
        }
        item { StatusCard("Mode", if (demoMode) "Local demo" else "Signed in", if (aiConfigured) "AI connected" else "AI configuration required") }
        items(listOf(Route.VOICE to "Talk naturally with HOPE", Route.CHAT to "Continue with text", Route.TASKS to "Plan tasks and reminders", Route.MEMORY to "Control what HOPE remembers")) { (route, subtitle) ->
            ActionCard(route.label, subtitle) { navigate(route) }
        }
    }
}

@Composable
private fun ChatScreen(store: HopeStore, backend: HopeBackendClient, session: AuthSession?) {
    val scope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf(ChatMessage(role = "assistant", content = "Hello. I am HOPE. How can I help you today?")) }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(messages, key = { it.id }) { message -> ChatBubble(message) }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(input, { input = it }, label = { Text("Message HOPE") }, modifier = Modifier.weight(1f), enabled = !loading)
            IconButton(enabled = input.isNotBlank() && !loading, onClick = {
                val text = input.trim(); input = ""; messages += ChatMessage(role = "user", content = text); loading = true
                scope.launch {
                    val language = if (store.language == HopeLanguage.AUTO) com.yashpawar.hopeai.data.LanguagePolicy.detect(text) else store.language
                    when (val result = backend.chat(text, language, session?.accessToken)) {
                        is NetworkResult.Success -> messages += ChatMessage(role = "assistant", content = result.value)
                        is NetworkResult.Failure -> messages += ChatMessage(role = "assistant", content = result.message)
                    }
                    loading = false
                }
            }) { if (loading) CircularProgressIndicator(Modifier.size(22.dp)) else Icon(Icons.Default.Send, "Send") }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (message.role == "user") Arrangement.End else Arrangement.Start) {
        Card(colors = CardDefaults.cardColors(containerColor = if (message.role == "user") HopeViolet.copy(alpha = .35f) else SurfaceNavy), shape = RoundedCornerShape(18.dp)) {
            Text(message.content, modifier = Modifier.padding(14.dp).fillMaxWidth(.86f))
        }
    }
}

@Composable
private fun VoiceScreen(store: HopeStore, backend: HopeBackendClient, session: AuthSession?) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf("Waiting") }
    var transcript by remember { mutableStateOf("Tap the microphone and speak") }
    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember { TextToSpeech(context) { ttsReady = it == TextToSpeech.SUCCESS } }
    val recognizer = remember { if (SpeechRecognizer.isRecognitionAvailable(context)) SpeechRecognizer.createSpeechRecognizer(context) else null }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (!granted) state = "Microphone permission denied" }

    DisposableEffect(Unit) { onDispose { recognizer?.destroy(); tts.stop(); tts.shutdown() } }
    LaunchedEffect(ttsReady, store.language) {
        if (ttsReady) tts.language = when (store.language) { HopeLanguage.HINDI -> Locale("hi", "IN"); else -> Locale("en", "IN") }
    }
    val listener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { state = "Listening" }
            override fun onBeginningOfSpeech() { state = "Listening" }
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() { state = "Thinking" }
            override fun onError(error: Int) { state = "Could not hear you. Try again." }
            override fun onResults(results: Bundle?) {
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                if (text.isBlank()) { state = "No speech detected"; return }
                transcript = text; state = "Thinking"
                scope.launch {
                    val language = if (store.language == HopeLanguage.AUTO) com.yashpawar.hopeai.data.LanguagePolicy.detect(text) else store.language
                    when (val result = backend.chat(text, language, session?.accessToken)) {
                        is NetworkResult.Success -> { transcript = result.value; state = "Speaking"; if (ttsReady) tts.speak(result.value, TextToSpeech.QUEUE_FLUSH, null, "hope_reply") }
                        is NetworkResult.Failure -> { transcript = result.message; state = "Configuration required" }
                    }
                }
            }
            override fun onPartialResults(partialResults: Bundle?) { partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let { transcript = it } }
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        }
    }
    LaunchedEffect(recognizer, listener) { recognizer?.setRecognitionListener(listener) }

    ScreenContainer {
        PulsingAvatar(state)
        Text(state, color = ElectricBlue, fontWeight = FontWeight.SemiBold)
        Text(transcript, textAlign = TextAlign.Center, fontSize = 18.sp)
        FloatingActionButton(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) permission.launch(Manifest.permission.RECORD_AUDIO)
            else recognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply { putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true); putExtra(RecognizerIntent.EXTRA_LANGUAGE, when (store.language) { HopeLanguage.HINDI -> "hi-IN"; else -> "en-IN" }) })
        }, containerColor = HopeViolet) { Icon(Icons.Default.Mic, "Start listening") }
        Text("The production Gemini Live full-duplex session activates after the secure backend is connected.", color = MutedText, textAlign = TextAlign.Center, fontSize = 13.sp)
    }
}

@Composable
private fun TasksScreen(store: HopeStore) {
    val context = LocalContext.current
    val tasks = remember { mutableStateListOf<TaskItem>().apply { addAll(store.loadTasks()) } }
    var title by remember { mutableStateOf("") }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    fun persist() = store.saveTasks(tasks)
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(title, { title = it }, label = { Text("New task") }, modifier = Modifier.weight(1f))
            IconButton(enabled = title.isNotBlank(), onClick = { tasks += TaskItem(title = title.trim()); title = ""; persist() }) { Icon(Icons.Default.Add, "Add task") }
        }
        OutlinedButton(onClick = {
            if (android.os.Build.VERSION.SDK_INT >= 33) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            NotificationCenter.show(context, "HOPE reminder", "Your notification test is working.")
        }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Notifications, null); Spacer(Modifier.width(8.dp)); Text("Test notification") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks, key = { it.id }) { task ->
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceNavy)) { Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { val i = tasks.indexOf(task); tasks[i] = task.copy(completed = !task.completed); persist() }) { Icon(Icons.Default.CheckCircle, "Toggle task", tint = if (task.completed) ElectricBlue else MutedText) }
                    Text(task.title, modifier = Modifier.weight(1f), color = if (task.completed) MutedText else Color.White)
                    IconButton(onClick = { tasks.remove(task); persist() }) { Icon(Icons.Default.Delete, "Delete task") }
                } }
            }
        }
    }
}

@Composable
private fun MemoryScreen(store: HopeStore) {
    val memories = remember { mutableStateListOf<MemoryItem>().apply { addAll(store.loadMemories()) } }
    var input by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Memory Manager", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("These entries stay on this device until your secure Mem0 backend is connected.", color = MutedText)
        OutlinedTextField(input, { input = it }, label = { Text("Something HOPE should remember") }, modifier = Modifier.fillMaxWidth())
        Button(enabled = input.isNotBlank(), onClick = { memories += MemoryItem(content = input.trim()); input = ""; store.saveMemories(memories) }, modifier = Modifier.fillMaxWidth()) { Text("Save memory") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { items(memories, key = { it.id }) { memory -> Card(colors = CardDefaults.cardColors(containerColor = SurfaceNavy)) { Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text(memory.content, Modifier.weight(1f)); IconButton(onClick = { memories.remove(memory); store.saveMemories(memories) }) { Icon(Icons.Default.Delete, "Delete memory") } } } } }
    }
}

@Composable
private fun SettingsScreen(store: HopeStore, backend: HopeBackendClient, demoMode: Boolean, onMemory: () -> Unit, onAbout: () -> Unit, onLogout: () -> Unit) {
    var name by remember { mutableStateOf(store.displayName) }
    var language by remember { mutableStateOf(store.language) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold) }
        item { OutlinedTextField(name, { name = it; store.displayName = it }, label = { Text("Preferred name") }, modifier = Modifier.fillMaxWidth()) }
        item { Text("Language", fontWeight = FontWeight.SemiBold) }
        items(HopeLanguage.entries) { option -> OutlinedButton(onClick = { language = option; store.language = option }, modifier = Modifier.fillMaxWidth()) { Text(if (language == option) "${option.label} — selected" else option.label) } }
        item { StatusCard("Connection", if (backend.supabaseConfigured) "Supabase configured" else "Supabase not configured", if (backend.aiConfigured) "AI backend configured" else "AI backend not configured") }
        item { ActionCard("Memory Manager", "View and control saved memory", onMemory) }
        item { ActionCard("About HOPE", "Version and creator information", onAbout) }
        item { OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text(if (demoMode) "Exit local demo" else "Sign out") } }
    }
}

@Composable
private fun AboutScreen() = ScreenContainer {
    HopeLogo(160)
    Text("HOPE AI V2.0", fontSize = 30.sp, fontWeight = FontWeight.Bold)
    Text("Created by Yash Pawar", color = ElectricBlue)
    Text("A multilingual personal AI assistant built for Android.", color = MutedText, textAlign = TextAlign.Center)
}

@Composable
private fun ScreenContainer(content: @Composable ColumnScope.() -> Unit) = Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Midnight, Color(0xFF0B1030)))).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, content = content)

@Composable
private fun HopeLogo(size: Int) = Image(painterResource(R.drawable.hope_logo), contentDescription = "HOPE AI logo", modifier = Modifier.size(size.dp).clip(RoundedCornerShape(28.dp)), contentScale = ContentScale.Crop)

@Composable
private fun PulsingAvatar(state: String) {
    val transition = rememberInfiniteTransition(label = "hopePulse")
    val alpha by transition.animateFloat(.65f, 1f, infiniteRepeatable(tween(if (state == "Speaking") 450 else 1200), RepeatMode.Reverse), label = "avatarAlpha")
    Box(Modifier.size(210.dp).alpha(alpha).background(Brush.radialGradient(listOf(ElectricBlue.copy(.65f), HopeViolet.copy(.3f), Color.Transparent)), CircleShape), contentAlignment = Alignment.Center) { HopeLogo(155) }
}

@Composable
private fun StatusCard(title: String, first: String, second: String) = Card(colors = CardDefaults.cardColors(containerColor = SurfaceNavy), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text(title, fontWeight = FontWeight.Bold); Text(first, color = ElectricBlue); Text(second, color = MutedText) } }

@Composable
private fun ActionCard(title: String, subtitle: String, onClick: () -> Unit) = Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = SurfaceNavy), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text(subtitle, color = MutedText) }; Icon(Icons.Default.Add, null, tint = ElectricBlue) } }

private fun routeIcon(route: Route): ImageVector = when (route) { Route.HOME -> Icons.Default.Home; Route.CHAT -> Icons.Default.Chat; Route.VOICE -> Icons.Default.Mic; Route.TASKS -> Icons.Default.List; Route.SETTINGS -> Icons.Default.Settings; Route.MEMORY -> Icons.Default.Memory; Route.ABOUT -> Icons.Default.Info; else -> Icons.Default.Home }
