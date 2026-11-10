package com.apolo.academy

import android.net.Uri

data class DrawingLesson(
    val id: Int, val level: String, val levelNumber: Int, val title: String,
    val objective: String, val exercise: String, val quizQuestion: String,
    val options: List<String>, val correctOption: Int, val sourceName: String, val sourceUrl: String
)

object LessonCatalog {
    private val basicTitles = listOf(
        "Conhecendo os materiais de desenho",
        "Como segurar o lápis e posicionar a mão",
        "Postura, apoio e movimento do braço",
        "Aquecimento: linhas retas e paralelas",
        "Linhas curvas e onduladas com controle",
        "Círculos feitos à mão livre",
        "Elipses e formas ovais",
        "Formas geométricas fundamentais",
        "Contorno e desenho de observação",
        "Espaço negativo: enxergando os vazios",
        "Como observar antes de desenhar",
        "Medidas visuais e comparação de tamanhos",
        "Proporção entre partes de um objeto",
        "Simplificando objetos em formas básicas",
        "Primeiro estudo de natureza-morta",
        "Contornos suaves e contornos marcados",
        "Luz: identificando a direção da iluminação",
        "Escala tonal do claro ao escuro",
        "Hachuras para criar valores",
        "Hachuras cruzadas e textura",
        "Sombra própria e sombra projetada",
        "Desenhando uma esfera com volume",
        "Desenhando um cubo em perspectiva",
        "Desenhando cilindros e cones",
        "Combinando sólidos geométricos",
        "Noções de linha do horizonte",
        "Ponto de fuga: primeira perspectiva",
        "Perspectiva de um ponto em objetos simples",
        "Construindo um esboço antes do acabamento",
        "Linhas de construção e linhas finais",
        "Como usar a borracha sem danificar o papel",
        "Texturas básicas com lápis grafite",
        "Diferenças entre lápis HB, 2B e 6B",
        "Escolhendo papel e materiais acessíveis",
        "Composição simples: organizando a página",
        "Miniaturas para planejar um desenho",
        "Desenhando folhas e elementos da natureza",
        "Desenhando um animal usando formas básicas",
        "Desenhando uma casa passo a passo",
        "Introdução às proporções do rosto",
        "Olhos, nariz e boca em formas simples",
        "Mãos: observação e formas simplificadas",
        "Como corrigir proporções no próprio esboço",
        "Projeto: natureza-morta com luz e sombra",
        "Revisão do nível básico e desafio final"
    )
    private val intermediateTitles = listOf(
        "Perspectiva de dois pontos de fuga",
        "Introdução à perspectiva de três pontos",
        "Elipses em perspectiva",
        "Caixas e objetos em diferentes ângulos",
        "Construção de objetos por planos",
        "Sobreposição e profundidade espacial",
        "Encurtamento: objetos apontados para a câmera",
        "Proporção usando relações e unidades",
        "Medição comparativa em desenhos complexos",
        "Luz e sombra em formas combinadas",
        "Valores tonais para criar profundidade",
        "Reflexos e luz rebatida",
        "Materiais: madeira, metal e vidro",
        "Texturas realistas com grafite",
        "Composição com ponto focal",
        "Regra dos terços e equilíbrio visual",
        "Perspectiva de interiores",
        "Perspectiva de ruas e edifícios",
        "Paisagem com planos de profundidade",
        "Árvores, nuvens e vegetação",
        "Anatomia artística: proporções do corpo",
        "Estrutura do tronco e caixa torácica",
        "Pelve e conexão com o tronco",
        "Braços: volumes e articulações",
        "Pernas: volumes e articulações",
        "Mãos construídas por planos",
        "Pés e apoio do corpo",
        "Gestual: capturando movimento",
        "Figura humana em poses simples",
        "Cabeça em vista frontal",
        "Cabeça em vista lateral",
        "Cabeça em três quartos",
        "Proporções do rosto humano",
        "Construção dos olhos e sobrancelhas",
        "Nariz em diferentes ângulos",
        "Boca, lábios e expressão",
        "Orelhas e planos da cabeça",
        "Cabelo: massas, mechas e direção",
        "Dobras e caimento de tecidos",
        "Roupas sobre o corpo em movimento",
        "Animais: estrutura e proporção",
        "Desenho de animais em ação",
        "Estudo de mãos em poses variadas",
        "Estudo de pés e calçados",
        "Retrato com valores de luz e sombra",
        "Desenho de objetos do cotidiano",
        "Composição de uma natureza-morta",
        "Introdução à cor e temperatura",
        "Mistura de cores e paleta limitada",
        "Lápis de cor: camadas e degradês",
        "Técnicas de acabamento e limpeza",
        "Como analisar e corrigir um desenho",
        "Projeto: personagem com cenário",
        "Projeto: estudo de figura humana em perspectiva"
    )
    private val advancedTitles = listOf(
        "Anatomia avançada: estrutura e proporção",
        "Esqueleto simplificado para artistas",
        "Marcos ósseos e referências anatômicas",
        "Musculatura do tronco em desenho",
        "Ombros, braços e grupos musculares",
        "Antebraço, punho e mãos avançadas",
        "Quadril, coxas e musculatura das pernas",
        "Anatomia dinâmica em movimento",
        "Equilíbrio, peso e centro de gravidade",
        "Perspectiva avançada com vários pontos de fuga",
        "Perspectiva curvilínea e grande angular",
        "Encurtamento extremo da figura humana",
        "Construção de cenas complexas",
        "Composição narrativa e leitura visual",
        "Hierarquia visual e pontos de interesse",
        "Ritmo, repetição e contraste",
        "Luz dramática e iluminação de recorte",
        "Luz ambiente e múltiplas fontes",
        "Materiais complexos: pele, metal e vidro",
        "Texturas orgânicas e superfícies difíceis",
        "Retrato realista: estrutura da cabeça",
        "Retrato em ângulos extremos",
        "Semelhança e análise de proporções faciais",
        "Olhos realistas e expressão do olhar",
        "Nariz realista com planos e valores",
        "Boca realista e anatomia dos lábios",
        "Orelhas e detalhes anatômicos",
        "Cabelo realista: volume e fios seletivos",
        "Pele: transições tonais e textura",
        "Mãos expressivas e gestos complexos",
        "Pés em perspectiva e movimento",
        "Expressões faciais e emoções",
        "Idade, estrutura facial e caracterização",
        "Diversidade de rostos e observação respeitosa",
        "Figura humana em poses de ação",
        "Anatomia em torção e compressão",
        "Drapejamento avançado de tecidos",
        "Dobras: tensão, compressão e gravidade",
        "Animais: anatomia comparada",
        "Pelos, penas e escamas",
        "Paisagem avançada e perspectiva atmosférica",
        "Vegetação detalhada sem excesso de linhas",
        "Arquitetura: escala, ritmo e perspectiva",
        "Cenários internos com iluminação",
        "Cenários externos e profundidade",
        "Desenho de personagens: silhueta e leitura",
        "Design de personagem e linguagem visual",
        "Variações de figurino e acessórios",
        "Ilustração editorial: conceito e composição",
        "Narrativa visual em uma única imagem",
        "Storyboard e sequência de ações",
        "Estilo autoral: referências e síntese",
        "Desenho de imaginação com referências",
        "Como estudar referências sem copiar mecanicamente",
        "Planejamento de uma ilustração final",
        "Esboço avançado e refinamento por etapas",
        "Line art: peso e hierarquia de linha",
        "Grafite avançado: controle de valores",
        "Lápis de cor: camadas, mistura e acabamento",
        "Técnicas mistas no desenho tradicional",
        "Crítica construtiva e autoavaliação",
        "Portfólio: seleção e apresentação de trabalhos",
        "Projeto final: retrato ou personagem completo",
        "Desafio de conclusão: composição autoral",
        "Apresentação final e plano de evolução"
    )

