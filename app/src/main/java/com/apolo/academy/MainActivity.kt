package com.apolo.academy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ApoloAcademyApp() }
    }
}

private enum class Tab { HOME, LESSONS, TUTOR }

@Composable
private fun ApoloAcademyApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("apolo_learning_progress", Context.MODE_PRIVATE) }
    var tab by remember { mutableStateOf(Tab.HOME) }
    var lessonOpen by remember { mutableStateOf<DrawingLesson?>(null) }
    var completed by remember { mutableStateOf(prefs.getStringSet("completed_ids", emptySet())?.toSet() ?: emptySet()) }
    var xp by remember { mutableStateOf(prefs.getInt("xp", 0)) }
    var streak by remember { mutableStateOf(prefs.getInt("streak", 0)) }
    val nextLesson = LessonCatalog.all.firstOrNull { !completed.contains(it.id.toString()) }

    fun finishLesson(lesson: DrawingLesson) {
        val key = lesson.id.toString()
        if (!completed.contains(key)) {
            val updated = completed + key
            completed = updated
            xp += 10
            val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
            val last = prefs.getString("last_study_day", "") ?: ""
            val yesterday = SimpleDateFormat("yyyyMMdd", Locale.US).format(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time)
            if (last != today) streak = if (last == yesterday) streak + 1 else 1
            prefs.edit().putStringSet("completed_ids", updated).putInt("xp", xp)
                .putInt("streak", streak).putString("last_study_day", today).apply()
        }
        lessonOpen = null
        tab = Tab.LESSONS
    }

    MaterialTheme {
        Scaffold(
            containerColor = Color(0xFFF4F6FB),
            bottomBar = {
                if (lessonOpen == null) NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(tab == Tab.HOME, { tab = Tab.HOME }, icon = { Icon(Icons.Default.Brush, null) }, label = { Text("Início") })
                    NavigationBarItem(tab == Tab.LESSONS, { tab = Tab.LESSONS }, icon = { Icon(Icons.Default.MenuBook, null) }, label = { Text("Trilhas") })
                    NavigationBarItem(tab == Tab.TUTOR, { tab = Tab.TUTOR }, icon = { Icon(Icons.Default.AutoAwesome, null) }, label = { Text("Tutor IA") })
                }
            }
        ) { padding ->
            if (lessonOpen != null) LessonDetailScreen(lessonOpen!!, completed.contains(lessonOpen!!.id.toString()), Modifier.padding(padding),
                { lessonOpen = null }, { finishLesson(it) })
            else when (tab) {
                Tab.HOME -> HomeScreen(completed.size, xp, streak, nextLesson, Modifier.padding(padding)) { lessonOpen = nextLesson }
                Tab.LESSONS -> LessonsScreen(completed, Modifier.padding(padding)) { lessonOpen = it }
                Tab.TUTOR -> TutorScreen(Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun HomeScreen(done: Int, xp: Int, streak: Int, next: DrawingLesson?, modifier: Modifier = Modifier, onStart: () -> Unit) {
    LazyColumn(modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Spacer(Modifier.height(10.dp))
            Text("APOLO ACADEMY", color = Color(0xFF667085), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Aprenda a desenhar.\nUm traço de cada vez.", fontSize = 29.sp, lineHeight = 35.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF17243A))
            Text("Sua jornada do básico ao avançado começa aqui.", color = Color(0xFF667085))
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF15243B)), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("SUA PRÓXIMA AULA", color = Color(0xFFFFD166), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(Modifier.height(7.dp))
                    Text(next?.title ?: "Você concluiu todas as aulas!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                    Text(next?.let { it.level + " · Aula " + it.levelNumber } ?: "Parabéns pela jornada completa!", color = Color(0xFFD5DEEC), fontSize = 13.sp)
                    Spacer(Modifier.height(13.dp))
                    Button(onClick = onStart, enabled = next != null, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC857), contentColor = Color(0xFF17243A))) {
                        Icon(Icons.Default.PlayArrow, null)
                        Text(if (done == 0) "Começar a aprender" else "Continuar aprendendo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Aulas", done.toString() + "/164", Modifier.weight(1f))
                StatCard("XP", xp.toString(), Modifier.weight(1f))
                StatCard("Dias", streak.toString(), Modifier.weight(1f))
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Sua evolução", fontWeight = FontWeight.Bold)
                        Text(((done / 164f) * 100).toInt().toString() + "%", color = Color(0xFF3478D4), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(progress = done / 164f, modifier = Modifier.fillMaxWidth().height(8.dp))
                    Text("Conclua cada aula para ganhar 10 XP e desbloquear a próxima.", fontSize = 12.sp, color = Color(0xFF667085))
                }
            }
        }
        item { Text("Sua trilha de aprendizado", fontWeight = FontWeight.Bold, fontSize = 20.sp) }
        item { LevelSummary("01", "Básico", "45 aulas · primeiros traços, formas, proporção e luz", Color(0xFF2E9D72), done.coerceAtMost(45), 45) }
        item { LevelSummary("02", "Intermediário", "54 aulas · perspectiva, volume, anatomia e composição", Color(0xFF3478D4), (done - 45).coerceIn(0, 54), 54) }
        item { LevelSummary("03", "Avançado", "65 aulas · anatomia, retrato, estilo e projetos autorais", Color(0xFF8A63C7), (done - 99).coerceIn(0, 65), 65) }
        item { Text("Vídeos de referência são abertos nos canais de seus criadores. O Apolo Academy organiza a trilha e oferece exercícios próprios.", color = Color(0xFF7A8495), fontSize = 11.sp) }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(15.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1D3557))
            Text(label, fontSize = 11.sp, color = Color(0xFF667085))
        }
    }
}

