# ConectaDoa — AEP MongoDB + Java 2026

O **ConectaDoa** é uma Prova de Conceito (PoC) desenvolvida para a AEP do curso de Engenharia de Software.

O projeto utiliza **Java, Spring Boot e MongoDB**, aplicando conceitos de programação orientada a objetos, banco de dados NoSQL, arquitetura em camadas, versionamento e testes automatizados.

---

## Problema e ODS

O desperdício de alimentos ocorre diariamente enquanto diversas pessoas e instituições enfrentam dificuldades para obter alimentos.

O ConectaDoa busca facilitar a conexão entre pessoas ou estabelecimentos que possuem alimentos disponíveis para doação e pessoas ou instituições interessadas em recebê-los.

O projeto está relacionado aos seguintes Objetivos de Desenvolvimento Sustentável da ONU:

- **ODS 2 — Fome Zero e Agricultura Sustentável**
- **ODS 12 — Consumo e Produção Responsáveis**

A aplicação contribui para o ODS 2 ao facilitar o direcionamento de alimentos a quem necessita e para o ODS 12 ao incentivar o reaproveitamento de alimentos que poderiam ser desperdiçados.

---

# Tecnologias utilizadas

- Java 21
- Spring Boot 3.5.16
- Spring Web
- Spring Data MongoDB
- MongoDB
- Jakarta Validation
- Maven
- Springdoc OpenAPI
- Swagger
- JUnit 5
- Mockito
- MockMvc
- JaCoCo
- Git
- GitHub

---

# Arquitetura da aplicação

O projeto foi dividido em camadas para separar as responsabilidades de cada parte da aplicação.

```text
JSON
  ↓
Controller
  ↓
Request DTO
  ↓
Service
  ↓
Repository
  ↓
MongoDB
```

No retorno da informação:

```text
MongoDB
  ↓
Model
  ↓
Service
  ↓
Mapper
  ↓
Response DTO
  ↓
Controller
  ↓
JSON
```

Essa organização facilita a manutenção, testes e evolução da aplicação.

---

# Organização dos packages

## Controller

Responsável por receber as requisições HTTP realizadas pelo usuário ou pelo front-end.

Exemplos:

```text
GET
POST
PUT
DELETE
```

O Controller encaminha essas requisições para a camada de Service.

Exemplo:

```java
@GetMapping
public List<DoacaoSummaryResponse> listar() {
    return service.listar();
}
```

Esse endpoint recebe uma requisição `GET` e retorna a lista de doações cadastradas.

---

## DTO

DTO significa **Data Transfer Object**.

Os DTOs são utilizados para controlar quais dados entram e saem da API.

No projeto são utilizados, entre outros:

```text
DoacaoCreateRequest
DoacaoUpdateRequest
DoacaoResponse
DoacaoSummaryResponse
ReservaRequest
```

### DoacaoCreateRequest

Utilizado para receber os dados necessários para criar uma doação.

Exemplo:

```json
{
  "alimento": "Arroz",
  "quantidade": 10,
  "unidade": "kg",
  "validade": "2026-09-20",
  "doadorNome": "Mercado Central",
  "doadorContato": "(44) 99999-9999"
}
```

### ReservaRequest

Utilizado para receber os dados da pessoa ou instituição que deseja reservar uma doação.

Exemplo:

```json
{
  "nome": "Instituto Esperança",
  "contato": "(44) 98888-8888"
}
```

---

# Model

A classe `Doacao` representa o principal objeto de domínio da aplicação.

Ela contém informações como:

```java
private String id;
private String alimento;
private Double quantidade;
private String unidade;
private LocalDate validade;
private String doadorNome;
private String doadorContato;
private StatusDoacao status;
private String reservadoPor;
private String contatoReserva;
```

Cada objeto `Doacao` representa um documento armazenado no MongoDB.

A anotação:

```java
@Document(collection = "doacoes")
```

indica que os objetos da classe serão armazenados na coleção:

```text
doacoes
```

---

# Status da doação

Para controlar o estado de cada doação foi criado o enum `StatusDoacao`.

```java
public enum StatusDoacao {
    DISPONIVEL,
    RESERVADA,
    RETIRADA
}
```

O uso de um `enum` limita os estados possíveis da doação, evitando valores inconsistentes.

Os estados são:

### DISPONIVEL

A doação está disponível para ser reservada.

### RESERVADA

A doação já foi reservada por uma pessoa ou instituição.

### RETIRADA

A doação já foi retirada pelo responsável.

---

# Mapper

O Mapper é responsável por converter objetos entre o modelo da aplicação e os DTOs.

Exemplo de fluxo:

```text
DoacaoCreateRequest
        ↓
      Mapper
        ↓
      Doacao
```

E no retorno:

```text
Doacao
   ↓
 Mapper
   ↓
DoacaoResponse
```

