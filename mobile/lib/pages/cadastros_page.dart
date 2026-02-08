import 'package:flutter/material.dart';
import '../models/funcionario.dart';
import 'filiais_page.dart';
// Importaremos as outras páginas conforme criarmos

class CadastrosPage extends StatelessWidget {
  final Funcionario user;
  const CadastrosPage({super.key, required this.user});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Cadastros')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _MenuTile(
            icon: Icons.business,
            title: 'Empresas',
            onTap: () {
              // Navigator.push(context, MaterialPageRoute(builder: (_) => EmpresasPage(user: user)));
            },
          ),
          _MenuTile(
            icon: Icons.location_city,
            title: 'Filiais',
            onTap: () {
              Navigator.push(context, MaterialPageRoute(builder: (_) => FiliaisPage(user: user)));
            },
          ),
          _MenuTile(
            icon: Icons.people,
            title: 'Departamentos',
            onTap: () {
              // Navigator.push(context, MaterialPageRoute(builder: (_) => DepartamentosPage(user: user)));
            },
          ),
          _MenuTile(
            icon: Icons.person,
            title: 'Funcionários',
            onTap: () {
              // Navigator.push(context, MaterialPageRoute(builder: (_) => FuncionariosPage(user: user)));
            },
          ),
          _MenuTile(
            icon: Icons.schedule,
            title: 'Horários',
            onTap: () {
              // Navigator.push(context, MaterialPageRoute(builder: (_) => HorariosPage(user: user)));
            },
          ),
          _MenuTile(
            icon: Icons.event_note,
            title: 'Escalas',
            onTap: () {
              // Navigator.push(context, MaterialPageRoute(builder: (_) => EscalasPage(user: user)));
            },
          ),
        ],
      ),
    );
  }
}

class _MenuTile extends StatelessWidget {
  final IconData icon;
  final String title;
  final VoidCallback onTap;

  const _MenuTile({required this.icon, required this.title, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        leading: Icon(icon, color: Colors.indigo),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
        trailing: const Icon(Icons.chevron_right),
        onTap: onTap,
      ),
    );
  }
}