@Composable
private fun LevelSummary(number: String, title: String, description: String, color: Color, done: Int, total: Int) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = color.copy(alpha = .13f), shape = RoundedCornerShape(12.dp)) {
                Text(number, Modifier.padding(horizontal = 14.dp, vertical = 12.dp), color = color, fontWeight = FontWeight.ExtraBold)
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, color = Color(0xFF667085), fontSize = 12.sp)
                LinearProgressIndicator(progress = done.toFloat() / total, modifier = Modifier.fillMaxWidth().padding(top = 6.dp).height(5.dp), color = color)
                Text(done.toString() + " de " + total + " aulas concluídas", fontSize = 10.sp, color = Color(0xFF667085))
            }
        }
    }
}

@Composable
private fun LessonsScreen(completed: Set<String>, modifier: Modifier = Modifier, onOpen: (DrawingLesson) -> Unit) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Spacer(Modifier.height(12.dp))
            Text("Trilhas de desenho", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF17243A))
            Text("164 aulas. Complete uma para liberar a próxima.", color = Color(0xFF667085), fontSize = 13.sp)
            Spacer(Modifier.height(7.dp))
        }
        itemsIndexed(LessonCatalog.all) { index, lesson ->
            val isDone = completed.contains(lesson.id.toString())
            val unlocked = index == 0 || isDone || completed.contains(LessonCatalog.all[index - 1].id.toString())
            if (index == 0 || LessonCatalog.all[index - 1].level != lesson.level) {
                Column(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 3.dp)) {
                    Text(lesson.level.uppercase(), color = when (lesson.level) { "Básico" -> Color(0xFF2E9D72); "Intermediário" -> Color(0xFF3478D4); else -> Color(0xFF8A63C7) }, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
                    Text(LessonCatalog.all.count { it.level == lesson.level }.toString() + " aulas nesta etapa", color = Color(0xFF7A8495), fontSize = 11.sp)
                }
            }
            Card(modifier = Modifier.fillMaxWidth().clickable(enabled = unlocked) { onOpen(lesson) },
                colors = CardDefaults.cardColors(containerColor = if (isDone) Color(0xFFEAF6EF) else Color.White), shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = if (isDone) Color(0xFFD0EBDD) else Color(0xFFE8EEF8), shape = RoundedCornerShape(11.dp)) {
                        Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                            if (isDone) Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF25865C))
                            else if (!unlocked) Icon(Icons.Default.Lock, null, tint = Color(0xFF9AA3B2))
                            else Text(lesson.levelNumber.toString(), color = Color(0xFF31527B), fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(Modifier.weight(1f).padding(horizontal = 11.dp)) {
                        Text("Aula " + lesson.levelNumber, fontSize = 10.sp, color = Color(0xFF778397), fontWeight = FontWeight.Bold)
                        Text(lesson.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = if (unlocked) Color(0xFF17243A) else Color(0xFF929AA8))
                    }
                    if (isDone) Text("Feita", color = Color(0xFF25865C), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    else if (unlocked) Icon(Icons.Default.PlayArrow, null, tint = Color(0xFF3478D4))
                }
            }
        }
    }
}

