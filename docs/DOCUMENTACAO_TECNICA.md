# Documentação técnica — ConectaDoa

## 1. Visão geral

O ConectaDoa é uma Prova de Conceito que liga quem tem alimento para doar (mercados, padarias, pessoas) a instituições que podem receber esse alimento.

O fluxo principal é:

1. o doador é cadastrado, com um ou mais endereços;
2. a instituição é cadastrada;
3. o doador registra uma doação (status `DISPONIVEL`);
4. uma instituição reserva a doação (status `RESERVADA`);
5. quando o alimento é buscado, a retirada é registrada (status `RETIRADA`).

## 2. O que mudou da 1ª para a 2ª entrega

| | 1ª entrega | 2ª entrega |
|---|---|---|
| Coleções | só `doacoes` | `doacoes`, `doadores` e `instituicoes` |
| Doador | nome e contato repetidos em cada doação | documento próprio, referenciado por `doadorId` |
| Endereços | não existiam | lista de subdocumentos no doador e subdocumento na instituição |
| Reserva | campos soltos com texto digitado | subdocumento com referência à instituição e datas |
| Retirada | status existia, mas não havia como chegar nele | `PUT /api/doacoes/{id}/retirar` |
| Erros de regra | erro 500 (`RuntimeException`) | erro 409 com mensagem clara |
| Telas | só doações | doações, doadores e instituições |

Também corrigimos três coisas da primeira versão: o nome do banco não estava configurado (a aplicação usava o banco `test`), o link do Swagger na página inicial estava errado e as exclusões do JaCoCo apontavam para um pacote que não existe.

## 3. Arquitetura

```text
Navegador / Swagger
        |  JSON
   Controller      recebe a requisição e valida os dados
        |
     Service       coordena o caso de uso e confere os relacionamentos
        |
  Model + Repository
        |
     MongoDB
```

| Pacote | Responsabilidade |
|---|---|
| `controller` | endpoints REST |
| `dto` | dados de entrada e saída da API |
| `service` | casos de uso e verificação das referências |
| `model` | documentos do banco e regras da doação |
| `repository` | acesso ao MongoDB com Spring Data |
| `mapper` | conversão entre Model e DTO |
| `exception` | exceções e respostas de erro |
| `configuration` | Swagger e dados de exemplo |

## 4. Orientação a objetos

- **Encapsulamento:** as regras de reserva e retirada ficam na classe `Doacao`, nos métodos `reservar()` e `registrarRetirada()`. O status não tem setter, então só muda por esses métodos.
- **Herança:** `DoacaoNotFoundException`, `DoadorNotFoundException` e `InstituicaoNotFoundException` herdam de `RecursoNaoEncontradoException`. O `GlobalExceptionHandler` trata a classe mãe, e todas viram erro 404.
- **Composição:** `Doador` tem uma lista de `Endereco`, `Instituicao` tem um `Endereco` e `Doacao` tem uma `Reserva`.
- **Responsabilidade única:** cada camada faz uma coisa só, como mostra a tabela da seção 3.
- **Injeção de dependência:** os services recebem os repositories pelo construtor, o que permite usar mocks nos testes.

## 5. Banco de dados

Banco: `conectadoa` (MongoDB).

```text
doadores  1 ──── N  doacoes  N ──── 0..1  instituicoes
         (doadorId)         (reserva.instituicaoId)
```

### Coleção `doadores`

Tem uma lista de subdocumentos em `enderecos`.

```json
{
  "_id": "mercado-central",
  "nome": "Mercado Central",
  "contato": "(44) 99999-9999",
  "enderecos": [
    { "rua": "Av. Brasil, 100", "cidade": "Maringá", "estado": "PR" },
    { "rua": "Rua das Flores, 25", "cidade": "Sarandi", "estado": "PR" }
  ]
}
```

### Coleção `instituicoes`

Tem um subdocumento em `endereco`.

```json
{
  "_id": "instituto-esperanca",
  "nome": "Instituto Esperança",
  "contato": "(44) 98888-8888",
  "endereco": { "rua": "Rua Paraná, 300", "cidade": "Maringá", "estado": "PR" }
}
```

### Coleção `doacoes`

Referencia o doador por `doadorId`. Depois de reservada, ganha o subdocumento `reserva`, que referencia a instituição.

