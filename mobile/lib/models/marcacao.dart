class Marcacao {
  final String filial;
  final String matricula;
  final String data;
  final String? e1;
  final String? s1;
  final String? e2;
  final String? s2;
  final String? origem;
  final double? latitude;
  final double? longitude;
  final String? endereco;
  final int? idAbono;
  final String? horasAbonadas;
  final bool abonoDiaTodo;
  final String? justificativa;

  Marcacao({
    required this.filial,
    required this.matricula,
    required this.data,
    this.e1,
    this.s1,
    this.e2,
    this.s2,
    this.origem,
    this.latitude,
    this.longitude,
    this.endereco,
    this.idAbono,
    this.horasAbonadas,
    this.abonoDiaTodo = false,
    this.justificativa,
  });

  factory Marcacao.fromJson(Map<String, dynamic> json) {
    return Marcacao(
      filial: json['filial'] ?? '',
      matricula: json['matricula'] ?? '',
      data: json['data'] ?? '',
      e1: json['e1'],
      s1: json['s1'],
      e2: json['e2'],
      s2: json['s2'],
      origem: json['origem'],
      latitude: json['latitude'] != null ? (json['latitude'] as num).toDouble() : null,
      longitude: json['longitude'] != null ? (json['longitude'] as num).toDouble() : null,
      endereco: json['endereco'],
      idAbono: json['idAbono'],
      horasAbonadas: json['horasAbonadas'],
      abonoDiaTodo: json['abonoDiaTodo'] ?? false,
      justificativa: json['justificativa'],
    );
  }
}
