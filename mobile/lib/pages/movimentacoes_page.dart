import 'package:flutter/material.dart';
import '../models/funcionario.dart';
import '../models/marcacao.dart';
import '../services/ponto_service.dart';

class MovimentacoesPage extends StatefulWidget {
  final Funcionario user;
  const MovimentacoesPage({super.key, required this.user});

  @override
  State<MovimentacoesPage> createState() => _MovimentacoesPageState();
}

class _MovimentacoesPageState extends State<MovimentacoesPage> {
  bool _loading = true;
  List<Marcacao> _itens = [];
  final _service = PontoService();

  @override
  void initState() {
    super.initState();
    _carregar();
  }

  Future<void> _carregar() async {
    setState(() => _loading = true);
    try {
      final lista = await _service.listarMarcacoes(widget.user.filial, widget.user.matricula);
      setState(() {
        _itens = lista;
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao carregar marcações: $e'), backgroundColor: Colors.red),
        );
      }
    }
  }

  int _hParaMin(String? h) {
    if (h == null || h.isEmpty || !h.contains(':')) return 0;
    final parts = h.split(':');
    return (int.tryParse(parts[0]) ?? 0) * 60 + (int.tryParse(parts[1]) ?? 0);
  }

  String _minParaH(int m) {
    final hrs = (m / 60).floor();
    final mins = m % 60;
    return '${hrs.toString().padLeft(2, '0')}:${mins.toString().padLeft(2, '0')}';
  }

  Map<String, String> _calcularTotais() {
    int trabalhado = 0;
    int abonado = 0;

    for (var m in _itens) {
      // Abono
      if (m.idAbono != null) {
        if (m.abonoDiaTodo) {
          // Simplificação: se for dia todo, assumimos 8h se não tiver horário definido no mobile
          abonado += 480;
        } else if (m.horasAbonadas != null) {
          abonado += _hParaMin(m.horasAbonadas);
        }
      }

      // Horas trabalhadas
      int t1 = _hParaMin(m.s1) - _hParaMin(m.e1);
      int t2 = _hParaMin(m.s2) - _hParaMin(m.e2);
      if (t1 > 0) trabalhado += t1;
      if (t2 > 0) trabalhado += t2;
    }

    int saldoTotal = trabalhado + abonado;

    return {
      'trabalhado': _minParaH(trabalhado),
      'abonado': _minParaH(abonado),
      'total': _minParaH(saldoTotal),
    };
  }

  @override
  Widget build(BuildContext context) {
    final totais = _calcularTotais();

    return Scaffold(
      appBar: AppBar(title: const Text('Espelho de Ponto / Movimentações')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : Column(
              children: [
                Container(
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: Colors.indigo.withOpacity(0.1),
                    border: Border(bottom: BorderSide(color: Colors.indigo.withOpacity(0.2))),
                  ),
                  child: Column(
                    children: [
                      const Text('RESUMO DO PERÍODO ATIVO', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.indigo)),
                      const SizedBox(height: 12),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceAround,
                        children: [
                          _ResumoItem(label: 'Trabalhado', valor: totais['trabalhado']!),
                          _ResumoItem(label: 'Abonado', valor: totais['abonado']!),
                          _ResumoItem(label: 'Saldo Geral', valor: totais['total']!, destaque: true),
                        ],
                      ),
                    ],
                  ),
                ),
                Expanded(
                  child: RefreshIndicator(
                    onRefresh: _carregar,
                    child: _itens.isEmpty
                        ? const Center(child: Text('Nenhuma marcação encontrada.'))
                        : ListView.builder(
                            padding: const EdgeInsets.all(12),
                            itemCount: _itens.length,
                            itemBuilder: (ctx, i) {
                              final item = _itens[i];
                              return Card(
                                child: ListTile(
                                  title: Text(item.data, style: const TextStyle(fontWeight: FontWeight.bold)),
                                  subtitle: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Text('E1: ${item.e1 ?? '--'} | S1: ${item.s1 ?? '--'}'),
                                      Text('E2: ${item.e2 ?? '--'} | S2: ${item.s2 ?? '--'}'),
                                      if (item.idAbono != null)
                                        Text('Abono: ${item.horasAbonadas ?? 'Dia Todo'}', style: const TextStyle(color: Colors.blue)),
                                    ],
                                  ),
                                  trailing: Text(item.origem == 'P' ? 'Mobile' : (item.origem == 'M' ? 'Manual' : 'Relógio')),
                                ),
                              );
                            },
                          ),
                  ),
                ),
              ],
            ),
    );
  }
}

class _ResumoItem extends StatelessWidget {
  final String label;
  final String valor;
  final bool destaque;
  const _ResumoItem({required this.label, required this.valor, this.destaque = false});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
        Text(valor, style: TextStyle(fontSize: 18, fontWeight: destaque ? FontWeight.bold : FontWeight.normal, color: destaque ? Colors.indigo : null)),
      ],
    );
  }
}
