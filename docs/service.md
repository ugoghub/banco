# Service Layer

## Visão Geral

A camada de serviços é responsável por coordenar operações da aplicação e executar casos de uso que envolvem múltiplas entidades ou múltiplos repositórios.

Ela atua como intermediária entre a camada de aplicação e o domínio.

Enquanto as entidades concentram as regras de negócio fundamentais, os serviços são responsáveis por orquestrar essas regras dentro dos fluxos da aplicação.

---

## Responsabilidades

A camada de serviços é responsável por:

* coordenar entidades;
* executar casos de uso;
* acessar repositórios;
* garantir consistência das operações;
* aplicar validações de aplicação;
* transformar entidades em DTOs quando necessário.

Não é responsabilidade da camada de serviços:

* armazenar dados;
* implementar interface de usuário;
* conter lógica de persistência;
* substituir regras pertencentes ao domínio.

---

## Estrutura

A camada é composta por três serviços:

```text
ClientService
AccountService
TransactionService
```

Cada serviço possui responsabilidade específica sobre um conjunto de operações do sistema.

---

## ClientService

Responsável pelo gerenciamento de clientes.

### Responsabilidades

* criação de clientes;
* remoção de clientes;
* alteração de nome;
* alteração de email;
* consulta de dados;
* validação de unicidade de CPF;
* validação de unicidade de email.

---

### Repositórios Utilizados

```text
ClientRepository
```

---

### Principais Casos de Uso

```java
createClient(...)
delete(...)
changeName(...)
changeEmail(...)
getClientData(...)
getCpfByEmail(...)
```

---

### Regras Aplicadas

Durante as operações o serviço garante:

* CPF único;
* email único;
* cliente existente;
* alteração válida de dados.

---

## AccountService

Responsável pela administração das contas bancárias.

### Responsabilidades

* abertura de contas;
* encerramento de contas;
* consulta de contas de um cliente;
* busca de contas;
* validações relacionadas à remoção.

---

### Repositórios Utilizados

```text
AccountRepository
```

---

### Principais Casos de Uso

```java
createAccount(...)
removeAccount(...)
removeClientAccounts(...)
getAccountByAccountIdentity(...)
getClientAccountsIdentity(...)
```

---

### Regras Aplicadas

O serviço garante:

* geração de AccountIdentity único;
* validação de conta existente;
* remoção apenas quando permitido;
* validação de contas vinculadas ao cliente.

---

### Criação de Contas

A criação de contas utiliza a fábrica:

```java
AccountIdentityFactory
```

para geração automática dos identificadores bancários.

A unicidade é validada através do repositório antes da criação definitiva.

---

## TransactionService

Responsável pelas operações financeiras do sistema.

É o serviço mais complexo da aplicação.

---

### Responsabilidades

* depósitos;
* saques;
* transferências;
* consulta de saldo;
* geração de extrato;
* aplicação automática de juros;
* registro de transações.

---

### Dependências

```text
AccountService
TransactionRepository
Clock
```

---

### Principais Casos de Uso

```java
deposit(...)
withdraw(...)
transfer(...)
getAccountBalance(...)
getTransactionHistoryByAccountIdentity(...)
```

---

## Aplicação Automática de Juros

Antes de qualquer operação financeira, o serviço garante que os juros pendentes sejam processados.

Fluxo simplificado:

```text
Localizar conta
      │
      ▼
Aplicar juros pendentes
      │
      ▼
Executar operação solicitada
```

Essa estratégia garante que a conta sempre esteja atualizada no momento de acesso.

---

### Operações que Disparam Atualização

Os juros são aplicados automaticamente antes de:

* depósito;
* saque;
* transferência;
* consulta de saldo;
* consulta de extrato.

---

### Geração de Transações de Juros

Após a aplicação dos rendimentos:

```java
SavingsAccount.applyPendingInterests(...)
```

os juros retornados são convertidos em:

```java
Transaction.interest(...)
```

e armazenados no histórico da conta.

Dessa forma:

* o domínio continua desacoplado da persistência;
* os rendimentos aparecem no extrato;
* o saldo permanece consistente.

---

## Estratégia de Extrato

O extrato não é produzido diretamente pelas entidades.

O processo ocorre em três etapas:

```text
TransactionRepository
        │
        ▼
Transaction
        │
        ▼
StatementData
```

O DTO `StatementData` representa a visão pública do histórico para as camadas superiores.

---

## Uso de DTOs

A camada de serviços é responsável por criar DTOs utilizados pela aplicação.

Atualmente:

```text
ClientData
StatementData
```

são produzidos pelos serviços.

Essa abordagem evita exposição direta das entidades para outras camadas.

---

## Tratamento de Exceções

Os serviços utilizam exclusivamente exceções do domínio.

Exemplos:

```text
ClientNotFoundException
AccountNotFoundException
CpfAlreadyExistsException
EmailAlreadyExistsException
InsufficientBalanceException
InvalidTransferException
```

A camada de serviços nunca retorna estados inválidos.

Situações excepcionais são representadas por exceções específicas.

---

## Benefícios da Camada de Serviços

A existência dessa camada proporciona:

* centralização dos casos de uso;
* coordenação entre entidades;
* separação entre domínio e persistência;
* redução de duplicação de código;
* maior organização arquitetural;
* facilidade de manutenção.

Além disso, permite que o domínio permaneça focado exclusivamente em regras de negócio, enquanto os serviços cuidam da execução dos fluxos da aplicação.
