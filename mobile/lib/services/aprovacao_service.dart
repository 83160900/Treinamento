import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/aprovacao.dart';

import 'package:flutter/foundation.dart';

class AprovacaoService {
  final String baseUrl;

  AprovacaoService({String? baseUrl}) 
    : baseUrl = baseUrl ?? (kIsWeb ? 'http://localhost:8081' : 'http://10.0.2.2:8081');

  Future<String?> _getCookie() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('cookie');
  }

  Future<List<Aprovacao>> listar({String? status}) async {
    final cookie = await _getCookie();
    final query = status != null ? '?status=$status' : '';
    final uri = Uri.parse('$baseUrl/api/aprovacoes$query');

    final resp = await http.get(uri, headers: {
      if (cookie != null) 'Cookie': cookie,
      'Accept': 'application/json',
    });

    if (resp.statusCode == 200) {
      final List<dynamic> data = jsonDecode(resp.body);
      return data.map((item) => Aprovacao.fromJson(item)).toList();
    }
    return [];
  }

  Future<bool> aprovar(int id) async {
    final cookie = await _getCookie();
    final uri = Uri.parse('$baseUrl/api/aprovacoes/$id/aprovar');

    final resp = await http.post(uri, headers: {
      if (cookie != null) 'Cookie': cookie,
    });

    return resp.statusCode == 200;
  }

  Future<bool> rejeitar(int id, {String? justificativa}) async {
    final cookie = await _getCookie();
    final uri = Uri.parse('$baseUrl/api/aprovacoes/$id/rejeitar');

    final resp = await http.post(
      uri,
      headers: {
        'Content-Type': 'application/json',
        if (cookie != null) 'Cookie': cookie,
      },
      body: justificativa,
    );

    return resp.statusCode == 200;
  }
}
