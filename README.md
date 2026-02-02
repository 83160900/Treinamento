# Projeto de Treinamento e Integração Protheus

Este projeto demonstra a criação de um servidor central em Java (Spring Boot) com banco de dados H2, e as bases para integração com Node.js, Python e Frontend (Angular/PO-UI).

## Estrutura do Projeto

- **/src/main/java**: Contém o servidor Spring Boot.
  - `TreinamentoApplication`: Inicializa a aplicação e popula dados iniciais.
  - `IntegrationController`: Endpoints REST (`/api/status`, `/api/integracoes`) para comunicação.
  - `Integracao` (Model) e `IntegracaoRepository` (JPA): Gerenciamento de persistência.
- **/python-integration**: Script Python (`main.py`) que demonstra como consumir a API do servidor Java para integrar dados.
- **/node-integration**: Estrutura preparada para serviços em Node.js.
- **/angular-poui**: Estrutura preparada para o frontend em Angular utilizando a biblioteca de componentes PO-UI da TOTVS.

## Como Executar

### Servidor Java
1. Certifique-se de ter o Maven instalado.
2. Execute o comando: `./mvnw spring-boot:run` ou `mvn spring-boot:run`.
3. O servidor estará disponível em `http://localhost:8080`.
4. Console do Banco H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:treinamentodb`).

### Integração Python
1. Navegue até a pasta `python-integration`.
2. Certifique-se de ter a biblioteca `requests` instalada (`pip install requests`).
3. Execute: `python main.py`.

## Integração com Protheus
O servidor Java atua como um middleware. O Protheus (via AdvPL) pode consumir os endpoints REST do Java para ler ou persistir dados no banco central, enquanto scripts Python e serviços Node.js fazem o mesmo, permitindo uma arquitetura multicamadas e multi-linguagem.
