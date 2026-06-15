# Repository Layer

## Visão Geral

A camada de repositórios é responsável pelo armazenamento e recuperação dos dados utilizados pela aplicação.

Atualmente a persistência é realizada inteiramente em memória através de coleções Java.

Os repositórios atuam como uma abstração de acesso aos dados, permitindo que as demais camadas não tenham conhecimento sobre a estrutura utilizada para armazenamento.

---

## Responsabilidades

A camada de repositórios é responsável por:

* armazenar objetos;
* recuperar objetos;
* manter índices de busca;
* remover registros;
* fornecer consultas.

Não é responsabilidade dos repositórios:

* aplicar regras de negócio;
* validar operações financeiras;
* executar casos de uso;
* coordenar entidades.

---

## Estrutura

A aplicação possui três repositórios:

```text
ClientRepository
AccountRepository
TransactionRepository
```

Cada um é responsável por um conjunto específico de dados.

---

## ClientRepository

Responsável pelo armazenamento dos clientes cadastrados.

### Objetivos

Permitir:

* cadastro de clientes;
* consulta por CPF;
* consulta por email;
* consulta por ID;
* atualização de email;
* remoção de clientes.

---

### Estruturas Utilizadas

```
Map<UUID, Client> clientsById

Map<Cpf, UUID> clientIdByCpf

Map<Email, UUID> clientIdByEmail
```

---

### Estratégia de Indexação

O cliente é armazenado apenas uma vez:

```text
UUID → Client
```

Os demais mapas funcionam como índices:

```text
Cpf   → UUID
Email → UUID
```

Essa abordagem evita duplicação de dados e permite buscas rápidas.

---

### Complexidade Média

```text
Busca por CPF      O(1)
Busca por Email    O(1)
Busca por ID       O(1)
```

---

## AccountRepository

Responsável pelo armazenamento das contas bancárias.

### Objetivos

Permitir:

* armazenamento de contas;
* busca por AccountIdentity;
* busca por ID;
* consulta de contas por cliente;
* remoção de contas;
* remoção de todas as contas de um cliente.

---

### Estruturas Utilizadas

```
Map<UUID, Account> accountByAccountId

Map<AccountIdentity, UUID> accountIndex

Map<UUID, List<UUID>> accountIdByClientId
```

---

### Estratégia de Indexação

A conta é armazenada apenas uma vez:

```text
UUID → Account
```

Os demais mapas funcionam como índices auxiliares:

```text
AccountIdentity → UUID

ClientId → Lista de contas
```

---

### Benefícios

Essa estrutura permite:

* localizar contas rapidamente;
* recuperar todas as contas de um cliente;
* validar unicidade do AccountIdentity.

---

## TransactionRepository

Responsável pelo armazenamento do histórico de transações.

### Objetivos

Permitir:

* registro de transações;
* consulta de extrato por conta.

---

### Estrutura Utilizada

```
Map<UUID, List<Transaction>>
```

Onde:

```text
AccountId → Lista de Transações
```

---

### Estratégia

Cada conta possui sua própria coleção de transações.

Exemplo:

```text
Conta A
 ├─ Depósito
 ├─ Saque
 └─ Transferência

Conta B
 ├─ Depósito
 └─ Rendimento
```

---

### Benefícios

Essa organização permite:

* consulta simples de extrato;
* separação natural por conta;
* armazenamento eficiente para o contexto atual do projeto.

---

## Persistência em Memória

Todos os dados da aplicação permanecem exclusivamente em memória durante a execução.

Consequentemente:

* não existe banco de dados;
* não existe serialização;
* não existe armazenamento permanente.

Ao encerrar a aplicação:

```text
Clientes
Contas
Transações
```

são descartados.

---

## Isolamento da Persistência

Apesar de utilizar armazenamento em memória, a lógica de persistência permanece isolada dentro dos repositórios.

As demais camadas interagem apenas através dos métodos públicos disponibilizados pelos repositórios.

Exemplo:

```text
Service
   │
   ▼
Repository
```

Sem acesso direto às estruturas internas.

---

## Ausência de Regras de Negócio

Os repositórios não executam validações de domínio.

Exemplos de regras que NÃO pertencem aos repositórios:

* saldo insuficiente;
* aplicação de juros;
* CPF duplicado;
* email duplicado;
* remoção de contas com saldo.

Essas responsabilidades pertencem às camadas superiores.

---

## Benefícios da Implementação Atual

A estratégia adotada oferece:

* simplicidade;
* baixo acoplamento;
* facilidade de testes;
* boa performance para o escopo atual;
* independência de banco de dados.

Além disso, a existência da camada de repositórios permite futura substituição da implementação atual por uma solução persistente sem alterações significativas na lógica da aplicação.

---

## Possíveis Evoluções

A arquitetura atual permite futura migração para:

* PostgreSQL;
* MySQL;
* MongoDB;
* arquivos locais;
* APIs externas.

Mantendo os contratos públicos dos repositórios e preservando as regras de negócio existentes.
