# Camada de Serviços

## Visão Geral

A camada de serviços é responsável por coordenar operações da aplicação e executar casos de uso que envolvem múltiplas entidades ou múltiplos repositórios.

Ela atua como intermediária entre a camada de aplicação, o domínio e os repositórios.

Enquanto as entidades concentram as regras de negócio fundamentais, os serviços são responsáveis por orquestrar essas regras dentro dos fluxos da aplicação.

Os serviços também concentram regras de aplicação que dependem de consultas aos repositórios ou da coordenação entre múltiplas entidades.

---

## Responsabilidades

A camada de serviços é responsável por:

* coordenar entidades;
* executar casos de uso;
* acessar repositórios;
* garantir consistência das operações;
* aplicar validações de aplicação;
* transformar entidades em DTOs quando necessário.
* coordenar múltiplos repositórios quando necessário;
* coordenar operações transacionais da aplicação;

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

```
import model.valueobject.PersonName;

createClient(...);

delete(...);

changeName(...);

changeEmail(...);

getClientData(...);

getCpfByEmail(...);
```

---

### Regras Aplicadas

Durante as operações o serviço garante:

* CPF único;
* email único;
* cliente existente;
* impedimento de alteração para o mesmo nome atual;
* impedimento de alteração para o mesmo email atual.
---

## AccountService

Responsável pelo ciclo de vida das contas bancárias.

### Responsabilidades

* abertura de contas;
* encerramento de contas;
* consulta de contas de um cliente;
* busca de contas;
* validações relacionadas à remoção;

---

### Repositórios Utilizados

```text
AccountRepository
```

---

### Principais Casos de Uso

```
createAccount(...)
removeAccount(...)
removeClientAccounts(...)
getAccountByAccountIdentity(...)
getClientAccountsIdentity(...)
```

---

### Regras Aplicadas

O serviço garante:

* garantia de unicidade do AccountIdentity.
* validação de conta existente;
* encerramento de contas apenas quando permitido;
* validação de contas vinculadas ao cliente.

---

### Criação de Contas

A criação de contas utiliza a fábrica:

```
AccountIdentityFactory
```

para geração automática dos identificadores bancários.

A geração é repetida até que seja encontrado um AccountIdentity ainda não utilizado no sistema.

---

## TransactionService

Responsável pelas operações financeiras do sistema.

É o serviço responsável pela coordenação das operações financeiras do sistema.

---

### Responsabilidades

* depósitos;
* saques;
* transferências;
* consulta de saldo;
* geração de extrato;
* coordenação da aplicação automática de juros;
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

```
deposit(...)
withdraw(...)
transfer(...)
getAccountBalance(...)
getTransactionHistoryByAccountIdentity(...)
```

---

## Coordenação de Transferências
Uma transferência gera duas transações independentes:

TRANSFER_SENT
TRANSFER_RECEIVED

Ambas compartilham o mesmo operationId, permitindo rastrear a operação completa.

---

## Coordenação da Aplicação Automática de Juros

Antes de qualquer operação financeira, o serviço garante que os juros pendentes sejam processados.

Todas as operações públicas do TransactionService utilizam internamente um fluxo único de carregamento de conta que garante a atualização prévia dos rendimentos pendentes.

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

SavingsAccount é responsável por calcular os rendimentos pendentes.

TransactionService é responsável por transformar esses rendimentos em registros históricos através de Transaction.interest(...).

Exemplo:

Após a aplicação dos rendimentos:
```
SavingsAccount.applyPendingInterests(...)
```

os juros retornados são convertidos em:

```
Transaction.interest(...)
```

e armazenados no histórico da conta.

Dessa forma:

* o domínio continua desacoplado da persistência;
* os rendimentos aparecem no extrato;
* o saldo permanece consistente.

---

## Centralização do Histórico
Toda criação de transações ocorre exclusivamente dentro do TransactionService.

Nenhuma entidade realiza persistência de histórico diretamente.

Isso garante consistência e centraliza o registro das movimentações financeiras.

---

## Estratégia de Extrato

O extrato não é produzido diretamente pelas entidades.

O processo ocorre em quatro etapas:

```text
TransactionRepository
        │
        ▼
Transaction
        │
        ▼
TransactionService
        │
        ▼
StatementData
```

O DTO `StatementData` representa a visão pública do histórico para as camadas superiores.

---

## Uso de DTOs

A camada de serviços é responsável por criar DTOs utilizados pela aplicação.

Os DTOs representam visões específicas dos dados necessárias para as camadas superiores, evitando exposição direta das entidades do domínio.

Atualmente:

```text
ClientData
StatementData
```

são produzidos pelos serviços.

---

## Tratamento de Exceções

Os serviços utilizam exceções específicas da aplicação e do domínio para representar situações inválidas ou impossíveis de executar.

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
