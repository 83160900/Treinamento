import 'package:flutter/material.dart';
import '../models/funcionario.dart';
import '../services/auth_service.dart';
import 'ponto_page.dart';
import 'aprovacoes_page.dart';
import 'cadastros_page.dart';
import 'movimentacoes_page.dart';

class HomePage extends StatelessWidget {
  final Funcionario user;
  const HomePage({super.key, required this.user});

  @override
  Widget build(BuildContext context) {
    final auth = AuthService();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin-RH'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              await auth.logout();
              if (context.mounted) Navigator.of(context).pop();
            },
          )
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Olá, ${user.nome}', style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Text('Perfil: ${user.perfil}'),
            const SizedBox(height: 24),
            Wrap(
              spacing: 12,
              runSpacing: 12,
              children: [
                _QuickAction(
                  icon: Icons.fingerprint,
                  label: 'Bater Ponto',
                  onTap: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(builder: (_) => PontoPage(user: user)),
                    );
                  },
                ),
                if (user.perfil == 'GESTOR' || user.perfil == 'ADMIN')
                  _QuickAction(
                    icon: Icons.approval,
                    label: 'Aprovações',
                    onTap: () {
                      Navigator.of(context).push(
                        MaterialPageRoute(builder: (_) => AprovacoesPage(user: user)),
                      );
                    },
                  ),
                if (user.perfil == 'ADMIN')
                  _QuickAction(
                    icon: Icons.settings,
                    label: 'Cadastros',
                    onTap: () {
                      Navigator.of(context).push(
                        MaterialPageRoute(builder: (_) => CadastrosPage(user: user)),
                      );
                    },
                  ),
                _QuickAction(
                  icon: Icons.history,
                  label: 'Movimentações',
                  onTap: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(builder: (_) => MovimentacoesPage(user: user)),
                    );
                  },
                ),
                _QuickAction(
                  icon: Icons.calendar_month,
                  label: 'Espelho de Ponto',
                  onTap: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(builder: (_) => MovimentacoesPage(user: user)),
                    );
                  },
                ),
              ],
            )
          ],
        ),
      ),
    );
  }
}

class _QuickAction extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;
  const _QuickAction({required this.icon, required this.label, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Container(
        width: 150,
        height: 90,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(12),
          color: Theme.of(context).colorScheme.primaryContainer,
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon),
            const SizedBox(height: 8),
            Text(label, style: const TextStyle(fontWeight: FontWeight.w600)),
          ],
        ),
      ),
    );
  }
}
