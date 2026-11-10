package com.apolo.academy
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ApoloAcademyApp() }
    }
}
private enum class Tab { HOME, LESSONS, TUTOR }

@Composable
private fun ApoloAcademyApp() {
    var selected by remember { mutableStateOf(Tab.HOME) }
    val navy = Color(0xFF101827)
    val gold = Color(0xFFFFC857)
    MaterialTheme {
        Scaffold(containerColor = Color(0xFFF5F6FA), bottomBar = {
            NavigationBar {
                NavigationBarItem(selected == Tab.HOME, { selected = Tab.HOME }, icon = { Icon(Icons.Default.Brush, null) }, label = { Text("Início") })
                NavigationBarItem(selected == Tab.LESSONS, { selected = Tab.LESSONS }, icon = { Icon(Icons.Default.MenuBook, null) }, label = { Text("Aulas") })
                NavigationBarItem(selected == Tab.TUTOR, { selected = Tab.TUTOR }, icon = { Icon(Icons.Default.AutoAwesome, null) }, label = { Text("Tutor IA") })
            }
        }) { padding ->
            when (selected) {
                Tab.HOME -> HomeScreen(navy, gold, Modifier.padding(padding)) { selected = Tab.LESSONS }
                Tab.LESSONS -> LessonsScreen(Modifier.padding(padding))
                Tab.TUTOR -> TutorScreen(Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun HomeScreen(navy: Color, gold: Color, modifier: Modifier = Modifier, onStartLesson: () -> Unit) {
    LazyColumn(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("APOLO ACADEMY", color = Color(0xFF64748B), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Text("Sua jornada artística começa aqui.", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = navy)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = navy), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(22.dp)) {
                    Text("Continue aprendendo", color = gold, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Fundamentos do desenho", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Aula 1 · Linhas, formas e observação", color = Color(0xFFD5DCE8))
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onStartLesson) { Text("Começar aula") }
                }
            }
        }
        item { Text("Sua trilha", fontSize = 21.sp, fontWeight = FontWeight.Bold) }
        item { LevelCard("01", "Iniciante", "Linhas, formas básicas e proporção", "12 aulas") }
        item { LevelCard("02", "Intermediário", "Perspectiva, luz, sombra e volume", "18 aulas") }
        item { LevelCard("03", "Avançado", "Anatomia, composição e estilo", "20 aulas") }
    }
}
@Composable
private fun LevelCard(number: String, title: String, description: String, count: String) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFFFFF1CC), shape = RoundedCornerShape(12.dp)) {
                Text(number, Modifier.padding(horizontal = 14.dp, vertical = 12.dp), color = Color(0xFF8A5A00), fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f).padding(start = 14.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(description, color = Color(0xFF64748B), fontSize = 13.sp)
                Text(count, color = Color(0xFF64748B), fontSize = 12.sp)
            }
        }
    }
}
@Composable
private fun LessonsScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Biblioteca de aulas", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Aprenda no seu ritmo, do primeiro traço às técnicas avançadas.", color = Color(0xFF64748B))
        LessonRow("01", "Desenho para iniciantes", "Controle do traço, formas e observação", "Fundamentos")
        LessonRow("02", "Perspectiva e profundidade", "Pontos de fuga, caixas e ambientes", "Intermediário")
        LessonRow("03", "Figura humana", "Proporções, gesto e anatomia artística", "Avançado")
        Text("A biblioteca internacional completa será ampliada em próximas versões.", fontSize = 12.sp, color = Color(0xFF64748B))
    }
}
@Composable
private fun LessonRow(number: String, title: String, description: String, level: String) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("$number · $level", color = Color(0xFF9A6700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(description, color = Color(0xFF64748B), fontSize = 13.sp)
        }
    }
}
private data class ChatMessage(val text: String, val fromUser: Boolean)

@Composable
private fun TutorScreen(modifier: Modifier = Modifier) {
    var apiKey by remember { mutableStateOf("") }
    var prompt by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf(ChatMessage("Olá! Sou o Tutor Apolo. Posso ensinar técnicas, criar exercícios e ajudar com seu processo de desenho. Informe sua chave Gemini para começar.", false))) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFB7791F))
            Spacer(Modifier.width(8.dp))
            Text("Tutor Apolo IA", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text("Conectado ao Google Gemini. A chave fica apenas na memória desta tela e não é salva no aplicativo.", color = Color(0xFF64748B), fontSize = 13.sp)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Chave API do Google AI Studio") }, placeholder = { Text("Cole sua chave Gemini") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(messages) { message ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start) {
                    Surface(color = if (message.fromUser) Color(0xFFE5EDFF) else Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.widthIn(max = 320.dp)) {
                        Text(message.text, Modifier.padding(12.dp), color = Color(0xFF172033))
                    }
                }
            }
            if (busy) item { Text("Tutor Apolo está pensando…", color = Color(0xFF64748B)) }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = prompt, onValueChange = { prompt = it }, modifier = Modifier.weight(1f), label = { Text("Pergunte sobre desenho") }, maxLines = 3)
            Spacer(Modifier.width(8.dp))
            IconButton(enabled = !busy && prompt.isNotBlank() && apiKey.isNotBlank(), onClick = {
                val question = prompt.trim()
                val key = apiKey.trim()
                messages = messages + ChatMessage(question, true)
                prompt = ""
                error = null
                busy = true
                scope.launch {
                    try {
                        val answer = GeminiTutor.ask(key, question)
                        messages = messages + ChatMessage(answer, false)
                    } catch (e: Exception) {
                        error = e.message ?: "Não foi possível obter uma resposta da IA."
                    } finally {
                        busy = false
                    }
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
            val instruction = "Você é Tutor Apolo, professor paciente e especializado em desenho. Responda em português brasileiro. Ensine do básico ao avançado com explicações claras, exercícios graduais, materiais, observação, perspectiva, luz e sombra, anatomia e composição. Se pedirem correção sem imagem, peça que descrevam o desenho. Nunca finja ter visto uma imagem que não recebeu."
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
            val answer = buildString {
                for (index in 0 until responseParts.length()) append(responseParts.optJSONObject(index)?.optString("text").orEmpty())
            }.trim()
            answer.ifBlank { "Não recebi texto do Tutor Apolo. Tente novamente." }
        } finally {
            connection.disconnect()
        }
    }
}
