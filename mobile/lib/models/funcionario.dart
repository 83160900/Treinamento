class Funcionario {
  final String cpf;
  final String nome;
  final String perfil;
  final String matricula;
  final String filial;
  final bool trocarSenha;

  Funcionario({
    required this.cpf,
    required this.nome,
    required this.perfil,
    required this.matricula,
    required this.filial,
    required this.trocarSenha,
  });

  factory Funcionario.fromJson(Map<String, dynamic> json) {
    return Funcionario(
      cpf: json['username'] ?? '',
      nome: json['nome'] ?? '',
      perfil: json['perfil'] ?? '',
      matricula: json['matricula'] ?? '',
      filial: json['filial'] ?? '',
      trocarSenha: json['trocarSenha'] ?? false,
    );
  }
}
