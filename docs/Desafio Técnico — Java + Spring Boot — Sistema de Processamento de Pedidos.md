# Desafio Técnico — Java + Spring Boot
## Sistema REST para Processamento de Pedidos

### 1. Objetivo

Desenvolver uma aplicação backend utilizando **Java e Spring Boot** para realizar o cadastro, consulta e gerenciamento de clientes, produtos e pedidos por meio de uma **API REST**.

A aplicação deverá permitir a criação de pedidos contendo múltiplos produtos, calcular o valor total da compra, controlar o estoque dos produtos e gerenciar o ciclo de vida dos pedidos.

O projeto deverá ser desenvolvido considerando boas práticas de organização de código, separação de responsabilidades, persistência de dados e tratamento adequado de erros.

---

### 2. Requisitos funcionais

**RF01 — Cadastrar cliente:** permitir o cadastro de um novo cliente.

**RF02 — Consultar cliente:** permitir a consulta de um cliente por seu identificador.

**RF03 — Listar clientes:** permitir a consulta dos clientes cadastrados.

**RF04 — Atualizar cliente:** permitir a alteração dos dados de um cliente existente.

**RF05 — Excluir cliente:** permitir a exclusão de um cliente existente, respeitando as regras de integridade do sistema.

**RF06 — Cadastrar produto:** permitir o cadastro de um novo produto.

**RF07 — Consultar produto:** permitir a consulta de um produto por seu identificador.

**RF08 — Listar produtos:** permitir a consulta dos produtos cadastrados.

**RF09 — Atualizar produto:** permitir a alteração dos dados de um produto existente.

**RF10 — Excluir produto:** permitir a exclusão de um produto existente.

**RF11 — Controlar estoque:** armazenar e atualizar a quantidade disponível de cada produto.

**RF12 — Criar pedido:** permitir a criação de um novo pedido associado a um cliente e contendo um ou mais produtos.

**RF13 — Consultar pedido:** permitir a consulta de um pedido por seu identificador.

**RF14 — Listar pedidos:** permitir a consulta dos pedidos cadastrados.

**RF15 — Processar pedido:** permitir que um pedido seja encaminhado para processamento.

**RF16 — Cancelar pedido:** permitir o cancelamento de um pedido quando sua situação permitir.

**RF17 — Calcular total:** calcular automaticamente o valor total de um pedido com base nos produtos e quantidades informados.

**RF18 — Validar estoque:** impedir a criação ou processamento de pedidos quando não houver estoque suficiente para os produtos solicitados.

**RF19 — Gerenciar status:** controlar o estado do pedido de acordo com as transições permitidas.

**RF20 — Consultar pedidos por cliente:** permitir a consulta dos pedidos pertencentes a um determinado cliente.

**RF21 — Resumo de compras do cliente:** disponibilizar informações agregadas sobre os pedidos de um cliente, incluindo quantidade de pedidos, valor total gasto, valor médio dos pedidos e produtos mais comprados.

---

### 3. Dados do sistema

#### Cliente

Cada cliente deve possuir, no mínimo, os seguintes dados:

| Campo | Tipo sugerido | Descrição |
|---|---|---|
| id | UUID | Identificador único do cliente. |
| name | String | Nome do cliente. |
| email | String | E-mail do cliente. |

#### Produto

Cada produto deve possuir, no mínimo, os seguintes dados:

| Campo | Tipo sugerido | Descrição |
|---|---|---|
| id | UUID | Identificador único do produto. |
| name | String | Nome do produto. |
| price | BigDecimal | Preço unitário do produto. |
| stock | Integer | Quantidade disponível em estoque. |

#### Pedido

Cada pedido deve possuir, no mínimo, os seguintes dados:

| Campo | Tipo sugerido | Descrição |
|---|---|---|
| id | UUID | Identificador único do pedido. |
| customer | Customer | Cliente responsável pelo pedido. |
| items | List\<OrderItem> | Produtos e quantidades presentes no pedido. |
| status | OrderStatus | Estado atual do pedido. |
| createdAt | LocalDateTime | Data e hora de criação do pedido. |
| total | BigDecimal | Valor total do pedido. |

