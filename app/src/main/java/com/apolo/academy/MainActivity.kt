package com.apolo.academy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        Scaffold(
            containerColor = Color(0xFFF5F6FA),
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(selected == Tab.HOME, { selected = Tab.HOME }, icon = { Icon(Icons.Default.Brush, null) }, label = { Text("Início") })
                    NavigationBarItem(selected == Tab.LESSONS, { selected = Tab.LESSONS }, icon = { Icon(Icons.Default.MenuBook, null) }, label = { Text("Aulas") })
                    NavigationBarItem(selected == Tab.TUTOR, { selected = Tab.TUTOR }, icon = { Icon(Icons.Default.AutoAwesome, null) }, label = { Text("Tutor IA") })
                }
            }
        ) { padding ->
            when (selected) {
                Tab.HOME -> HomeScreen(navy, gold, Modifier.padding(padding))
                Tab.LESSONS -> LessonsScreen(Modifier.padding(padding))
                Tab.TUTOR -> TutorScreen(Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun HomeScreen(navy: Color, gold: Color, modifier: Modifier = Modifier) {
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
                    Button(onClick = { }) { Text("Começar aula") }
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
        LevelCard("01", "Desenho para iniciantes", "Controle do traço, formas e observação", "Fundamentos")
        LevelCard("02", "Perspectiva e profundidade", "Pontos de fuga, caixas e ambientes", "Intermediário")
        LevelCard("03", "Figura humana", "Proporções, gesto e anatomia artística", "Avançado")
        Text("A biblioteca internacional e os links de referência serão adicionados em uma próxima etapa.", fontSize = 12.sp, color = Color(0xFF64748B))
    }
}

@Composable
private fun TutorScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFB7791F))
        Text("Tutor Apolo", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Seu espaço para tirar dúvidas sobre técnicas, materiais e exercícios de desenho.")
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Text("O tutor está em modo demonstrativo. A conexão com IA será feita por uma API segura, sem armazenar chaves secretas no aplicativo.", Modifier.padding(18.dp), color = Color(0xFF475569))
        }
    }
}