```json
{
  "_id": "arroz-10kg",
  "alimento": "Arroz",
  "quantidade": 10.0,
  "unidade": "kg",
  "validade": "2026-12-05",
  "doadorId": "mercado-central",
  "status": "RESERVADA",
  "reserva": {
    "instituicaoId": "instituto-esperanca",
    "dataReserva": "2026-11-05",
    "dataRetirada": null
  }
}
```

No banco, as datas ficam gravadas como tipo data. Aqui elas aparecem como texto só para facilitar a leitura.

### Por que referência em uns casos e subdocumento em outros

- **Referência (`doadorId`, `instituicaoId`):** doadores e instituições têm vida própria e aparecem em várias doações. Se o telefone mudar, basta atualizar em um lugar.
- **Subdocumento (`enderecos`, `reserva`):** esses dados só fazem sentido junto do documento principal e são sempre lidos com ele.

O MongoDB não garante essas referências sozinho, como faria uma chave estrangeira. Por isso a aplicação faz a verificação:

- não cria doação com doador inexistente;
- não reserva para instituição inexistente;
- não exclui doador que tem doações;
- não exclui instituição que tem reservas.

## 6. Regras de negócio

| Regra | Resposta |
|---|---|
| Toda doação nova começa como `DISPONIVEL` | — |
| Só doação `DISPONIVEL` pode ser reservada | 409 |
| Doação vencida não pode ser reservada | 409 |
| Só doação `RESERVADA` pode ser retirada | 409 |
| Doador com doações não pode ser excluído | 409 |
| Instituição com reservas não pode ser excluída | 409 |
| Doador, instituição ou doação inexistente | 404 |
| Campos obrigatórios vazios, quantidade zero ou validade passada | 400 |
| Doador precisa ter pelo menos um endereço | 400 |

## 7. API

A lista completa também pode ser vista no Swagger: http://localhost:8080/swagger-ui.html

### Doações — `/api/doacoes`

| Método | Caminho | Função |
|---|---|---|
| GET | `/api/doacoes` | lista resumida |
| GET | `/api/doacoes/disponiveis` | só as disponíveis |
| GET | `/api/doacoes/doador/{doadorId}` | doações de um doador |
| GET | `/api/doacoes/{id}` | detalhes, com dados do doador e da reserva |
| POST | `/api/doacoes` | cria |
| PUT | `/api/doacoes/{id}` | atualiza |
| PUT | `/api/doacoes/{id}/reservar` | reserva para uma instituição |
| PUT | `/api/doacoes/{id}/retirar` | registra a retirada |
| DELETE | `/api/doacoes/{id}` | exclui |

### Doadores — `/api/doadores` e instituições — `/api/instituicoes`

| Método | Caminho | Função |
|---|---|---|
| GET | `/` | lista |
| GET | `/{id}` | busca por id |
| POST | `/` | cria |
| PUT | `/{id}` | atualiza |
| DELETE | `/{id}` | exclui |

## 8. Testes

| Tipo | Classes | O que verifica |
|---|---|---|
| Unitário (Mockito) | `DoacaoServiceTest`, `DoadorServiceTest`, `InstituicaoServiceTest` | regras de negócio |
| Web (MockMvc) | `DoacaoControllerTest`, `DoadorControllerTest`, `InstituicaoControllerTest` | endpoints, validação e códigos de erro |
| Integração | `FluxoDoacaoIntegrationTest` | fluxo completo e o que fica gravado em cada coleção |
| Contexto | `ApplicationTests` | se a aplicação sobe |

O teste de integração usa o `mongo-java-server`, um servidor compatível com o MongoDB que roda em memória. Assim os testes funcionam sem instalar o MongoDB nem o Docker.

Para rodar os testes e gerar a cobertura:

```bash
./mvnw clean verify
```

No Windows, use `mvnw.cmd clean verify`. O relatório fica em `target/site/jacoco/index.html`.

O build falha se a cobertura de linhas ficar abaixo de 70%. As classes `Application` e `configuration` ficam fora da medição, porque não têm regra de negócio.

## 9. Limitações

- Não há login: qualquer pessoa com acesso ao sistema consegue reservar ou excluir.
- Não dá para cancelar uma reserva.
- Dois pedidos de reserva exatamente ao mesmo tempo não são tratados de forma especial.
- Como é uma PoC, o sistema não foi testado com usuários reais.
