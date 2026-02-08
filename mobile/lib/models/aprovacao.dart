class Aprovacao {
  final int id;
  final String filial;
  final String matricula;
  final String nomeFuncionario;
  final String dataMarcacao;
  final String tipo;
  final String? e1;
  final String? s1;
  final String? e2;
  final String? s2;
  final int? idAbono;
  final String? horasAbonadas;
  final bool abonoDiaTodo;
  final String status;
  final String? justificativa;
  final String? anexoNome;
  final String? historico;

  Aprovacao({
    required this.id,
    required this.filial,
    required this.matricula,
    required this.nomeFuncionario,
    required this.dataMarcacao,
    required this.tipo,
    this.e1,
    this.s1,
    this.e2,
    this.s2,
    this.idAbono,
    this.horasAbonadas,
    required this.abonoDiaTodo,
    required this.status,
    this.justificativa,
    this.anexoNome,
    this.historico,
  });

  factory Aprovacao.fromJson(Map<String, dynamic> json) {
    return Aprovacao(
      id: json['id'],
      filial: json['filial'] ?? '',
      matricula: json['matricula'] ?? '',
      nomeFuncionario: json['nomeFuncionario'] ?? '',
      dataMarcacao: json['dataMarcacao'] ?? '',
      tipo: json['tipo'] ?? '',
      e1: json['e1'],
      s1: json['s1'],
      e2: json['e2'],
      s2: json['s2'],
      idAbono: json['idAbono'],
      horasAbonadas: json['horasAbonadas'],
      abonoDiaTodo: json['abonoDiaTodo'] ?? false,
      status: json['status'] ?? 'PENDENTE',
      justificativa: json['justificativa'],
      anexoNome: json['anexoNome'],
      historico: json['historico'],
    );
  }
}
