# ConectaDoa — AEP MongoDB + Java 2026

Projeto baseado na mesma arquitetura didática do exemplo da disciplina, adaptado para o tema **doação de alimentos**.

## Problema e ODS
O ConectaDoa busca reduzir o desperdício de alimentos e facilitar a divulgação de doações para pessoas e instituições interessadas.

- ODS 2 — Fome Zero e Agricultura Sustentável
- ODS 12 — Consumo e Produção Responsáveis

## Stack
- Java 21
- Spring Boot 3.5.16
- Spring Web
- Spring Data MongoDB
- Jakarta Validation
- Springdoc OpenAPI / Swagger
- JUnit 5 / Mockito / MockMvc
- JaCoCo

## Arquitetura

```text
JSON -> Controller -> Request DTO -> Service -> Repository -> MongoDB
MongoDB -> Model -> Service -> Mapper -> Response DTO -> Controller -> JSON
```

## Banco NoSQL
Banco: `conectadoa`

Única coleção: `doacoes`

## Execução
Com o MongoDB local em `localhost:27017`, execute a aplicação `ConectaDoaApplication`.

A aplicação usa a porta `8080`.

- Página inicial: http://localhost:8080/
- CRUD web: http://localhost:8080/crud.html
- Swagger: http://localhost:8080/swagger-ui.html

## Endpoints
| Método | Endpoint | Função |
|---|---|---|
| GET | `/api/doacoes` | Lista resumida |
| GET | `/api/doacoes/{id}` | Busca completa |
| GET | `/api/doacoes/disponiveis` | Lista doações disponíveis |
| POST | `/api/doacoes` | Cria uma doação |
| PUT | `/api/doacoes/{id}` | Atualiza uma doação |
| PUT | `/api/doacoes/{id}/reservar` | Reserva uma doação |
| DELETE | `/api/doacoes/{id}` | Exclui uma doação |

## Exemplo POST

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

Toda nova doação é cadastrada com status inicial:

```text
DISPONIVEL
```

## Status da doação

Os possíveis status são:

```text
DISPONIVEL
RESERVADA
RETIRADA
```

## Reserva

Exemplo de reserva:

```json
{
  "nome": "Instituto Esperança",
  "contato": "(44) 98888-8888"
}
```

Após a reserva, o status da doação passa para:

```text
RESERVADA
```

## Organização do código

O projeto está dividido em camadas:

- `controller` — recebe as requisições HTTP;
- `dto` — define os dados de entrada e saída;
- `service` — concentra as regras de negócio;
- `repository` — realiza o acesso ao MongoDB;
- `mapper` — converte Model e DTO;
- `model` — representa os objetos da aplicação;
- `exception` — trata erros e exceções;
- `configuration` — configura componentes da aplicação.

## Model

A classe principal do projeto é `Doacao`.

Exemplo:

```java
@Document(collection = "doacoes")
public class Doacao {
```

A anotação indica que os objetos serão armazenados na coleção:

```text
doacoes
```

## Repository

O acesso ao MongoDB é realizado pelo Spring Data:

```java
public interface DoacaoRepository
        extends MongoRepository<Doacao, String> {
}
```

Também existe a consulta por status:

```java
List<Doacao> findByStatus(StatusDoacao status);
```

Ela é utilizada para localizar apenas as doações que estão disponíveis.

## Service

O `Service` contém as principais regras de negócio.

Na reserva, por exemplo, o sistema verifica se a doação está disponível:

```java
if (doacao.getStatus() != StatusDoacao.DISPONIVEL) {
    throw new RuntimeException(
        "Esta doação não está disponível."
    );
}
```

Se estiver disponível, os dados da reserva são registrados e o status é alterado:

```java
doacao.setReservadoPor(request.nome());
doacao.setContatoReserva(request.contato());
doacao.setStatus(StatusDoacao.RESERVADA);
```

## Testes e cobertura

Os testes automatizados utilizam:

- JUnit 5
- Mockito
- MockMvc

Para executar:

```bash
mvn clean verify
```

A cobertura é medida pelo JaCoCo.

O requisito mínimo da AEP é:

```text
70%
```

A cobertura atual do projeto é:

```text
88%
```

O relatório pode ser acessado em:

```text
target/site/jacoco/index.html
```

## Versionamento

O projeto utiliza Git e GitHub para controle de versão e registro da evolução da PoC.