#### Item do pedido

Cada item de pedido deve possuir, no mínimo, os seguintes dados:

| Campo | Tipo sugerido | Descrição |
|---|---|---|
| id | UUID | Identificador único do item. |
| product | Product | Produto associado ao item. |
| quantity | Integer | Quantidade solicitada. |
| unitPrice | BigDecimal | Preço do produto no momento da criação do pedido. |
| subtotal | BigDecimal | Resultado da multiplicação do preço unitário pela quantidade. |

---

### 4. Status do pedido

O pedido deverá possuir um status que represente seu ciclo de vida.

Os estados mínimos são:

```text
CREATED
PROCESSING
COMPLETED
CANCELLED
```

As transições deverão respeitar as seguintes regras:

| Estado atual | Próximo estado | Permitido |
|---|---|---|
| CREATED | PROCESSING | Sim |
| CREATED | CANCELLED | Sim |
| PROCESSING | COMPLETED | Sim |
| PROCESSING | CANCELLED | Sim |
| COMPLETED | CANCELLED | Não |
| COMPLETED | PROCESSING | Não |
| CANCELLED | PROCESSING | Não |
| CANCELLED | COMPLETED | Não |

A aplicação deverá impedir transições inválidas e retornar uma resposta HTTP apropriada quando uma operação não puder ser realizada.

---

### 5. API REST

A aplicação deverá disponibilizar endpoints REST para as operações de gerenciamento de clientes, produtos e pedidos.

A estrutura abaixo é uma sugestão; o candidato pode organizar os endpoints de forma equivalente, desde que os requisitos sejam atendidos.

#### Clientes

| Método | Endpoint | Finalidade |
|---|---|---|
| POST | `/customers` | Cadastrar um cliente. |
| GET | `/customers/{id}` | Consultar um cliente pelo ID. |
| GET | `/customers` | Listar clientes. |
| PUT | `/customers/{id}` | Atualizar um cliente. |
| DELETE | `/customers/{id}` | Excluir um cliente. |

#### Produtos

| Método | Endpoint | Finalidade |
|---|---|---|
| POST | `/products` | Cadastrar um produto. |
| GET | `/products/{id}` | Consultar um produto pelo ID. |
| GET | `/products` | Listar produtos. |
| PUT | `/products/{id}` | Atualizar um produto. |
| DELETE | `/products/{id}` | Excluir um produto. |

#### Pedidos

| Método | Endpoint | Finalidade |
|---|---|---|
| POST | `/orders` | Criar um pedido. |
| GET | `/orders/{id}` | Consultar um pedido pelo ID. |
| GET | `/orders` | Listar pedidos. |
| POST | `/orders/{id}/process` | Processar um pedido. |
| POST | `/orders/{id}/cancel` | Cancelar um pedido. |

#### Consultas adicionais

| Método | Endpoint | Finalidade |
|---|---|---|
| GET | `/customers/{id}/orders` | Listar pedidos de um cliente. |
| GET | `/customers/{id}/orders/summary` | Consultar resumo de compras do cliente. |

---

### 6. Exemplo de requisição

Exemplo de corpo para criação de um pedido:

```json
{
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "items": [
    {
      "productId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
      "quantity": 2
    },
    {
      "productId": "6ba7b811-9dad-11d1-80b4-00c04fd430c8",
      "quantity": 1
    }
  ]
}
```

A aplicação deverá localizar os produtos, verificar a disponibilidade em estoque, criar os itens do pedido e calcular seu valor total.

Exemplo de resposta:

```json
{
  "id": "7ba7b810-9dad-11d1-80b4-00c04fd430c8",
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "CREATED",
  "total": 249.90,
  "createdAt": "2026-09-18T10:30:00"
}
```

O preço utilizado no `OrderItem` deverá representar o preço do produto no momento da criação do pedido, evitando que alterações futuras no preço do produto modifiquem o histórico de pedidos.

---

### 7. Requisitos técnicos

A aplicação deverá:

