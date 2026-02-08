import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/cadastros.dart';

class CadastroService {
  final String baseUrl;

  CadastroService({this.baseUrl = 'http://10.0.2.2:8081'});

  Future<String?> _getCookie() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('cookie');
  }

  // Empresas
  Future<List<Empresa>> listarEmpresas() async {
    final cookie = await _getCookie();
    final resp = await http.get(Uri.parse('$baseUrl/api/empresas'), headers: {
      if (cookie != null) 'Cookie': cookie,
      'Accept': 'application/json',
    });
    if (resp.statusCode == 200) {
      final List data = jsonDecode(resp.body);
      return data.map((e) => Empresa.fromJson(e)).toList();
    }
    return [];
  }

  // Departamentos
  Future<List<Departamento>> listarDepartamentos() async {
    final cookie = await _getCookie();
    final resp = await http.get(Uri.parse('$baseUrl/api/departamentos'), headers: {
      if (cookie != null) 'Cookie': cookie,
      'Accept': 'application/json',
    });
    if (resp.statusCode == 200) {
      final List data = jsonDecode(resp.body);
      return data.map((e) => Departamento.fromJson(e)).toList();
    }
    return [];
  }

  // Horarios
  Future<List<Horario>> listarHorarios() async {
    final cookie = await _getCookie();
    final resp = await http.get(Uri.parse('$baseUrl/api/horarios'), headers: {
      if (cookie != null) 'Cookie': cookie,
      'Accept': 'application/json',
    });
    if (resp.statusCode == 200) {
      final List data = jsonDecode(resp.body);
      return data.map((e) => Horario.fromJson(e)).toList();
    }
    return [];
  }

  // Escalas
  Future<List<Escala>> listarEscalas() async {
    final cookie = await _getCookie();
    final resp = await http.get(Uri.parse('$baseUrl/api/escalas'), headers: {
      if (cookie != null) 'Cookie': cookie,
      'Accept': 'application/json',
    });
    if (resp.statusCode == 200) {
      final List data = jsonDecode(resp.body);
      return data.map((e) => Escala.fromJson(e)).toList();
    }
    return [];
  }
}
