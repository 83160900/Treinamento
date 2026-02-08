class Empresa {
  final String codEmpresa;
  final String nome;
  final String? cnpj;

  Empresa({required this.codEmpresa, required this.nome, this.cnpj});

  factory Empresa.fromJson(Map<String, dynamic> json) {
    return Empresa(
      codEmpresa: json['codEmpresa'] ?? '',
      nome: json['nome'] ?? '',
      cnpj: json['cnpj'],
    );
  }

  Map<String, dynamic> toJson() => {
    'codEmpresa': codEmpresa,
    'nome': nome,
    'cnpj': cnpj,
  };
}

class Departamento {
  final int? id;
  final String filial;
  final String codigo;
  final String nome;

  Departamento({this.id, required this.filial, required this.codigo, required this.nome});

  factory Departamento.fromJson(Map<String, dynamic> json) {
    return Departamento(
      id: json['id'],
      filial: json['filial'] ?? '',
      codigo: json['codigo'] ?? '',
      nome: json['nome'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'filial': filial,
    'codigo': codigo,
    'nome': nome,
  };
}

class Horario {
  final int? id;
  final String filial;
  final String descricao;
  final String? e1;
  final String? s1;
  final String? e2;
  final String? s2;
  final String? tolerancia;
  final String? minutos;

  Horario({this.id, required this.filial, required this.descricao, this.e1, this.s1, this.e2, this.s2, this.tolerancia, this.minutos});

  factory Horario.fromJson(Map<String, dynamic> json) {
    return Horario(
      id: json['id'],
      filial: json['filial'] ?? '',
      descricao: json['descricao'] ?? '',
      e1: json['e1'],
      s1: json['s1'],
      e2: json['e2'],
      s2: json['s2'],
      tolerancia: json['tolerancia'],
      minutos: json['minutos'],
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'filial': filial,
    'descricao': descricao,
    'e1': e1,
    's1': s1,
    'e2': e2,
    's2': s2,
    'tolerancia': tolerancia,
    'minutos': minutos,
  };
}

class Escala {
  final int? id;
  final String filial;
  final String codigo;
  final int? idHorario;
  final String? segunda;
  final String? terca;
  final String? quarta;
  final String? quinta;
  final String? sexta;
  final String? sabado;
  final String? domingo;
  final bool cercaVirtual;
  final int? raioCerca;

  Escala({
    this.id,
    required this.filial,
    required this.codigo,
    this.idHorario,
    this.segunda,
    this.terca,
    this.quarta,
    this.quinta,
    this.sexta,
    this.sabado,
    this.domingo,
    this.cercaVirtual = false,
    this.raioCerca,
  });

  factory Escala.fromJson(Map<String, dynamic> json) {
    return Escala(
      id: json['id'],
      filial: json['filial'] ?? '',
      codigo: json['codigo'] ?? '',
      idHorario: json['idHorario'],
      segunda: json['segunda'],
      terca: json['terca'],
      quarta: json['quarta'],
      quinta: json['quinta'],
      sexta: json['sexta'],
      sabado: json['sabado'],
      domingo: json['domingo'],
      cercaVirtual: json['cercaVirtual'] ?? false,
      raioCerca: json['raioCerca'],
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'filial': filial,
    'codigo': codigo,
    'idHorario': idHorario,
    'segunda': segunda,
    'terca': terca,
    'quarta': quarta,
    'quinta': quinta,
    'sexta': sexta,
    'sabado': sabado,
    'domingo': domingo,
    'cercaVirtual': cercaVirtual,
    'raioCerca': raioCerca,
  };
}