- Utilizar **Java**.
- Utilizar **Spring Boot**.
- Disponibilizar uma **API REST**.
- Utilizar **Spring Web** para construção dos endpoints.
- Utilizar **Spring Data JPA** para persistência.
- Utilizar **Hibernate/JPA** para mapeamento objeto-relacional.
- Utilizar **PostgreSQL** como banco de dados.
- Utilizar **BigDecimal** para valores monetários.
- Utilizar **UUID** como identificador das entidades.
- Organizar o projeto de forma clara, separando responsabilidades entre camadas.
- Utilizar DTOs para entrada e saída da API quando aplicável.
- Utilizar Bean Validation para validação dos dados recebidos.
- Utilizar tratamento adequado dos códigos HTTP.
- Possuir tratamento adequado dos erros da API.
- Utilizar transações nas operações que alteram dados relacionados.
- Manter as regras de negócio fora da camada de Controller.
- Disponibilizar instruções para execução do projeto.

A estrutura inicial esperada poderá seguir uma organização semelhante a:

```text
src/
└── main/
    └── java/
        └── br.com.orderprocessing/
            ├── controller/
            ├── dto/
            ├── domain/
            ├── repository/
            ├── service/
            ├── exception/
            └── config/
```

A organização poderá ser alterada conforme as necessidades do projeto, desde que as responsabilidades permaneçam bem definidas.

---

### 8. Persistência e relacionamentos

O banco de dados deverá representar os principais relacionamentos do sistema.

A estrutura conceitual esperada é:

```text
Customer
   |
   | 1:N
   v
Order
   |
   | 1:N
   v
OrderItem
   |
   | N:1
   v
Product
```

As tabelas deverão representar, no mínimo:

```text
customers
products
orders
order_items
```

O relacionamento entre `Order` e `Product` deverá ser realizado por meio de `OrderItem`, permitindo armazenar informações específicas da compra, como quantidade, preço unitário e subtotal.

A aplicação deverá evitar problemas comuns de persistência, como:

- referências inexistentes;
- registros órfãos;
- consultas desnecessárias;
- problemas de carregamento de relacionamentos;
- inconsistências entre estoque e pedidos.

---

### 9. Validações esperadas

A aplicação deverá realizar validações adequadas para os dados recebidos.

#### Cliente

- Nome deve ser informado.
- E-mail deve ser informado.
- E-mail deve possuir formato válido.
- Não deverá existir mais de um cliente com o mesmo e-mail.

#### Produto

- Nome deve ser informado.
- Preço deve ser informado.
- Preço não pode ser negativo.
- Estoque não pode ser negativo.

#### Pedido

- Cliente deve existir.
- O pedido deve possuir pelo menos um item.
- Produto deve existir.
- Quantidade deve ser maior que zero.
- Deve existir estoque suficiente para os produtos solicitados.
- Pedido inexistente deve resultar em resposta HTTP apropriada.
- Não deve ser possível processar um pedido cancelado.
- Não deve ser possível cancelar um pedido já concluído.
- Não deve ser possível realizar transições de status inválidas.

A API deverá retornar respostas HTTP coerentes com cada operação.

Exemplos:

```text
201 Created
200 OK
204 No Content
400 Bad Request
404 Not Found
409 Conflict
```

---

### 10. Concorrência e consistência do estoque

O sistema deverá considerar situações em que múltiplos pedidos sejam processados simultaneamente.

Considere o seguinte cenário:

```text
Produto A
Estoque = 1
```

Dois clientes realizam simultaneamente:

```text
Cliente A -> compra 1 unidade
Cliente B -> compra 1 unidade
```

O sistema deverá garantir que o estoque não seja consumido duas vezes.

O resultado esperado é que apenas uma operação consiga reservar a última unidade disponível.

A implementação deverá considerar mecanismos de controle de concorrência e consistência, como:

- transações;
- optimistic locking;
- pessimistic locking;
- `@Version`;
- isolamento de transações;
- tratamento de conflitos;
- retry quando aplicável.

A estratégia escolhida deverá ser documentada no projeto, explicando o motivo da escolha e seus respectivos trade-offs.

---

### 11. Consultas e estruturas de dados

