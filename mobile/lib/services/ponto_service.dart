import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:geolocator/geolocator.dart';
import '../models/marcacao.dart';

import 'package:flutter/foundation.dart';

class PontoService {
  final String baseUrl;

  PontoService({String? baseUrl}) 
    : baseUrl = baseUrl ?? (kIsWeb ? 'http://localhost:8081' : 'http://10.0.2.2:8081');

  Future<String?> _getCookie() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('cookie');
  }

  Future<Position> determinePosition() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return Future.error('O serviço de localização está desativado.');
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return Future.error('Permissão de localização negada.');
      }
    }

    if (permission == LocationPermission.deniedForever) {
      return Future.error('As permissões de localização estão permanentemente negadas.');
    }

    return await Geolocator.getCurrentPosition();
  }

  Future<Map<String, dynamic>> baterPonto({double? latitude, double? longitude, String? endereco}) async {
    try {
      final cookie = await _getCookie();
      
      final Map<String, dynamic> body = {};
      if (latitude != null) body['latitude'] = latitude;
      if (longitude != null) body['longitude'] = longitude;
      if (endereco != null) body['endereco'] = endereco;

      final resp = await http.post(
        Uri.parse('$baseUrl/api/marcacoes/bater-ponto'),
        headers: {
          'Content-Type': 'application/json',
          if (cookie != null) 'Cookie': cookie,
        },
        body: jsonEncode(body),
      );

      if (resp.statusCode == 200) {
        return jsonDecode(resp.body);
      } else {
        throw Exception(resp.body);
      }
    } catch (e) {
      rethrow;
    }
  }

  Future<List<Marcacao>> listarMarcacoes(String filial, String matricula) async {
    final cookie = await _getCookie();
    final uri = Uri.parse('$baseUrl/api/marcacoes?filial=$filial&matricula=$matricula');

    final resp = await http.get(uri, headers: {
      if (cookie != null) 'Cookie': cookie,
      'Accept': 'application/json',
    });

    if (resp.statusCode == 200) {
      final List data = jsonDecode(resp.body);
      return data.map((m) => Marcacao.fromJson(m)).toList();
    }
    return [];
  }
}
