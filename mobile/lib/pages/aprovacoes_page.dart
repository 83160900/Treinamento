import 'package:flutter/material.dart';
import '../models/funcionario.dart';
import '../models/aprovacao.dart';
import '../services/aprovacao_service.dart';

class AprovacoesPage extends StatefulWidget {
  final Funcionario user;
  const AprovacoesPage({super.key, required this.user});

  @override
  State<AprovacoesPage> createState() => _AprovacoesPageState();
}

class _AprovacoesPageState extends State<AprovacoesPage> {
  bool _loading = true;
  List<Aprovacao> _itens = [];
  final _service = AprovacaoService();

  @override
  void initState() {
    super.initState();
    _carregar();
  }

  Future<void> _carregar() async {
    setState(() => _loading = true);
    try {
      final status = widget.user.perfil == 'GESTOR' ? 'PENDENTE' : null;
      final lista = await _service.listar(status: status);
      setState(() {
        _itens = lista;
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao carregar aprovações: $e'), backgroundColor: Colors.red),
        );
      }
    }
  }

  void _aprovar(Aprovacao item) async {
    final ok = await _service.aprovar(item.id);
    if (ok) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Aprovado: ${item.nomeFuncionario}')),
        );
      }
      _carregar();
    } else {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Erro ao aprovar'), backgroundColor: Colors.red),
        );
      }
    }
  }

  void _reprovar(Aprovacao item) async {
    final justificativaController = TextEditingController();
    final confirmar = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Reprovar Solicitação'),
        content: TextField(
          controller: justificativaController,
          decoration: const InputDecoration(labelText: 'Justificativa'),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('CANCELAR')),
          TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('REPROVAR')),
        ],
      ),
    );

    if (confirmar == true) {
      final ok = await _service.rejeitar(item.id, justificativa: justificativaController.text);
      if (ok) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Reprovado: ${item.nomeFuncionario}')),
          );
        }
        _carregar();
      } else {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Erro ao reprovar'), backgroundColor: Colors.red),
          );
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Aprovações')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _carregar,
              child: _itens.isEmpty
                  ? const Center(child: Text('Nenhuma aprovação pendente'))
                  : ListView.builder(
                      padding: const EdgeInsets.all(12),
                      itemCount: _itens.length,
                      itemBuilder: (ctx, i) {
                        final item = _itens[i];
                        return Card(
                          child: ListTile(
                            title: Text(item.nomeFuncionario),
                            subtitle: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text('${item.tipo} • ${item.dataMarcacao}'),
                                if (item.tipo != 'ABONO')
                                  Text('Horas: ${item.e1 ?? '--'}:${item.s1 ?? '--'} | ${item.e2 ?? '--'}:${item.s2 ?? '--'}'),
                                if (item.tipo == 'ABONO')
                                  Text('Abono: ${item.horasAbonadas ?? 'Dia Todo'}'),
                                Text('Status: ${item.status}', style: const TextStyle(fontWeight: FontWeight.bold)),
                              ],
                            ),
                            trailing: Wrap(
                              spacing: 8,
                              children: [
                                IconButton(
                                  icon: const Icon(Icons.check_circle, color: Colors.green),
                                  onPressed: () => _aprovar(item),
                                  tooltip: 'Aprovar',
                                ),
                                IconButton(
                                  icon: const Icon(Icons.cancel, color: Colors.red),
                                  onPressed: () => _reprovar(item),
                                  tooltip: 'Reprovar',
                                ),
                              ],
                            ),
                          ),
                        );
                      },
                    ),
            ),
    );
  }
}