Ao cadastrar uma nova doação, o Mapper também define automaticamente:

```java
StatusDoacao.DISPONIVEL
```

Dessa forma, o usuário não precisa informar manualmente o status inicial.

---

# Service

O Service é responsável pelas **regras de negócio** da aplicação.

Uma das principais regras implementadas é a reserva de doações.

Exemplo:

```java
public DoacaoResponse reservar(
        String id,
        ReservaRequest request) {

    Doacao doacao = buscarModelPorId(id);

    if (doacao.getStatus() != StatusDoacao.DISPONIVEL) {
        throw new RuntimeException(
                "Esta doação não está disponível."
        );
    }

    doacao.setReservadoPor(request.nome());
    doacao.setContatoReserva(request.contato());
    doacao.setStatus(StatusDoacao.RESERVADA);

    return mapper.toResponse(
            repository.save(doacao)
    );
}
```

O fluxo dessa regra é:

```text
Buscar doação
      ↓
Verificar status
      ↓
Está DISPONIVEL?
   ↓        ↓
  Sim      Não
   ↓        ↓
Reservar   Erro
   ↓
Alterar status para RESERVADA
```

Isso impede que uma mesma doação seja reservada mais de uma vez.

---

# Repository

O Repository é responsável pela comunicação com o MongoDB.

A interface utiliza Spring Data MongoDB:

```java
public interface DoacaoRepository
        extends MongoRepository<Doacao, String> {
}
```

Ao estender `MongoRepository`, a aplicação já recebe métodos prontos para operações como:

```text
save()
findAll()
findById()
delete()
```

Também foi criada uma consulta específica:

```java
List<Doacao> findByStatus(StatusDoacao status);
```

O Spring Data interpreta o nome `findByStatus` e gera automaticamente a consulta necessária no MongoDB.

Essa consulta é utilizada, por exemplo, para localizar apenas as doações disponíveis.

---

# Banco de dados NoSQL

O projeto utiliza **MongoDB**, um banco de dados NoSQL orientado a documentos.

Banco utilizado:

```text
conectadoa
```

O projeto utiliza apenas uma coleção:

```text
doacoes
```

Exemplo de documento armazenado:

```json
{
  "_id": "68...",
  "alimento": "Arroz",
  "quantidade": 10,
  "unidade": "kg",
  "validade": "2026-09-20",
  "doadorNome": "Mercado Central",
  "doadorContato": "(44) 99999-9999",
  "status": "DISPONIVEL",
  "reservadoPor": null,
  "contatoReserva": null
}
```

Após uma reserva:

```json
{
  "alimento": "Arroz",
  "status": "RESERVADA",
  "reservadoPor": "Instituto Esperança",
  "contatoReserva": "(44) 98888-8888"
}
```

---

# Endpoints da API

| Método | Endpoint | Função |
|---|---|---|
| GET | `/api/doacoes` | Lista todas as doações |
| GET | `/api/doacoes/{id}` | Busca uma doação pelo ID |
| GET | `/api/doacoes/disponiveis` | Lista somente doações disponíveis |
| POST | `/api/doacoes` | Cadastra uma nova doação |
| PUT | `/api/doacoes/{id}` | Atualiza uma doação |
| PUT | `/api/doacoes/{id}/reservar` | Reserva uma doação |
| DELETE | `/api/doacoes/{id}` | Exclui uma doação |

---

# Cadastro de uma doação

Endpoint:

```text
POST /api/doacoes
```

Exemplo:

```json
{
  "alimento": "Arroz",
  "quantidade": 10,
  "unidade": "kg",
  "validade": "2026-09-20",
  "doadorNome": "Mercado Central",
  "doadorContato": "(44) 99999-9999"
}
```

Ao criar uma nova doação, o sistema define automaticamente:

```json
"status": "DISPONIVEL"
```

---

# Listagem de doações disponíveis

Endpoint:

```text
GET /api/doacoes/disponiveis
```

Esse endpoint utiliza a consulta:

```java
findByStatus(StatusDoacao.DISPONIVEL)
```

Portanto, somente doações com status:

```text
DISPONIVEL
```

são retornadas.

---

# Reserva de uma doação

Endpoint:

```text
PUT /api/doacoes/{id}/reservar
```

Exemplo:

```json
{
  "nome": "Instituto Esperança",
  "contato": "(44) 98888-8888"
}
```

Após a reserva:

```json
{
  "status": "RESERVADA",
  "reservadoPor": "Instituto Esperança",
  "contatoReserva": "(44) 98888-8888"
}
```

Depois disso, a doação deixa de aparecer no endpoint:

```text
/api/doacoes/disponiveis
```

---

# Tratamento de erros

O sistema possui tratamento de exceções para situações como busca de uma doação inexistente.

Exemplo:

```text
Doação não encontrada
```

