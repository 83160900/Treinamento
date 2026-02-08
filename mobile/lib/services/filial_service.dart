import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/filial.dart';

class FilialService {
  final String baseUrl;

  FilialService({this.baseUrl = 'http://10.0.2.2:8081'});

  Future<String?> _getCookie() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('cookie');
  }

  Future<List<Filial>> listar() async {
    final cookie = await _getCookie();
    final uri = Uri.parse('$baseUrl/api/filiais');

    final resp = await http.get(uri, headers: {
      if (cookie != null) 'Cookie': cookie,
      'Accept': 'application/json',
    });

    if (resp.statusCode == 200) {
      final List<dynamic> data = jsonDecode(resp.body);
      return data.map((item) => Filial.fromJson(item)).toList();
    }
    return [];
  }

  Future<bool> salvar(Filial filial) async {
    final cookie = await _getCookie();
    final uri = Uri.parse('$baseUrl/api/filiais');

    final resp = await http.post(
      uri,
      headers: {
        'Content-Type': 'application/json',
        if (cookie != null) 'Cookie': cookie,
      },
      body: jsonEncode(filial.toJson()),
    );

    return resp.statusCode == 200;
  }

  Future<bool> excluir(String codFilial) async {
    final cookie = await _getCookie();
    final uri = Uri.parse('$baseUrl/api/filiais/$codFilial');

    final resp = await http.delete(uri, headers: {
      if (cookie != null) 'Cookie': cookie,
    });

    return resp.statusCode == 200;
  }
}