@Composable
private fun LessonDetailScreen(lesson: DrawingLesson, isDone: Boolean, modifier: Modifier = Modifier, onBack: () -> Unit, onComplete: (DrawingLesson) -> Unit) {
    val context = LocalContext.current
    var answer by remember(lesson.id) { mutableStateOf(-1) }
    var checked by remember(lesson.id) { mutableStateOf(false) }
    val correct = answer == lesson.correctOption
    Column(modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") }
            Column {
                Text(lesson.level.uppercase() + " · AULA " + lesson.levelNumber, color = Color(0xFF3478D4), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("Lição de desenho", color = Color(0xFF667085), fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Star, null, tint = Color(0xFFE3A72F))
            Text("+10 XP", fontWeight = FontWeight.Bold, color = Color(0xFF9A6B13), fontSize = 12.sp)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(lesson.title, fontSize = 25.sp, lineHeight = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF17243A))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF1FC)), shape = RoundedCornerShape(17.dp)) {
                Column(Modifier.padding(15.dp)) {
                    Text("O QUE VOCÊ VAI APRENDER", color = Color(0xFF3478D4), fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(lesson.objective, color = Color(0xFF24344D), fontSize = 14.sp)
                }
            }
            Text("Prática guiada", fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Text("Pegue papel, lápis e borracha. Faça a atividade sem pressa e compare seu resultado com o primeiro esboço.", color = Color(0xFF667085), fontSize = 13.sp)
            Text(lesson.exercise, color = Color(0xFF27364D), fontSize = 14.sp)
            OutlinedCard(shape = RoundedCornerShape(15.dp), colors = CardDefaults.outlinedCardColors(containerColor = Color.White)) {
                Column(Modifier.padding(14.dp)) {
                    Text("TUTORIAL BRASILEIRO DE REFERÊNCIA", fontWeight = FontWeight.Bold, color = Color(0xFF17243A), fontSize = 12.sp)
                    Text("Referência: " + lesson.sourceName, color = Color(0xFF667085), fontSize = 12.sp)
                    Text("O vídeo abre no YouTube, no canal do criador. O aplicativo não copia nem hospeda o conteúdo dos autores.", color = Color(0xFF667085), fontSize = 11.sp)
                    TextButton(onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(lesson.sourceUrl))) } }) { Text("Assistir tutorial de referência ↗") }
                }
            }
            Text("Desafio rápido", fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Text(lesson.quizQuestion, color = Color(0xFF27364D), fontSize = 14.sp)
            lesson.options.forEachIndexed { index, option ->
                Row(Modifier.fillMaxWidth().clickable { answer = index; checked = false }, verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = answer == index, onClick = { answer = index; checked = false })
                    Text(option, color = Color(0xFF27364D), fontSize = 13.sp)
                }
            }
            if (checked) Text(if (correct) "Muito bem! Você acertou. A aula está pronta para ser concluída." else "Ainda não. Revise a explicação e tente novamente.", color = if (correct) Color(0xFF25865C) else Color(0xFFB45309), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
        }
        Surface(color = Color.White, shadowElevation = 8.dp) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                if (isDone) Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Aula já concluída · Voltar à trilha") }
                else if (checked && correct) Button(onClick = { onComplete(lesson) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25865C))) { Text("Concluir aula e ganhar 10 XP") }
                else Button(onClick = { checked = true }, enabled = answer >= 0, modifier = Modifier.fillMaxWidth()) { Text("Conferir resposta") }
            }
        }
    }
}

