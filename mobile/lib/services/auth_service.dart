import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/funcionario.dart';

import 'package:flutter/foundation.dart';

class AuthService {
  final String baseUrl;
  String? _cookie;

  AuthService({String? baseUrl}) 
    : baseUrl = baseUrl ?? (kIsWeb ? 'http://localhost:8081' : 'http://10.0.2.2:8081');

  Future<void> _loadCookie() async {
    final prefs = await SharedPreferences.getInstance();
    _cookie = prefs.getString('cookie');
  }

  Future<void> _saveCookie(String cookie) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('cookie', cookie);
    _cookie = cookie;
  }

  Future<bool> login(String cpf, String senha) async {
    await _loadCookie();
    final uri = Uri.parse('$baseUrl/login');

    try {
      final resp = await http.post(
        uri,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Accept': 'application/json',
        },
        body: {
          'username': cpf,
          'password': senha,
        },
      ).timeout(const Duration(seconds: 10));

      debugPrint('Login response: ${resp.statusCode}');
      debugPrint('Headers: ${resp.headers}');

      if (resp.statusCode == 302 || resp.statusCode == 200) {
        final setCookie = resp.headers['set-cookie'];
        if (setCookie != null) {
          await _saveCookie(setCookie.split(';').first);
        }
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Erro no login: $e');
      return false;
    }
  }

  Future<Funcionario?> me() async {
    await _loadCookie();
    final uri = Uri.parse('$baseUrl/api/auth/me');
    final resp = await http.get(uri, headers: {
      if (_cookie != null) 'Cookie': _cookie!,
      'Accept': 'application/json',
    });

    if (resp.statusCode == 200) {
      final data = jsonDecode(resp.body) as Map<String, dynamic>;
      if (data['autenticado'] == true) {
        return Funcionario.fromJson(data);
      }
      return null;
    }
    return null;
  }

  Future<bool> changePassword(String cpf, String novaSenha) async {
    await _loadCookie();
    final uri = Uri.parse('$baseUrl/api/auth/primeiro-acesso');
    final resp = await http.post(uri,
        headers: {
          'Content-Type': 'application/json',
          if (_cookie != null) 'Cookie': _cookie!,
        },
        body: jsonEncode({
          'cpf': cpf,
          'novaSenha': novaSenha,
        }));
    return resp.statusCode == 200;
  }

  Future<void> logout() async {
    final uri = Uri.parse('$baseUrl/logout');
    await http.post(uri, headers: {
      if (_cookie != null) 'Cookie': _cookie!,
    });
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('cookie');
    _cookie = null;
  }
}
