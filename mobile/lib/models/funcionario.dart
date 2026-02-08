class Funcionario {
  final String cpf;
  final String filial;
  final String matricula;
  final String nome;
  final String perfil;
  final bool trocarSenha;

  Funcionario({
    required this.cpf,
    required this.filial,
    required this.matricula,
    required this.nome,
    required this.perfil,
    required this.trocarSenha,
  });

  factory Funcionario.fromJson(Map<String, dynamic> json) {
    return Funcionario(
      cpf: json['username'] ?? '',
      filial: json['filial'] ?? '',
      matricula: json['matricula'] ?? '',
      nome: json['nome'] ?? '',
      perfil: json['perfil'] ?? '',
      trocarSenha: json['trocarSenha'] ?? false,
    );
  }
}
