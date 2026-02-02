# Plugin-RH Mobile

Esta é a versão mobile do sistema Plugin-RH, desenvolvida em Flutter.

## Pré-requisitos

- Flutter SDK (>= 3.0.0)
- Android Studio / VS Code com extensões Flutter/Dart
- Backend Spring Boot em execução

## Configuração

1. Entre na pasta `mobile/`:
   ```bash
   cd mobile
   ```

2. Obtenha as dependências:
   ```bash
   flutter pub get
   ```

3. Configure o endereço do servidor no arquivo `lib/services/auth_service.dart`:
   - Por padrão está `http://10.0.2.2:8080` (acesso ao localhost no emulador Android).

4. Execute o aplicativo:
   ```bash
   flutter run
   ```

## Funcionalidades Implementadas

- [x] Autenticação (Login/Logout) baseada em sessão.
- [x] Dashboard dinâmico por perfil (Colaborador/Gestor/Admin).
- [x] Estrutura para registro de marcações.
- [x] Persistência de sessão (SharedPreferences).
