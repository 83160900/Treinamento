import 'package:flutter/material.dart';
import 'pages/login_page.dart';

void main() {
  runApp(const PluginRHApp());
}

class PluginRHApp extends StatelessWidget {
  const PluginRHApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Plugin-RH Mobile',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF6366F1)),
        useMaterial3: true,
        fontFamily: 'Plus Jakarta Sans',
      ),
      home: const LoginPage(),
    );
  }
}