    val all: List<DrawingLesson> by lazy {
        val levels = listOf("Básico" to basicTitles, "Intermediário" to intermediateTitles, "Avançado" to advancedTitles)
        val lessons = mutableListOf<DrawingLesson>()
        var id = 1
        for ((level, titles) in levels) {
            titles.forEachIndexed { index, title ->
                val source = when (level) {
                    "Básico" -> when { index < 15 -> "VCdesenhos e Lápis com Café"; index < 30 -> "Mia GB"; else -> "Mariana Cagnin e Cantinho dos Arteiros" }
                    "Intermediário" -> if (index < 30) "Mia GB e Mariana Cagnin" else "Mariana Cagnin"
                    else -> when { index < 25 -> "Mariana Cagnin"; index < 45 -> "VCdesenhos World"; else -> "Cantinho dos Arteiros e Tathiane Vargas" }
                }
                val video = when (title) {
                    "Conhecendo os materiais de desenho" -> "https://www.youtube.com/watch?v=9nb8rFU7iVs"
                    "Introdução às proporções do rosto" -> "https://www.youtube.com/watch?v=P2g0LheCcpQ"
                    "Perspectiva de dois pontos de fuga" -> "https://www.youtube.com/watch?v=16bg4WGjxOo"
                    "Luz e sombra em formas combinadas" -> "https://www.youtube.com/watch?v=zB5d6Ed8_78"
                    "Mãos construídas por planos" -> "https://www.youtube.com/watch?v=NtzbfgBk_Ps"
                    else -> null
                }
                val url = video ?: "https://www.youtube.com/results?search_query=" + Uri.encode("$title desenho tutorial $source")
                val objective = "Nesta aula, você vai estudar $title. Observe exemplos, entenda as formas principais e construa o desenho por etapas, sem pular o esboço."
                val exercise = "Em uma folha, faça três estudos de $title: comece com formas simples, desenvolva o desenho com calma e finalize comparando proporções, linhas e valores. Anote uma coisa que melhorou e outra para praticar novamente."
                val options = listOf("Observar, construir por etapas e praticar $title.", "Pular o esboço e tentar acertar tudo de primeira.", "Desenhar apenas o contorno, sem observar forma ou proporção.")
                lessons.add(DrawingLesson(id, level, index + 1, title, objective, exercise,
                    "Qual abordagem ajuda a aplicar corretamente o conteúdo desta aula?", options, 0, source, url))
                id++
            }
        }
        lessons
    }
    const val UFOB_REFERENCE = "https://ufob.edu.br/especial-de-quarentena/desenho-e-pintura/aprenda-a-desenhar-e-pintar-com-o-youtube"
    const val UFJF_REFERENCE = "https://www2.ufjf.br/design/primeira-linha-sketch/"
}
