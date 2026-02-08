import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import '../models/funcionario.dart';
import 'home_page.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _cpfCtrl = TextEditingController();
  final _senhaCtrl = TextEditingController();
  final _auth = AuthService();
  bool _loading = false;

  void _doLogin() async {
    setState(() => _loading = true);
    final ok = await _auth.login(_cpfCtrl.text.trim(), _senhaCtrl.text.trim());
    if (!mounted) return;

    if (!ok) {
      setState(() => _loading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Usuário ou senha inválidos.')),
      );
      return;
    }

    final user = await _auth.me();
    if (!mounted) return;

    if (user == null) {
      setState(() => _loading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Não foi possível obter o usuário.')),
      );
      return;
    }

    if (user.trocarSenha) {
      _showChangePasswordDialog(user);
    } else {
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => HomePage(user: user)),
      );
    }
  }

  void _showChangePasswordDialog(Funcionario user) {
    final ctrl = TextEditingController();
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (ctx) {
        return AlertDialog(
          title: const Text('Primeiro Acesso'),
          content: TextField(
            controller: ctrl,
            decoration: const InputDecoration(labelText: 'Nova senha'),
            obscureText: true,
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(ctx).pop(),
              child: const Text('Cancelar'),
            ),
            ElevatedButton(
              onPressed: () async {
                final ok = await _auth.changePassword(user.cpf, ctrl.text.trim());
                if (!mounted) return;
                Navigator.of(ctx).pop();
                if (ok) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Senha alterada! Faça login novamente.')),
                  );
                  setState(() => _loading = false);
                } else {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Falha ao alterar senha.')),
                  );
                }
              },
              child: const Text('Salvar'),
            )
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 380),
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const Text('Plugin-RH', textAlign: TextAlign.center, style: TextStyle(fontSize: 26, fontWeight: FontWeight.bold)),
                const SizedBox(height: 24),
                TextField(
                  controller: _cpfCtrl,
                  decoration: const InputDecoration(labelText: 'CPF'),
                ),
                const SizedBox(height: 12),
                TextField(
                  controller: _senhaCtrl,
                  obscureText: true,
                  decoration: const InputDecoration(labelText: 'Senha'),
                ),
                const SizedBox(height: 24),
                ElevatedButton(
                  onPressed: _loading ? null : _doLogin,
                  child: _loading ? const CircularProgressIndicator() : const Text('Entrar'),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
