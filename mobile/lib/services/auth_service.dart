import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/funcionario.dart';

class AuthService {
  // Em desenvolvimento mobile, localhost refere-se ao próprio dispositivo/emulador.
  // Use 10.0.2.2 para emulador Android ou o IP da máquina na rede.
  static const String baseUrl = 'http://10.0.2.2:8080';
  
  String? _cookie;

  Future<bool> login(String username, String password) async {
    final response = await http.post(
      Uri.parse('$baseUrl/login'),
      body: {
        'username': username,
        'password': password,
      },
    );

    if (response.statusCode == 200 || response.statusCode == 302) {
      // Captura o cookie de sessão do Spring Security
      _updateCookie(response);
      return true;
    }
    return false;
  }

  void _updateCookie(http.Response response) {
    String? rawCookie = response.headers['set-cookie'];
    if (rawCookie != null) {
      int index = rawCookie.indexOf(';');
      _cookie = (index == -1) ? rawCookie : rawCookie.substring(0, index);
      _saveCookie(_cookie!);
    }
  }

  Future<void> _saveCookie(String cookie) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('session_cookie', cookie);
  }

  Future<Funcionario?> getMe() async {
    final prefs = await SharedPreferences.getInstance();
    _cookie = prefs.getString('session_cookie');

    final response = await http.get(
      Uri.parse('$baseUrl/api/auth/me'),
      headers: _cookie != null ? {'cookie': _cookie!} : {},
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      if (data['autenticado'] == true) {
        return Funcionario.fromJson(data);
      }
    }
    return null;
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('session_cookie');
    await http.post(
      Uri.parse('$baseUrl/logout'),
      headers: _cookie != null ? {'cookie': _cookie!} : {},
    );
    _cookie = null;
  }
}
