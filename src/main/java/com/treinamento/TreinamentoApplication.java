package com.treinamento;

import com.treinamento.model.Integracao;
import com.treinamento.model.Periodo;
import com.treinamento.repository.IntegracaoRepository;
import com.treinamento.repository.PeriodoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TreinamentoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TreinamentoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(IntegracaoRepository repository, 
                                    com.treinamento.repository.MarcacaoRepository marcacaoRepository,
                                    PeriodoRepository periodoRepository,
                                    com.treinamento.repository.FeriadoRepository feriadoRepository,
                                    com.treinamento.repository.FuncionarioRepository funcionarioRepository,
                                    com.treinamento.repository.FilialRepository filialRepository,
                                    com.treinamento.repository.EmpresaRepository empresaRepository,
                                    com.treinamento.repository.HorarioRepository horarioRepository,
                                    com.treinamento.repository.AbonoRepository abonoRepository,
                                    com.treinamento.repository.HoraExtraRepository horaExtraRepository,
                                    com.treinamento.repository.EscalaRepository escalaRepository,
                                    com.treinamento.repository.DepartamentoRepository departamentoRepository,
                                    org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            // -1. Cadastrar Empresas
            if (empresaRepository.count() == 0) {
                empresaRepository.save(new com.treinamento.model.Empresa("001", "GRUPO TREINAMENTO SA", "12345678000100"));
            }

            // 0. Cadastrar Filiais
            try {
                if (filialRepository.count() == 0) {
                    filialRepository.save(new com.treinamento.model.Filial("001", "001", "MATRIZ - SÃO PAULO", "AVENIDA PAULISTA", "1000", "SP", "SÃO PAULO", "12345678000199", null, null));
                    filialRepository.save(new com.treinamento.model.Filial("002", "001", "FILIAL - RIO DE JANEIRO", "RUA DA ASSEMBLEIA", "10", "RJ", "RIO DE JANEIRO", "12345678000200", null, null));
                } else {
                    // Atualiza filiais existentes que possam estar sem codEmpresa
                    filialRepository.findAll().forEach(f -> {
                        if (f.getCodEmpresa() == null || f.getCodEmpresa().isEmpty()) {
                            f.setCodEmpresa("001");
                            filialRepository.save(f);
                        }
                    });
                }
                
                // Agora que os dados estão migrados, podemos tornar codEmpresa obrigatório em futuras execuções se quisermos,
                // mas para esta sessão o nullable=true permitiu a criação da coluna.
            } catch (Exception e) {
                System.err.println("[SISTEMA] Erro ao carregar/atualizar filiais: " + e.getMessage());
            }

            // 0.0. Cadastrar Horários Iniciais
            if (horarioRepository.count() == 0) {
                horarioRepository.save(new com.treinamento.model.Horario("001", "ADMINISTRATIVO 08-18", "08:00", "12:00", "13:00", "18:00"));
                horarioRepository.save(new com.treinamento.model.Horario("002", "ADMINISTRATIVO 09-19", "09:00", "13:00", "14:00", "19:00"));
            }

            // 0.0.1. Cadastrar Abonos Iniciais
            if (abonoRepository.count() == 0) {
                abonoRepository.save(new com.treinamento.model.Abono("001", "001", "ATESTADO MEDICO", "SIM", "SIM"));
                abonoRepository.save(new com.treinamento.model.Abono("001", "002", "DECLARACAO", "NAO", "NAO"));
                abonoRepository.save(new com.treinamento.model.Abono("002", "001", "ATESTADO MEDICO", "SIM", "SIM"));
            }

            // 0.0.2. Cadastrar Horas Extras Iniciais
            if (horaExtraRepository.count() == 0) {
                horaExtraRepository.save(new com.treinamento.model.HoraExtra("001", "Feriado", "100%"));
                horaExtraRepository.save(new com.treinamento.model.HoraExtra("001", "DSR", "100%"));
                horaExtraRepository.save(new com.treinamento.model.HoraExtra("001", "Extras", "050%"));
                horaExtraRepository.save(new com.treinamento.model.HoraExtra("001", "Extras", "060%"));
                horaExtraRepository.save(new com.treinamento.model.HoraExtra("001", "Extras", "080%"));
                horaExtraRepository.save(new com.treinamento.model.HoraExtra("001", "Compensado", "070%"));
            }

            // 0.0.4. Cadastrar Departamentos Iniciais
            if (departamentoRepository.count() == 0) {
                departamentoRepository.save(new com.treinamento.model.Departamento("001", "001", "TI"));
                departamentoRepository.save(new com.treinamento.model.Departamento("001", "002", "RH"));
                departamentoRepository.save(new com.treinamento.model.Departamento("001", "003", "VENDAS"));
            }

            com.treinamento.model.Horario h1 = horarioRepository.findAll().stream().filter(h -> "ADMINISTRATIVO 08-18".equals(h.getDescricao())).findFirst().orElse(null);
            com.treinamento.model.Horario h2 = horarioRepository.findAll().stream().filter(h -> "ADMINISTRATIVO 09-19".equals(h.getDescricao())).findFirst().orElse(null);

            // 0.0.3. Cadastrar Escalas Iniciais
            if (escalaRepository.count() == 0) {
                if (h1 != null) {
                    escalaRepository.save(new com.treinamento.model.Escala("001", "001", h1.getId(), "Útil", "Útil", "Útil", "Útil", "Útil", "Compensado", "DSR"));
                }
                if (h2 != null) {
                    escalaRepository.save(new com.treinamento.model.Escala("002", "001", h2.getId(), "Útil", "Útil", "Útil", "Útil", "Útil", "Compensado", "DSR"));
                }
            }
            
            com.treinamento.model.Escala e1 = escalaRepository.findAll().stream().filter(e -> "001".equals(e.getFilial()) && "001".equals(e.getCodigo())).findFirst().orElse(null);
            com.treinamento.model.Escala e2 = escalaRepository.findAll().stream().filter(e -> "002".equals(e.getFilial()) && "001".equals(e.getCodigo())).findFirst().orElse(null);

            // 0. Cadastrar ou Atualizar Funcionário Padrão (Administrador)
            com.treinamento.model.Funcionario admin = funcionarioRepository.findByCpf("000")
                    .orElseGet(() -> {
                        com.treinamento.model.Funcionario f = new com.treinamento.model.Funcionario();
                        f.setFilial("001");
                        f.setMatricula("000001");
                        f.setCpf("000");
                        return f;
                    });
            
            if (h1 != null) admin.setIdHorario(h1.getId());
            if (e1 != null) admin.setIdEscala(e1.getId());
            admin.setNome("ADMINISTRADOR DO SISTEMA");
            // Senha padrão solicitada: admin
            String senhaCodificada = passwordEncoder.encode("admin");
            System.out.println("[DEBUG_LOG] Atualizando Admin. Senha plain: admin, Hash: " + senhaCodificada);
            admin.setSenha(senhaCodificada);
            admin.setPerfil("ADMIN");
            admin.setDepartamento("TI");
            admin.setTrocarSenha(false);
            funcionarioRepository.save(admin);

            // 0.1. Cadastrar ou Atualizar Funcionário Gestor
            com.treinamento.model.Funcionario gestor = funcionarioRepository.findByCpf("12345678900")
                    .orElseGet(() -> {
                        com.treinamento.model.Funcionario f = new com.treinamento.model.Funcionario();
                        f.setFilial("001");
                        f.setMatricula("000002");
                        f.setCpf("12345678900");
                        return f;
                    });

            if (h1 != null) gestor.setIdHorario(h1.getId());
            if (e1 != null) gestor.setIdEscala(e1.getId());
            gestor.setNome("GESTOR DE TESTE");
            // Senha inicial solicitada: 123
            gestor.setSenha(passwordEncoder.encode("123"));
            gestor.setPerfil("GESTOR");
            gestor.setDepartamento("RH");
            gestor.setTrocarSenha(true); // Solicitar nova senha no primeiro login
            funcionarioRepository.save(gestor);

            // 0.2. Cadastrar ou Atualizar Funcionário de Teste (Usuário Adicional)
            com.treinamento.model.Funcionario teste = funcionarioRepository.findByCpf("98765432100")
                    .orElseGet(() -> {
                        com.treinamento.model.Funcionario f = new com.treinamento.model.Funcionario();
                        f.setFilial("001");
                        f.setMatricula("000003");
                        f.setCpf("98765432100");
                        return f;
                    });

            if (h2 != null) teste.setIdHorario(h2.getId());
            if (e2 != null) teste.setIdEscala(e2.getId());
            teste.setNome("USUARIO DE TESTE");
            // Senha padrão conforme a nova regra: 3 primeiros dígitos do CPF (987)
            teste.setSenha(passwordEncoder.encode("987"));
            teste.setPerfil("COLABORADOR");
            teste.setDepartamento("VENDAS");
            teste.setTrocarSenha(true);
            funcionarioRepository.save(teste);

            // 1. Cadastrar Feriados Nacionais 2025
            if (feriadoRepository.count() == 0) {
                feriadoRepository.save(new com.treinamento.model.Feriado("Ano Novo", java.time.LocalDate.of(2025, 1, 1), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Carnaval", java.time.LocalDate.of(2025, 3, 3), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Carnaval", java.time.LocalDate.of(2025, 3, 4), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Sexta-feira Santa", java.time.LocalDate.of(2025, 4, 18), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Tiradentes", java.time.LocalDate.of(2025, 4, 21), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Dia do Trabalho", java.time.LocalDate.of(2025, 5, 1), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Corpus Christi", java.time.LocalDate.of(2025, 6, 19), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Independência", java.time.LocalDate.of(2025, 9, 7), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Nossa Sra Aparecida", java.time.LocalDate.of(2025, 10, 12), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Finados", java.time.LocalDate.of(2025, 11, 2), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Proclamação da República", java.time.LocalDate.of(2025, 11, 15), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Consciência Negra", java.time.LocalDate.of(2025, 11, 20), "N"));
                feriadoRepository.save(new com.treinamento.model.Feriado("Natal", java.time.LocalDate.of(2025, 12, 25), "N"));
            }

            // 2. Cadastrar Período Aquisitivo
            java.time.LocalDate inicio = java.time.LocalDate.of(2025, 1, 20);
            java.time.LocalDate fim = java.time.LocalDate.of(2025, 2, 19);
            if (periodoRepository.count() == 0) {
                Periodo p1 = new Periodo();
                p1.setFilial("001");
                p1.setDataInicio(inicio);
                p1.setDataFim(fim);
                p1.setAtivo(true);
                periodoRepository.save(p1);
            }

            // 2. Mock de Integrações
            if (repository.count() == 0) {
                Integracao i1 = new Integracao();
                i1.setNome("Integração Node.js");
                i1.setLinguagem("Node.js");
                i1.setStatus("Pendente");
                repository.save(i1);
            }

            // 3. Carga Aleatória de Marcações (Sem horários britânicos)
            if (marcacaoRepository.count() == 0) {
                java.util.Random random = new java.util.Random();
                
                // Gerar marcações para o Admin (000001)
                gerarMarcacoesPara(admin.getFilial(), admin.getMatricula(), inicio, fim, marcacaoRepository, random);
                
                // Gerar marcações para o Gestor (000002)
                gerarMarcacoesPara(gestor.getFilial(), gestor.getMatricula(), inicio, fim, marcacaoRepository, random);
                
                // Gerar marcações para o Teste (000003)
                gerarMarcacoesPara(teste.getFilial(), teste.getMatricula(), inicio, fim, marcacaoRepository, random);
                
                System.out.println("[SISTEMA] Carga de dados aleatórios concluída!");
            }
            System.out.println("[SISTEMA] Admin CPF: 000 | Gestor CPF: 12345678900 | Teste CPF: 98765432100");
            System.out.println("[SISTEMA] Regra de senha padrão: 3 primeiros dígitos do CPF");
            System.out.println("[SISTEMA] Acesse: http://localhost:8080/index.html");
        };
    }

    private void gerarMarcacoesPara(String filial, String matricula, java.time.LocalDate inicio, java.time.LocalDate fim, 
                                  com.treinamento.repository.MarcacaoRepository marcacaoRepository, java.util.Random random) {
        java.time.LocalDate dataAtual = inicio;
        while (!dataAtual.isAfter(fim)) {
            if (dataAtual.getDayOfWeek() != java.time.DayOfWeek.SUNDAY) {
                java.util.function.BiFunction<Integer, Integer, String> gerarHora = (h, m) -> {
                    int variacao = random.nextInt(21) - 10;
                    int totalMin = (h * 60) + m + variacao;
                    return String.format("%02d:%02d", totalMin / 60, totalMin % 60);
                };

                com.treinamento.model.Marcacao m = new com.treinamento.model.Marcacao(
                    filial, 
                    matricula, 
                    dataAtual,
                    gerarHora.apply(8, 0),
                    gerarHora.apply(12, 0),
                    gerarHora.apply(13, 0),
                    gerarHora.apply(18, 0),
                    "I"
                );
                marcacaoRepository.save(m);
            }
            dataAtual = dataAtual.plusDays(1);
        }
    }
}
/ /   B u i l d   t e s t  
 