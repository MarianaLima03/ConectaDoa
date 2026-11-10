# ConectaDoa — AEP 2026.2

Sistema para conectar quem tem alimentos para doar a instituições que podem recebê-los. É a Prova de Conceito da AEP de Engenharia de Software (6º semestre).

## Problema e ODS

Muito alimento em bom estado é jogado fora enquanto outras pessoas passam necessidade. Mercados e padarias costumam ter sobras, mas nem sempre têm um jeito simples de avisar instituições que poderiam buscar esses alimentos.

- **ODS 2 — Fome Zero e Agricultura Sustentável:** facilita a chegada dos alimentos a quem precisa.
- **ODS 12 — Consumo e Produção Responsáveis:** ajuda a reduzir o desperdício.

## Tecnologias

- Java 21
- Spring Boot 3.5.16 (Web, Data MongoDB e Validation)
- MongoDB
- Springdoc OpenAPI (Swagger)
- HTML, CSS e JavaScript
- JUnit 5, Mockito, MockMvc e mongo-java-server
- JaCoCo
- Maven (com Maven Wrapper)

## Como executar

Você precisa ter instalado:

- Java 21 (JDK);
- MongoDB rodando em `localhost:27017`.

Na pasta do projeto, para subir com dados de exemplo:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

No Windows (CMD ou PowerShell), troque `./mvnw` por `mvnw.cmd`. No PowerShell, coloque o `-D...` entre aspas:

```bash
mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Para subir sem dados de exemplo, tire o `-Dspring-boot.run.profiles=dev`.

Depois é só acessar:

| Página | Endereço |
|---|---|
| Início | http://localhost:8080/ |
| Doações | http://localhost:8080/crud.html |
| Doadores | http://localhost:8080/doadores.html |
| Instituições | http://localhost:8080/instituicoes.html |
| Swagger | http://localhost:8080/swagger-ui.html |

## Como usar

1. Cadastre um doador, com pelo menos um endereço.
2. Cadastre uma instituição.
3. Em Doações, cadastre o alimento e escolha o doador.
4. Escolha a instituição e clique em **Reservar**.
5. Quando o alimento for buscado, clique em **Registrar retirada**.

O status da doação segue este caminho:

```text
DISPONIVEL -> RESERVADA -> RETIRADA
```

## Estrutura do banco

Banco `conectadoa`, com três coleções:

| Coleção | Conteúdo | Tipo de dado |
|---|---|---|
| `doadores` | nome, contato e endereços | lista de subdocumentos em `enderecos` |
| `instituicoes` | nome, contato e endereço | subdocumento em `endereco` |
| `doacoes` | alimento, quantidade, validade, status e reserva | referência `doadorId` e subdocumento `reserva` com `instituicaoId` |

Exemplos de documentos e a explicação da modelagem estão na [documentação técnica](docs/DOCUMENTACAO_TECNICA.md).

## Exemplo de uso da API

Criar um doador em `POST /api/doadores`:

```json
{
  "nome": "Mercado Central",
  "contato": "(44) 99999-9999",
  "enderecos": [
    { "rua": "Av. Brasil, 100", "cidade": "Maringá", "estado": "PR" }
  ]
}
```

Criar uma doação em `POST /api/doacoes`:

```json
{
  "alimento": "Arroz",
  "quantidade": 10,
  "unidade": "kg",
  "validade": "2027-03-20",
  "doadorId": "ID_DO_DOADOR"
}
```

Reservar em `PUT /api/doacoes/{id}/reservar`:

```json
{
  "instituicaoId": "ID_DA_INSTITUICAO"
}
```

Registrar a retirada em `PUT /api/doacoes/{id}/retirar`, sem corpo.

## Testes e cobertura

Os testes não precisam do MongoDB instalado, porque o teste de integração usa um servidor em memória.

```bash
./mvnw clean verify
```

O relatório do JaCoCo fica em `target/site/jacoco/index.html`.

- Mínimo exigido na AEP: 70% (o build falha abaixo disso).
- Resultado desta versão: 62 testes passando e 100% de cobertura de linhas.

As classes `Application` e `configuration` ficam fora da medição, porque não têm regra de negócio.

## Organização do código

```text
src/main/java/com/AEP/_BIM
├── controller      endpoints REST
├── dto             dados de entrada e saída
├── service         casos de uso
├── model           documentos do banco e regras da doação
├── repository      acesso ao MongoDB
├── mapper          conversão entre Model e DTO
├── exception       erros da aplicação
└── configuration   Swagger e dados de exemplo
```

## Metodologia de trabalho

Usamos Kanban, com um quadro de tarefas nas colunas A fazer, Fazendo e Feito.

Quadro de tarefas: https://github.com/users/MarianaLima03/projects/2

## Versões

| Entrega | Tag | Principais pontos |
|---|---|---|
| 1ª | `v1.0` | CRUD de doações com uma coleção |
| 2ª | `v2.0` | três coleções relacionadas, documentos aninhados, fluxo de retirada e novas telas |
