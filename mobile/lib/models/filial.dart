class Filial {
  final String codFilial;
  final String? codEmpresa;
  final String nome;
  final String? logradouro;
  final String? numero;
  final String? estado;
  final String? municipio;
  final String? cnpj;
  final double? latitude;
  final double? longitude;

  Filial({
    required this.codFilial,
    this.codEmpresa,
    required this.nome,
    this.logradouro,
    this.numero,
    this.estado,
    this.municipio,
    this.cnpj,
    this.latitude,
    this.longitude,
  });

  factory Filial.fromJson(Map<String, dynamic> json) {
    return Filial(
      codFilial: json['codFilial'] ?? '',
      codEmpresa: json['codEmpresa'],
      nome: json['nome'] ?? '',
      logradouro: json['logradouro'],
      numero: json['num'],
      estado: json['estado'],
      municipio: json['municipio'],
      cnpj: json['cnpj'],
      latitude: json['latitude'] != null ? (json['latitude'] as double?) : null,
      longitude: json['longitude'] != null ? (json['longitude'] as double?) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'codFilial': codFilial,
      'codEmpresa': codEmpresa,
      'nome': nome,
      'logradouro': logradouro,
      'num': numero,
      'estado': estado,
      'municipio': municipio,
      'cnpj': cnpj,
      'latitude': latitude,
      'longitude': longitude,
    };
  }
}
