# AP Booster — projeto Android

Aplicativo Android simples, sem root, com visual escuro profissional e foco em Free Fire.

## Recursos
- Exibe RAM disponível, armazenamento livre, bateria e temperatura da bateria (quando fornecida pelo aparelho).
- Atalho para abrir Free Fire MAX ou Free Fire normal, se instalado.
- Atalhos para configurações de bateria, armazenamento, aplicativos e Não Perturbe.
- Não solicita permissões perigosas nem encerra processos à força.

## Como gerar o APK
1. Abra este projeto no Android Studio.
2. Aguarde a sincronização do Gradle e instale o SDK Android 35 se solicitado.
3. Vá em **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. O APK de teste será gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Limitações importantes
Aplicativos Android comuns não podem garantir ganho de FPS, limpar toda a RAM ou controlar a temperatura do processador. O Android gerencia memória e processos. Os dados exibidos são de monitoramento; os botões abrem configurações oficiais do sistema.