Também são realizadas validações dos dados enviados para evitar cadastros inválidos.

---

# Interface Web

O projeto possui uma interface web para facilitar a visualização e utilização da PoC.

Página inicial:

```text
http://localhost:8081/
```

CRUD Web:

```text
http://localhost:8081/crud.html
```

---

# Swagger / OpenAPI

A documentação interativa da API está disponível através do Swagger.

Acesso:

```text
http://localhost:8081/docs
```

Através do Swagger é possível testar diretamente os métodos:

```text
GET
POST
PUT
DELETE
```

sem necessidade de utilizar outro cliente HTTP.

---

# Execução

## Pré-requisitos

É necessário possuir:

- Java 21
- MongoDB
- Maven ou suporte Maven pelo IntelliJ

O MongoDB deve estar disponível em:

```text
localhost:27017
```

Depois execute a classe:

```text
ConectaDoaApplication
```

A aplicação utilizará a porta:

```text
8081
```

Acesse:

```text
http://localhost:8081/
```

---

# Testes automatizados

A aplicação possui testes automatizados das principais funcionalidades.

Foram utilizados:

- **JUnit 5** — criação e execução dos testes;
- **Mockito** — simulação das dependências;
- **MockMvc** — simulação de requisições HTTP;
- **JaCoCo** — medição da cobertura dos testes.

Os testes estão principalmente nas classes:

```text
DoacaoControllerTest
DoacaoServiceTest
```

---

## Testes do Controller

Os testes do Controller verificam comportamentos como:

```text
deveListar
deveListarDisponiveis
deveBuscar
deveRetornar404
deveCriar
deveRejeitarCriacaoInvalida
deveAtualizar
deveReservar
deveExcluir
```

Esses testes simulam requisições HTTP utilizando `MockMvc`.

Exemplo:

```java
mockMvc.perform(get("/api/doacoes"))
        .andExpect(status().isOk());
```

Esse teste verifica se o endpoint retorna uma resposta HTTP de sucesso.

---

## Testes do Service

Os testes do Service verificam as regras de negócio.

Entre eles:

```text
deveListar
deveListarDisponiveis
deveBuscar
deveCriar
deveAtualizar
deveReservar
naoDeveReservarDoacaoIndisponivel
deveExcluir
deveFalharQuandoNaoEncontrar
```

Um teste importante é:

```text
naoDeveReservarDoacaoIndisponivel
```

Ele garante que uma doação que já está reservada não possa ser reservada novamente.

---

# Executando os testes

No Maven:

```bash
mvn clean verify
```

Também é possível executar pelo IntelliJ:

```text
Maven
→ Lifecycle
→ verify
```

---

# Cobertura de testes

A cobertura dos testes é medida pelo **JaCoCo**.

O requisito mínimo da AEP é:

```text
70%
```

Na versão atual do projeto, a cobertura total obtida foi:

```text
88%
```

O relatório apresentou cobertura de **100%** nas principais camadas da aplicação, incluindo:

```text
Service
Model
DTO
Mapper
Controller
```

O relatório pode ser acessado em:

```text
target/site/jacoco/index.html
```

O JaCoCo também está configurado para verificar automaticamente a cobertura mínima exigida.

Caso a cobertura fique abaixo de 70%, a verificação Maven pode falhar.

---

# Programação Orientada a Objetos

A aplicação utiliza conceitos de orientação a objetos através de:

- classes e objetos;
- encapsulamento;
- construtores;
- getters e setters;
- enum;
- separação de responsabilidades;
- interfaces;
- organização em camadas.

A classe `Doacao`, por exemplo, representa uma entidade do domínio do sistema.

---

# Versionamento

O projeto utiliza Git e GitHub para controle de versão.

O repositório contém:

- código-fonte;
- testes;
- documentação;
- histórico de commits;
- evolução incremental da PoC.

Os commits foram realizados pelos integrantes do grupo durante o desenvolvimento.

---

# Fluxo principal da aplicação

```text
Doador cadastra alimento
        ↓
Doação recebe status DISPONIVEL
        ↓
A doação aparece na lista de disponíveis
        ↓
Pessoa ou instituição realiza a reserva
        ↓
Sistema registra nome e contato
        ↓
Status passa para RESERVADA
        ↓
A doação deixa de aparecer entre as disponíveis
```

---

# Objetivo da PoC

A primeira versão do ConectaDoa demonstra que é possível utilizar tecnologia para facilitar a conexão entre doadores e interessados em receber alimentos, utilizando uma solução simples baseada em Java, Spring Boot e MongoDB.

O projeto demonstra a aplicação prática de:

- Banco de Dados NoSQL;
- Programação Orientada a Objetos;
- Processo de Software;
- Projeto e Implementação;
- Testes Automatizados;
- Versionamento;
- Documentação.