O endpoint:

```text
GET /customers/{id}/orders/summary
```

deverá retornar informações agregadas dos pedidos do cliente.

Exemplo:

```json
{
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "totalOrders": 15,
  "totalSpent": 4870.50,
  "averageOrderValue": 324.70,
  "mostPurchasedProducts": [
    {
      "productId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
      "quantity": 37
    }
  ]
}
```

O sistema deverá identificar os **5 produtos mais comprados pelo cliente**.

A implementação deverá considerar eficiência da solução e utilização adequada de estruturas de dados.

O candidato deverá ser capaz de explicar a complexidade temporal e espacial da solução escolhida.

Estruturas que podem ser consideradas:

```text
HashMap
HashSet
PriorityQueue
List
```

A solução deverá considerar que a quantidade de pedidos e itens pode crescer significativamente.

---

### 12. Diferenciais

Os itens abaixo não são obrigatórios, mas podem ser utilizados para demonstrar conhecimentos adicionais:

- Testes unitários.
- Testes de integração.
- JUnit.
- Mockito.
- Testcontainers.
- Documentação da API com OpenAPI/Swagger.
- Docker/Docker Compose.
- Migrations utilizando Flyway ou Liquibase.
- Paginação na listagem de pedidos.
- Paginação na listagem de produtos.
- Filtros de pedidos por status.
- Consultas de pedidos por cliente.
- Bean Validation.
- Tratamento global de exceções.
- Logs estruturados.
- Organização adequada das configurações da aplicação.
- Profiles do Spring (`dev`, `test`, `prod`).
- Variáveis de ambiente para configurações sensíveis.
- Health checks.
- Actuator.
- Métricas da aplicação.
- Cache.
- Mensageria assíncrona.
- Idempotência no processamento de pedidos.
- Retry de operações que possam falhar.
- Docker Compose para ambiente local.

---

### 13. Arquitetura

A aplicação deverá possuir separação clara de responsabilidades.

Uma arquitetura inicial sugerida:

```text
                Client
                  |
                  v
            REST Controller
                  |
                  v
               Service
                  |
          ┌───────┴───────┐
          v               v
     Repository       Domain Rules
          |
          v
      PostgreSQL
```

Responsabilidades esperadas:

#### Controller

Responsável por:

- receber requisições HTTP;
- validar entrada;
- chamar os serviços;
- retornar respostas HTTP.

#### Service

Responsável por:

- executar casos de uso;
- coordenar operações;
- aplicar regras de negócio;
- controlar transações quando necessário.

#### Repository

Responsável por:

- acesso aos dados;
- consultas ao banco;
- persistência das entidades.

#### Domain

Responsável por:

- representar as entidades;
- manter regras e invariantes próprias do domínio.

#### DTO

Responsável por:

- representar contratos de entrada e saída da API;
- evitar exposição direta das entidades quando apropriado.

---

### 14. Tratamento de erros

A API deverá possuir uma estratégia centralizada para tratamento de exceções.

Exemplo de resposta:

```json
{
  "timestamp": "2026-09-18T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found",
  "path": "/orders/550e8400-e29b-41d4-a716-446655440000"
}
```

As exceções de negócio deverão ser tratadas de maneira apropriada.

Exemplos:

```text
CustomerNotFoundException
ProductNotFoundException
OrderNotFoundException
InvalidOrderStatusException
InsufficientStockException
```

O tratamento poderá ser centralizado utilizando:

```java
@RestControllerAdvice
```

---

### 15. Testes

A aplicação deverá possuir testes para os principais casos de uso.

#### Testes unitários

Deverão ser considerados testes para:

```text
OrderService
ProductService
CustomerService
InventoryService
OrderSummaryService
```

Devem ser testados, entre outros:

- criação de pedido;
- cálculo do total;
- produto inexistente;
- cliente inexistente;
- estoque insuficiente;
- transição válida de status;
- transição inválida de status;
- cancelamento;
- processamento;
- cálculo do resumo do cliente.

#### Testes de integração

Deverão ser considerados testes envolvendo:

```text
Controller
Service
Repository
Database
```

