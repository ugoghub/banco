# Camada de Repositórios

## Visão Geral

A camada de repositórios é responsável pelo armazenamento e recuperação dos dados utilizados pela aplicação.

Atualmente a persistência é realizada inteiramente em memória através de coleções Java.

Os repositórios atuam como uma abstração de acesso aos dados, permitindo que as demais camadas não tenham conhecimento sobre a estrutura utilizada para armazenamento.

Os repositórios acumulam simultaneamente as responsabilidades de abstração e armazenamento em memória.

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
* atualização do índice de email;
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

Os índices são mantidos sincronizados com o mapa principal durante operações de inclusão, atualização e remoção.

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
* permitir localização rápida de contas através de sua identidade bancária.

---

## TransactionRepository

Responsável pelo armazenamento do histórico de transações.

### Objetivos

Permitir:

* registro de transações;
* consulta de extrato por conta.

---

### Estrutura Utilizada

O histórico é organizado por AccountId(UUID) interno.

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

## Relação com a Camada de Serviços

Os repositórios são utilizados exclusivamente pelos serviços da aplicação.

Eles não são acessados diretamente pela interface nem pela camada de aplicação.

Fluxo:

```text
Services
    │
    ▼
Repositories
```
---

## Imutabilidade do histórico

As transações retornadas pelo repositório são expostas através de uma cópia imutável da coleção interna.

Isso impede que consumidores modifiquem diretamente o histórico armazenado pelo repositório.

---

## Preservação do histórico

O TransactionRepository não possui operações de remoção.

Uma vez registrada, uma transação permanece armazenada no histórico.

Consequentemente, a remoção de uma conta não implica a remoção de suas transações.

Essa decisão preserva rastreabilidade e auditabilidade das operações financeiras realizadas pelo sistema.

---

## Persistência em Memória

Como os repositórios armazenam referências para os próprios objetos do domínio, alterações realizadas diretamente nas entidades são refletidas automaticamente nas estruturas armazenadas.

Por esse motivo, a implementação atual não necessita de operações explícitas de atualização (update), já que os objetos manipulados pelos serviços são os mesmos objetos mantidos pelos repositórios.

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
* validação de CPF duplicado;
* validação de email duplicado;
* remoção de contas com saldo.

Essas validações são executadas pelos serviços da aplicação utilizando os dados fornecidos pelos repositórios.

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

## Repositórios em Memória

Os repositórios atuais não representam contratos de persistência independentes.

Cada repositório possui uma implementação concreta em memória utilizada diretamente pela aplicação.

Essa escolha foi adotada para simplificar o projeto acadêmico, mantendo a possibilidade de evolução futura para mecanismos persistentes.

---

## Possíveis Evoluções

A arquitetura atual permite futura migração para:

* PostgreSQL;
* MySQL;
* MongoDB;
* arquivos locais;
* APIs externas.

Mantendo os contratos públicos dos repositórios e preservando as regras de negócio existentes.

A adoção futura de uma solução persistente provavelmente exigirá a introdução de mecanismos explícitos de atualização e sincronização de estado que atualmente não são necessários devido ao armazenamento em memória por referência.