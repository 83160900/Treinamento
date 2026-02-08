import 'package:flutter/material.dart';
import '../models/funcionario.dart';
import '../models/filial.dart';
import '../services/filial_service.dart';

class FiliaisPage extends StatefulWidget {
  final Funcionario user;
  const FiliaisPage({super.key, required this.user});

  @override
  State<FiliaisPage> createState() => _FiliaisPageState();
}

class _FiliaisPageState extends State<FiliaisPage> {
  bool _loading = true;
  List<Filial> _itens = [];
  final _service = FilialService();

  @override
  void initState() {
    super.initState();
    _carregar();
  }

  Future<void> _carregar() async {
    setState(() => _loading = true);
    try {
      final lista = await _service.listar();
      setState(() {
        _itens = lista;
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao carregar filiais: $e'), backgroundColor: Colors.red),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Filiais')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _carregar,
              child: ListView.builder(
                padding: const EdgeInsets.all(12),
                itemCount: _itens.length,
                itemBuilder: (ctx, i) {
                  final item = _itens[i];
                  return Card(
                    child: ListTile(
                      title: Text(item.nome),
                      subtitle: Text('Código: ${item.codFilial} • CNPJ: ${item.cnpj ?? ''}\nLat: ${item.latitude ?? '--'}, Long: ${item.longitude ?? '--'}'),
                      isThreeLine: true,
                    ),
                  );
                },
              ),
            ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // TODO: Implementar cadastro de filial
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