Um cenário importante deverá testar concorrência no estoque.

Exemplo:

```text
Produto:
stock = 1

100 requisições simultâneas:
cada uma tentando comprar 1 unidade.
```

O sistema deverá garantir que a quantidade de pedidos confirmados seja compatível com o estoque disponível.

---

### 16. Debugging e produção

O projeto deverá ser desenvolvido considerando a possibilidade de diagnóstico de problemas em ambiente de desenvolvimento e produção.

Deverão ser utilizados, quando aplicável:

- logs;
- stack traces;
- mensagens de erro claras;
- identificação de requisições;
- informações de transação;
- métricas;
- health checks.

Problemas de concorrência, inconsistência de estoque e falhas de processamento deverão ser reproduzíveis e diagnosticáveis.

Para problemas encontrados durante o desenvolvimento, recomenda-se documentar:

```text
Problem:
Root Cause:
How to Reproduce:
Fix:
How to Prevent:
```

---

### 17. Entrega

Entregar o código-fonte do projeto em um repositório Git contendo:

```text
README.md
src/
pom.xml
Dockerfile
docker-compose.yml
```

quando aplicável.

O README deverá informar:

- objetivo do projeto;
- tecnologias utilizadas;
- requisitos para execução;
- como configurar o banco de dados;
- como executar a aplicação;
- como executar os testes;
- endpoints disponíveis;
- exemplos de requisições;
- decisões técnicas relevantes.

As configurações sensíveis, como credenciais do banco de dados, não deverão ser armazenadas diretamente no código-fonte.

---

### 18. Git e versionamento

O projeto deverá utilizar Git para controle de versão.

Recomenda-se utilizar branches para desenvolvimento de funcionalidades.

Exemplo:

```text
main
 |
 +--- feature/customer-api
 |
 +--- feature/product-api
 |
 +--- feature/order-api
 |
 +--- feature/inventory-control
 |
 +--- feature/order-summary
```

Os commits deverão representar alterações claras e isoladas.

Exemplos:

```text
feat: add customer management
feat: implement product CRUD
feat: create order API
feat: add inventory validation
fix: prevent negative inventory
test: add order service tests
refactor: extract order validation
docs: update project README
```

Também deverá ser possível explicar as decisões realizadas durante o desenvolvimento em um Pull Request ou documentação equivalente.

---

### 19. Critérios de avaliação

| Critério | O que será observado |
|---|---|
| Funcionamento | CRUD e processamento de pedidos funcionando corretamente. |
| Java | Domínio, orientação a objetos, coleções, tratamento de exceções e boas práticas. |
| Spring Boot | Uso adequado dos recursos do framework. |
| API REST | Endpoints, métodos HTTP, status codes e contratos. |
| Persistência | Modelagem, relacionamentos e acesso aos dados. |
| Banco de dados | PostgreSQL, integridade e consultas. |
| Arquitetura | Separação de responsabilidades e organização do projeto. |
| Regras de negócio | Implementação correta do ciclo de vida dos pedidos. |
| Validações | Tratamento de entradas inválidas e cenários de erro. |
| Concorrência | Controle adequado do estoque em operações simultâneas. |
| Testes | Cobertura e qualidade dos testes apresentados. |
| Código | Clareza, organização, legibilidade e boas práticas. |
| Git | Histórico organizado e commits coerentes. |
| Debugging | Capacidade de identificar e solucionar problemas. |
| Documentação | Facilidade para executar e compreender o projeto. |

---

### 20. Observação

O candidato pode tomar decisões de implementação diferentes das sugestões deste documento, desde que os requisitos principais sejam atendidos.

As decisões arquiteturais e técnicas deverão, sempre que possível, ser justificadas considerando:

```text
Problema
Solução escolhida
Alternativas consideradas
Trade-offs
```

O objetivo não é apenas fazer a aplicação funcionar, mas demonstrar capacidade de desenvolver uma aplicação backend utilizando **Java + Spring Boot**, compreender o domínio, trabalhar com persistência, construir APIs REST, aplicar regras de negócio, lidar com erros e pensar em problemas reais de produção.