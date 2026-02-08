import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import 'package:geolocator/geolocator.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/funcionario.dart';
import '../services/ponto_service.dart';

class PontoPage extends StatefulWidget {
  final Funcionario user;
  const PontoPage({super.key, required this.user});

  @override
  State<PontoPage> createState() => _PontoPageState();
}

class _PontoPageState extends State<PontoPage> {
  bool _loading = false;
  String? _ultimoResultado;
  final _pontoService = PontoService();

  Future<void> _iniciarProcessoPonto() async {
    setState(() => _loading = true);
    try {
      Position pos = await _pontoService.determinePosition();
      if (!mounted) return;
      
      // Abre a tela de confirmação com mapa
      final confirmou = await Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => ConfirmacaoPontoPage(
            latitude: pos.latitude,
            longitude: pos.longitude,
          ),
        ),
      );

      if (confirmou != null && confirmou is Map<String, dynamic>) {
        await _baterPonto(
          latitude: confirmou['latitude'],
          longitude: confirmou['longitude'],
          endereco: confirmou['endereco'],
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Erro: $e'), backgroundColor: Colors.red),
      );
    } finally {
      setState(() => _loading = false);
    }
  }

  Future<void> _baterPonto({double? latitude, double? longitude, String? endereco}) async {
    setState(() {
      _loading = true;
      _ultimoResultado = null;
    });
    
    try {
      final result = await _pontoService.baterPonto(
        latitude: latitude,
        longitude: longitude,
        endereco: endereco,
      );
      setState(() {
        _ultimoResultado = '${result['message']}\nHora: ${result['hora']}';
        if (result['warning'] != null) {
          _ultimoResultado = '${_ultimoResultado!}\n\nAviso: ${result['warning']}';
        }
      });
      if (!mounted) return;
      
      if (result['warning'] != null || result['status'] == 'FORA_DO_RAIO') {
        showDialog(
          context: context,
          builder: (context) => AlertDialog(
            title: Text(result['status'] == 'FORA_DO_RAIO' ? 'Atenção' : 'Aviso'),
            content: Text(result['status'] == 'FORA_DO_RAIO' ? result['message'] : result['warning']),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('OK'),
              ),
            ],
          ),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(result['message'])),
        );
      }
    } catch (e) {
      setState(() {
        _ultimoResultado = 'Erro ao bater ponto: $e';
      });
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Erro: $e'), backgroundColor: Colors.red),
      );
    } finally {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Bater Ponto')),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('Olá, ${widget.user.nome}', style: const TextStyle(fontSize: 18)),
              const SizedBox(height: 24),
              ElevatedButton.icon(
                onPressed: _loading ? null : _iniciarProcessoPonto,
                icon: const Icon(Icons.fingerprint),
                label: _loading
                    ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2))
                    : const Text('Bater ponto agora'),
                style: ElevatedButton.styleFrom(minimumSize: const Size(220, 48)),
              ),
              if (_ultimoResultado != null) ...[
                const SizedBox(height: 24),
                Text(_ultimoResultado!, textAlign: TextAlign.center),
              ]
            ],
          ),
        ),
      ),
    );
  }
}

class ConfirmacaoPontoPage extends StatefulWidget {
  final double latitude;
  final double longitude;

  const ConfirmacaoPontoPage({
    super.key,
    required this.latitude,
    required this.longitude,
  });

  @override
  State<ConfirmacaoPontoPage> createState() => _ConfirmacaoPontoPageState();
}

class _ConfirmacaoPontoPageState extends State<ConfirmacaoPontoPage> {
  String _endereco = "Buscando endereço...";

  @override
  void initState() {
    super.initState();
    _buscarEndereco();
  }

  Future<void> _buscarEndereco() async {
    try {
      final url = Uri.parse(
          'https://nominatim.openstreetmap.org/reverse?format=json&lat=${widget.latitude}&lon=${widget.longitude}&zoom=18&addressdetails=1');
      final response = await http.get(url, headers: {
        'User-Agent': 'MobileRH/1.0',
      });
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          _endereco = data['display_name'] ?? "Endereço não encontrado";
        });
      }
    } catch (e) {
      setState(() {
        _endereco = "Erro ao buscar endereço";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Confirmar Localização')),
      body: Column(
        children: [
          Expanded(
            child: FlutterMap(
              options: MapOptions(
                initialCenter: LatLng(widget.latitude, widget.longitude),
                initialZoom: 16.0,
              ),
              children: [
                TileLayer(
                  urlTemplate: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
                  userAgentPackageName: 'com.example.mobile_rh',
                ),
                MarkerLayer(
                  markers: [
                    Marker(
                      point: LatLng(widget.latitude, widget.longitude),
                      width: 80,
                      height: 80,
                      child: const Icon(
                        Icons.location_on,
                        color: Colors.red,
                        size: 40,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.white,
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.1),
                  blurRadius: 10,
                  offset: const Offset(0, -5),
                ),
              ],
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const Text(
                  'ENDEREÇO IDENTIFICADO:',
                  style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                    color: Colors.grey,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  _endereco,
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 24),
                ElevatedButton(
                  onPressed: () {
                    Navigator.pop(context, {
                      'latitude': widget.latitude,
                      'longitude': widget.longitude,
                      'endereco': _endereco,
                    });
                  },
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    backgroundColor: Colors.indigo,
                    foregroundColor: Colors.white,
                  ),
                  child: const Text('CONFIRMAR E BATER PONTO'),
                ),
                TextButton(
                  onPressed: () => Navigator.pop(context),
                  child: const Text('CANCELAR'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
