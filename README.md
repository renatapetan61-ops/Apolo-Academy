# Apolo Academy

Aplicativo Android nativo em Kotlin + Jetpack Compose para aprender desenho do nível iniciante ao avançado.

## Tutor Apolo IA
O tutor faz chamadas reais à API Google Gemini (modelo Gemini 2.5 Flash). Na tela Tutor IA, informe uma chave criada no Google AI Studio e envie perguntas. A chave fica apenas na memória da tela e não é gravada em SharedPreferences nem embutida no APK. A comunicação usa HTTPS.

**Segurança:** esta integração direta é apropriada para testes e uso pessoal com chave própria. Para distribuição pública, o ideal é mover a chamada para um backend autenticado e manter a chave do provedor somente no servidor. Uma chave digitada no app pode ser extraída do dispositivo enquanto está em uso.

## Criar chave Gemini
1. Acesse https://aistudio.google.com/app/apikey
2. Entre na sua conta Google.
3. Crie uma API key.
4. Copie e cole no campo "Chave API do Google AI Studio" na tela Tutor IA.
5. Não compartilhe a chave nem a publique no GitHub.

A disponibilidade, as cotas e eventual cobrança dependem das condições da sua conta Google AI Studio.

## Compilar
Requisitos: JDK 17, Android SDK 35 e Gradle 8.9.

```bash
gradle --no-daemon :app:assembleDebug
gradle --no-daemon :app:assembleRelease
```

O APK fica em app/build/outputs/apk/release/app-release.apk. Sem keystore configurado, o Release não será assinado para distribuição.

## Release assinado pelo GitHub Actions
O workflow .github/workflows/android-release.yml requer quatro GitHub Actions repository secrets:
- APOLO_RELEASE_KEYSTORE_BASE64
- APOLO_RELEASE_STORE_PASSWORD
- APOLO_RELEASE_KEY_ALIAS
- APOLO_RELEASE_KEY_PASSWORD

O alias usado no comando de geração recomendado é apolo-release. Guarde o keystore permanentemente e faça backup seguro. Não gere outro para cada atualização, pois as próximas versões precisam da mesma chave.

## Estado do produto
A conversa com Gemini está implementada. A biblioteca internacional completa, editor/canvas de desenho, persistência de progresso e conteúdo curricular amplo ainda são etapas futuras.