private data class ChatMessage(val text: String, val fromUser: Boolean)

@Composable
private fun TutorScreen(modifier: Modifier = Modifier) {
    var apiKey by remember { mutableStateOf("") }
    var prompt by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf(ChatMessage("Olá! Sou o Tutor Apolo. Posso explicar técnicas, propor exercícios e ajudar você a evoluir no desenho. Informe sua chave Gemini para começar.", false))) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFB7791F))
            Spacer(Modifier.width(8.dp))
            Text("Tutor Apolo IA", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        }
        Text("A chave fica apenas na memória desta tela. Não a compartilhe.", color = Color(0xFF64748B), fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Chave API do Google AI Studio") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(messages.size) { index ->
                val message = messages[index]
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start) {
                    Surface(color = if (message.fromUser) Color(0xFFE5EDFF) else Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.widthIn(max = 320.dp)) {
                        Text(message.text, Modifier.padding(12.dp), color = Color(0xFF172033))
                    }
                }
            }
            if (busy) item { Text("Tutor Apolo está pensando…", color = Color(0xFF64748B)) }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = prompt, onValueChange = { prompt = it }, modifier = Modifier.weight(1f), label = { Text("Pergunte sobre desenho") }, maxLines = 3)
            IconButton(enabled = !busy && prompt.isNotBlank() && apiKey.isNotBlank(), onClick = {
                val question = prompt.trim()
                val key = apiKey.trim()
                messages = messages + ChatMessage(question, true)
                prompt = ""
                error = null
                busy = true
                scope.launch {
                    try { messages = messages + ChatMessage(GeminiTutor.ask(key, question), false) }
                    catch (e: Exception) { error = e.message ?: "Não foi possível obter uma resposta." }
                    finally { busy = false }
                }
            }) { Icon(Icons.Default.Send, contentDescription = "Enviar pergunta") }
        }
    }
}

private object GeminiTutor {
    suspend fun ask(apiKey: String, question: String): String = withContext(Dispatchers.IO) {
        require(apiKey.isNotBlank()) { "Informe uma chave da API Gemini." }
        val connection = (URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 20000
            readTimeout = 60000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("x-goog-api-key", apiKey)
        }
        try {
            val instruction = "Você é Tutor Apolo, professor paciente e especializado em desenho. Responda em português brasileiro. Ensine do básico ao avançado com explicações claras, exercícios graduais, materiais, observação, perspectiva, luz e sombra, anatomia e composição. Nunca finja ter visto uma imagem que não recebeu."
            val parts = JSONArray().put(JSONObject().put("text", instruction + "\n\nPergunta do aluno: " + question))
            val body = JSONObject().put("contents", JSONArray().put(JSONObject().put("role", "user").put("parts", parts))).put("generationConfig", JSONObject().put("temperature", 0.7).put("maxOutputTokens", 1200))
            connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val response = stream.bufferedReader().use { it.readText() }
            if (status !in 200..299) {
                val detail = runCatching { JSONObject(response).optJSONObject("error")?.optString("message") }.getOrNull()
                throw IllegalStateException("Gemini HTTP " + status + ". " + (detail ?: "Confira a chave, o acesso ao modelo e a conexão."))
            }
            val candidates = JSONObject(response).optJSONArray("candidates") ?: throw IllegalStateException("A IA não retornou candidatos.")
            val content = candidates.optJSONObject(0)?.optJSONObject("content") ?: throw IllegalStateException("Resposta vazia da IA.")
            val responseParts = content.optJSONArray("parts") ?: throw IllegalStateException("Resposta sem texto.")
            val answer = buildString { for (index in 0 until responseParts.length()) append(responseParts.optJSONObject(index)?.optString("text").orEmpty()) }.trim()
            answer.ifBlank { "Não recebi texto do Tutor Apolo. Tente novamente." }
        } finally { connection.disconnect() }
    }
}
