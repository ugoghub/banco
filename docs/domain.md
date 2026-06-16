# Domínio

## Visão Geral do Domínio

O sistema modela um banco digital simplificado, permitindo o gerenciamento de clientes, contas bancárias e movimentações financeiras.

O objetivo principal do domínio é garantir que operações financeiras sejam executadas de forma consistente, preservando as regras de negócio relacionadas a saldo, transferências, rendimentos e identificação de clientes.

A modelagem foi construída seguindo princípios de orientação a objetos e conceitos de Domain-Driven Design (DDD), concentrando as regras de negócio dentro das entidades e Value Objects do domínio.

---

## Conceitos Principais

O domínio é composto por três conceitos centrais:

### Cliente

Representa uma pessoa cadastrada no banco.

Cada cliente possui:

* identificação única;
* nome;
* CPF;
* email.

Um cliente pode possuir uma ou mais contas bancárias.

---

### Conta Bancária

Representa o recurso financeiro utilizado pelo cliente para armazenar saldo e realizar operações.

O sistema possui dois tipos de conta:

* Conta Corrente (`CheckingAccount`);
* Conta Poupança (`SavingsAccount`).

Toda conta:

* pertence a exatamente um cliente;
* possui uma identidade bancária única;
* mantém seu próprio saldo;
* registra suas movimentações através de transações.

---

### Transação

Representa um evento financeiro ocorrido em uma conta.

As transações são responsáveis por registrar o histórico das movimentações realizadas pelo sistema.

Os tipos de transação suportados são:

* depósito;
* saque;
* transferência enviada;
* transferência recebida;
* rendimento de poupança.

As transações não alteram saldo diretamente. Elas atuam como registro histórico das operações executadas pelas contas.

Todas as transações possuem:

- identificador único;
- data e hora de registro;
- tipo;
- valor monetário.

Transferências compartilham um operationId comum.

---

## Relacionamento Entre os Conceitos

O relacionamento entre os principais elementos do domínio pode ser representado da seguinte forma:

```text
Cliente
   │
   ├── Conta Corrente
   │       │
   │       └── Transações
   │
   └── Conta Poupança
           │
           └── Transações
```

Um cliente pode possuir múltiplas contas correntes e/ou poupança.

Cada conta mantém seu próprio saldo e seu próprio histórico de transações.

As transações registram eventos financeiros ocorridos em uma conta, permitindo rastreabilidade e consulta de extrato.

---

## Responsabilidades do Domínio

O domínio é responsável por:

* validar dados de negócio;
* controlar saldo das contas;
* impedir operações inválidas;
* calcular rendimentos da poupança;
* registrar movimentações financeiras;
* garantir consistência entre clientes, contas e transações.

Aspectos relacionados a interface, persistência e orquestração de casos de uso são tratados em camadas externas e não fazem parte das responsabilidades do domínio.


# Exceções

O projeto utiliza uma hierarquia de exceções orientada ao domínio para separar claramente erros de validação, violações de regras de negócio e recursos inexistentes.

Todas as exceções da aplicação herdam de `DomainException`, permitindo tratamento centralizado sem perder o significado semântico de cada erro.

## Hierarquia

```text
DomainException
│
├── ValidationException
│   ├── InvalidAccountNumberException
│   ├── InvalidAmountException
│   ├── InvalidBranchException
│   ├── InvalidCpfException
│   ├── InvalidEmailException
│   ├── InvalidPersonNameException
│   ├── InvalidAccountIdentityException
│   ├── InvalidClientIdException
│   ├── InvalidClockException
│   └── InvalidTransactionException
│
├── BusinessRuleException
│   ├── AccountDeletionNotAllowedException
│   ├── CpfAlreadyExistsException
│   ├── EmailAlreadyExistsException
│   ├── InsufficientBalanceException
│   ├── InvalidClientChangeException
│   ├── InvalidTransferException
│   └── NoAccountsFoundException
│
└── NotFoundException
    ├── AccountNotFoundException
    └── ClientNotFoundException
```

---

## DomainException

Classe base para todas as exceções do domínio.

Seu objetivo é fornecer um tipo comum para captura e tratamento de erros relacionados às regras da aplicação.

---

## ValidationException

Representa erros de entrada ou construção de objetos inválidos.

Essas exceções normalmente são lançadas durante a criação de Value Objects ou na validação de argumentos recebidos pelas entidades.

### Exemplos

* CPF com formato inválido.
* Email inválido.
* Nome com caracteres não permitidos.
* Valor monetário nulo ou inválido.
* Agência ou número de conta inválidos.

### Exceções

| Exceção                         | Responsabilidade                           |
| ------------------------------- | ------------------------------------------ |
| InvalidCpfException             | CPF inválido                               |
| InvalidEmailException           | Email inválido                             |
| InvalidPersonNameException      | Nome inválido                              |
| InvalidAmountException          | Valor monetário inválido                   |
| InvalidBranchException          | Agência inválida                           |
| InvalidAccountNumberException   | Número de conta inválido                   |
| InvalidAccountIdentityException | Identidade de conta inválida               |
| InvalidClientIdException        | Identificador de cliente inválido          |
| InvalidClockException           | Relógio inválido                           |
| InvalidTransactionException     | Estado inválido para criação de transações |

---

## BusinessRuleException

Representa violações das regras de negócio da aplicação.

São situações em que os dados são válidos, mas a operação solicitada não pode ser executada.

### Exemplos

* Tentar excluir uma conta com saldo diferente de zero.
* Tentar transferir para a mesma conta.
* Tentar cadastrar um CPF já existente.
* Tentar cadastrar um email já utilizado.

### Exceções

| Exceção                            | Responsabilidade                       |
| ---------------------------------- | -------------------------------------- |
| CpfAlreadyExistsException          | CPF já cadastrado                      |
| EmailAlreadyExistsException        | Email já cadastrado                    |
| InsufficientBalanceException       | Saldo insuficiente                     |
| InvalidTransferException           | Transferência inválida                 |
| InvalidClientChangeException       | Alteração inválida de dados do cliente |
| AccountDeletionNotAllowedException | Conta não pode ser removida            |
| NoAccountsFoundException           | Cliente não possui contas              |

---

## NotFoundException

Representa a tentativa de acesso a recursos inexistentes.

É normalmente lançada pela camada de serviço quando uma entidade esperada não pode ser localizada nos repositórios.

### Exceções

| Exceção                  | Responsabilidade       |
| ------------------------ | ---------------------- |
| ClientNotFoundException  | Cliente não encontrado |
| AccountNotFoundException | Conta não encontrada   |

---

## Motivação da Hierarquia

A separação entre validação, regras de negócio e recursos inexistentes permite:

* maior clareza semântica;
* mensagens de erro mais precisas;
* tratamento específico quando necessário;
* desacoplamento entre interface, aplicação e domínio;
* evolução futura sem impactar a camada de apresentação.

Essa estrutura segue os princípios de Domain-Driven Design, onde exceções representam falhas relevantes do domínio e não apenas erros técnicos da plataforma.

---

# Value Objects

Os Value Objects representam conceitos do domínio que não possuem identidade própria e são definidos exclusivamente pelos seus valores.

Todos os Value Objects do sistema são imutáveis, realizam validação no momento da criação e garantem que estados inválidos não sejam propagados para o restante da aplicação.

## AccountIdentity

Representa a identificação pública de uma conta bancária.

É composto por:

* agência (`branch`);
* número da conta (`accountNumber`).

### Responsabilidades

* validar o formato da agência;
* validar o formato do número da conta;
* validar o dígito verificador da conta;
* representar a identidade pública de uma conta dentro do sistema.

### Regras

Agência:

* deve possuir exatamente 2 dígitos numéricos;
* não pode ser nula.

Número da conta:

* deve seguir o padrão `123456-1`;
* deve conter exatamente 6 dígitos e 1 dígito verificador;
* não pode ser nulo;
* o dígito verificador é calculado pela soma dos dígitos da conta módulo 10.

### Exemplo de Conta Válida

```text
Agência: 01
Conta:   123456-1
```

### Exceções

* `InvalidBranchException`
* `InvalidAccountNumberException`

---

## AccountIdentityGenerator

Responsável pela geração automática de novas identidades de conta.

### Responsabilidades

* gerar agências válidas;
* gerar números de conta válidos;
* calcular automaticamente o dígito verificador.

### Observações

A fábrica apenas gera identidades válidas.

A garantia de unicidade é responsabilidade do `AccountService`, que consulta o repositório antes de persistir uma nova conta.

---

## Cpf

Representa o CPF de um cliente.

### Responsabilidades

* validar estrutura do CPF;
* validar dígitos verificadores;
* normalizar entradas formatadas.

### Formatos aceitos

```text
52998224725
529.982.247-25
```

### Regras

* deve possuir 11 dígitos;
* não pode ser nulo;
* não pode conter todos os dígitos iguais;
* deve possuir dígitos verificadores válidos.

### Normalização

O CPF é armazenado sem máscara.

Exemplo:

```text
529.982.247-25
↓
52998224725
```

### Exceções

* `InvalidCpfException`

---

## Email

Representa o endereço de email do cliente.

### Responsabilidades

* validar estrutura do email;
* normalizar valores para comparação.

### Regras

* não pode ser nulo;
* deve possuir formato válido;
* deve conter usuário e domínio.

### Normalização

Antes da validação:

* remove espaços nas extremidades;
* converte para minúsculo.

Exemplo:

```text
TESTE@GMAIL.COM
↓
teste@gmail.com
```

### Exceções

* `InvalidEmailException`

---

## Money

Representa valores monetários do sistema.

### Responsabilidades

* encapsular operações financeiras;
* evitar uso direto de `BigDecimal` nas regras de negócio;
* garantir precisão monetária.

### Características

* imutável;
* utiliza escala fixa de 2 casas decimais;
* utiliza arredondamento `HALF_EVEN`.

### Operações disponíveis

* soma;
* subtração;
* multiplicação por taxa;
* comparação;
* negação de valor.

### Exemplos

```
Money balance = Money.of("100.00");

balance = balance.add(Money.of("50.00"));
```

### Métodos utilitários

* `isZero()`
* `isNegativeOrZero()`
* `isGreaterThan(...)`

### Constantes

```
Money.ZERO
```

### Exceções

* `InvalidAmountException`

---

## PersonName

Representa o nome completo de um cliente.

### Responsabilidades

* validar nomes;
* normalizar espaços;
* impedir nomes inválidos.

### Regras

* não pode ser nulo;
* deve possuir ao menos 2 caracteres;
* aceita letras acentuadas;
* aceita apóstrofos;
* aceita espaços internos;
* aceita hífens.

### Normalização

Espaços duplicados são removidos.

Exemplo:

```text
"  João    da   Silva "
↓
"João da Silva"
```

### Exceções

* `InvalidPersonNameException`

---

## Benefícios da abordagem

O uso de Value Objects permite:

* validação centralizada;
* eliminação de estados inválidos;
* redução de código defensivo em serviços e entidades;
* comparações baseadas em valor, implementadas pelos próprios Value Objects.
* maior clareza semântica das regras de negócio.

Com isso, entidades e serviços podem assumir que os dados recebidos já estão válidos, simplificando significativamente a lógica do domínio.

---

# Padrões e Estratégias de Domínio

## Template Method na Hierarquia de Contas

A classe Account define o comportamento comum compartilhado por todos os tipos de conta, centralizando regras de depósito, saque, validação de valores e gerenciamento de saldo.

Em vez de permitir que cada conta implemente completamente sua lógica de movimentação, a classe abstrata controla o fluxo principal e delega apenas as regras específicas para as subclasses.

---

## Estrutura

A classe Account implementa:

* validação de valores;
* depósitos;
* saques;
* atualização de saldo;
* regra de exclusão da conta.

As subclasses precisam fornecer apenas a implementação do método:

```text
protected abstract Money minimumAllowedBalance();
```

Esse método define qual o saldo mínimo permitido para cada tipo de conta.

---

## Implementações
### CheckingAccount

Permite utilização de limite especial.

```text
@Override
protected Money minimumAllowedBalance() {
return OVERDRAFT_LIMIT.negate();
}
```

Saldo mínimo permitido:

-1000,00

### SavingsAccount

Não permite saldo negativo.

```text
@Override
protected Money minimumAllowedBalance() {
return Money.ZERO;
}
```

Saldo mínimo permitido:

0,00

---

## Benefícios
* elimina duplicação de lógica de saque e depósito;
* garante comportamento consistente entre contas;
* mantém regras específicas isoladas nas subclasses;
* facilita criação de novos tipos de conta.

---

## Aplicação Preguiçosa (Lazy) de Juros

A conta poupança utiliza uma estratégia de aplicação preguiçosa de juros (Lazy Interest Application).

Nessa abordagem, os rendimentos não são processados automaticamente por agendadores ou tarefas periódicas.

Em vez disso, os juros são calculados e aplicados quando a conta participa de uma operação que exige atualização de seu estado.

---

## Motivação

Uma implementação tradicional exigiria:

* execução mensal de tarefas agendadas;
* processamento de todas as contas poupança do sistema;
* infraestrutura adicional para agendamento.

Como o projeto é focado em domínio e regras de negócio, foi adotada uma abordagem mais simples e desacoplada.

---

## Funcionamento

A classe SavingsAccount mantém o campo:
```text
private LocalDateTime lastInterestAppliedAt;
```

Esse atributo registra a última data considerada para aplicação de rendimento.

Sempre que a conta é acessada pelo sistema, ocorre a verificação de meses pendentes.
```text
Exemplo:

Criação da conta:
01/01/2025

Último rendimento aplicado:
01/01/2025

Data atual:
01/04/2025

Ao acessar a conta:

Janeiro → Fevereiro
Fevereiro → Março
Março → Abril
```
Os três meses de rendimento são aplicados retroativamente.

---

## Juros Acumulados Retroativos

A implementação garante que nenhum rendimento seja perdido mesmo após longos períodos sem movimentação.
```text
Exemplo:

Saldo:
R$ 10.000,00

Conta sem movimentação:
24 meses

Ao acessar a conta novamente:

Todos os 24 rendimentos pendentes serão processados.

Isso mantém o saldo consistente com o tempo transcorrido.
```

---

## Atualização Automática Antes das Operações

Os juros são aplicados automaticamente antes das operações relevantes.

O TransactionService executa a atualização ao:

* consultar saldo;
* realizar depósito;
* realizar saque;
* realizar transferência;
* consultar extrato.

Fluxo simplificado:
```text

Usuário solicita saldo
↓
TransactionService
↓
applyPendingInterest()
↓
SavingsAccount.applyPendingInterests()
↓
Saldo atualizado
↓
Retorna saldo correto
```
---

## Registro dos Rendimentos no Extrato

Além de atualizar o saldo, os rendimentos geram transações reais no histórico.

Para cada rendimento aplicado é criada uma transação:
```text
Transaction.interest(...)
```
Isso garante que:

* o saldo seja atualizado;
* os rendimentos apareçam no extrato;
* exista rastreabilidade completa das aplicações de juros.

---

## Benefícios da Abordagem
### Simplicidade

Não exige:

* schedulers;
* cron jobs;
* processamento periódico.

### Consistência

O saldo sempre reflete todos os rendimentos acumulados.

### Escalabilidade

Somente contas efetivamente acessadas precisam processar juros.

### Desacoplamento

A lógica permanece inteiramente dentro do domínio, sem dependência de infraestrutura externa.

---

# Entidades

* As entidades representam os principais conceitos do domínio bancário.
* As entidades possuem identidade própria e são distinguidas por um identificador único, independentemente de seus atributos.

---

## Client

Representa um cliente cadastrado no sistema.

### Responsabilidades

* Armazenar dados cadastrais do cliente.
* Permitir alteração de nome.
* Permitir alteração de email.
* Garantir que CPF permaneça imutável após criação.

### Atributos

| Campo | Tipo       |
| ----- | ---------- |
| id    | UUID       |
| name  | PersonName |
| cpf   | Cpf        |
| email | Email      |

### Regras de negócio

* Nome não pode ser nulo.
* CPF não pode ser nulo.
* Email não pode ser nulo.
* CPF é imutável após criação.
* O cliente pode alterar nome e email.
* Alterações somente são aplicadas através dos métodos de domínio.

### Identidade

A identidade da entidade é definida pelo campo `id`.

---

## Account

Classe abstrata que representa uma conta bancária.

Serve como base para os diferentes tipos de conta do sistema.

### Responsabilidades

* Armazenar saldo.
* Realizar depósitos.
* Realizar saques.
* Controlar limites de saldo permitidos.
* Garantir validação de valores monetários.
* Manter informações de identificação da conta.

### Atributos

| Campo           | Tipo            |
| --------------- | --------------- |
| id              | UUID            |
| clientId        | UUID            |
| accountIdentity | AccountIdentity |
| creationTime    | LocalDateTime   |
| balance         | Money           |

### Regras de negócio

* Valores movimentados devem ser maiores que zero.
* Depósitos aumentam o saldo.
* Saques reduzem o saldo.
* O saldo nunca pode ficar abaixo do limite permitido pela implementação concreta.
* Contas só podem ser removidas quando o saldo for zero.

### Métodos abstratos

#### minimumAllowedBalance()

Define qual o menor saldo permitido para cada tipo de conta.

Implementado pelas subclasses.

### Identidade

A identidade da entidade é definida pelo campo `id`.

---

## CheckingAccount

Representa uma conta corrente.

Herda de `Account`.

### Responsabilidades

* Permitir utilização de limite especial (cheque especial).

### Regras de negócio

* Possui limite negativo de R$ 1.000,00.
* O saldo mínimo permitido é -R$ 1.000,00.

### Exemplo

Saldo atual:

R$ 100,00

Saque:

R$ 1.050,00

Saldo final:

-R$ 950,00

Operação permitida.

---

## SavingsAccount

Representa uma conta poupança.

Herda de `Account`.

### Responsabilidades

* Calcular e disponibilizar rendimentos mensais automaticamente.
* Controlar a data da última aplicação de juros.

### Atributos adicionais

| Campo                 | Tipo          |
| --------------------- | ------------- |
| lastInterestAppliedAt | LocalDateTime |

### Regras de negócio

* Não permite saldo negativo.
* Possui rendimento mensal de 0,5%.
* Juros são aplicados apenas sobre saldo positivo.
* Caso vários meses tenham passado sem movimentação, todos os rendimentos pendentes são aplicados de uma única vez.
* Cada aplicação de juros gera um valor de rendimento que posteriormente será transformado em transação pela Camada de Serviço.

### Exemplo

Saldo:

R$ 1.000,00

Rendimento mensal:

0,5%

Juro aplicado:

0,5% de 1000 = 5

Novo saldo:

R$ 1.005,00

---

## Transaction

Representa um registro histórico de movimentação financeira.

Uma transação é imutável após sua criação.

### Responsabilidades

* Registrar operações realizadas nas contas.
* Armazenar informações necessárias para geração de extrato.
* Representar depósitos, saques, transferências e rendimentos.

### Atributos

| Campo               | Tipo            |
| ------------------- | --------------- |
| id                  | UUID            |
| operationId         | UUID            |
| type                | TransactionType |
| amount              | Money           |
| sourceIdentity      | AccountIdentity |
| destinationIdentity | AccountIdentity |
| dateTime            | LocalDateTime   |

### Tipos suportados

* DEPOSIT
* WITHDRAW
* TRANSFER_SENT
* TRANSFER_RECEIVED
* INTEREST

### Regras de negócio

#### Depósito

* Deve possuir conta destino.
* Não deve possuir conta origem.

#### Saque

* Deve possuir conta origem.
* Não deve possuir conta destino.

#### Transferência

* Deve possuir conta origem.
* Deve possuir conta destino.
* Deve compartilhar o mesmo operationId entre TRANSFER_SENT e TRANSFER_RECEIVED.

#### Rendimento

* Deve possuir conta destino.
* Não deve possuir conta origem.

#### Valor

* Deve ser maior que zero.
* Não pode ser nulo.

### Factory Methods

A criação de transações é centralizada através de métodos estáticos:

* `deposit(...)`
* `withdraw(...)`
* `transferSent(...)`
* `transferReceived(...)`
* `interest(...)`

Isso garante que nenhuma transação inválida seja criada.

### Identidade

A identidade da entidade é definida pelo campo `id`.

---

## Enums

### AccountType

Representa os tipos de conta disponíveis.

Valores:

* CHECKING
* SAVINGS

---

### TransactionType

Representa os tipos de transação possíveis.

Valores:

* DEPOSIT
* WITHDRAW
* TRANSFER_SENT
* TRANSFER_RECEIVED
* INTEREST

---

# Agregados e Regras de Negócio do Domínio

Esta seção descreve como as entidades e Value Objects se relacionam conceitualmente dentro do domínio bancário.

O objetivo é explicar as fronteiras de responsabilidade e as regras que governam as interações entre os principais objetos do sistema.

---

# Estrutura da Entidade Client

O agregado Cliente é composto por:

```text
Client
 ├── PersonName
 ├── Cpf
 └── Email
```

O `Client` é a raiz do agregado.

Nenhum outro objeto possui permissão para modificar diretamente seus dados internos.

Todas as alterações devem ocorrer através dos métodos expostos pela própria entidade.

### Responsabilidades

* representar um cliente do banco;
* armazenar informações cadastrais;
* garantir integridade dos dados pessoais.

### Regras

* CPF é imutável após criação;
* nome pode ser alterado;
* email pode ser alterado;
* cada CPF deve ser único no sistema;
* cada email deve ser único no sistema.

---

# Estrutura da Entidade Account

A estrutura interna é composta por:

```text
Account
 ├── AccountIdentity
 └── Money (saldo)
```

A entidade `Account` é a raiz do agregado.

As subclasses concretas são:

```text
Account
 ├── CheckingAccount
 └── SavingsAccount
```

### Responsabilidades

* controlar saldo;
* realizar movimentações financeiras;
* aplicar regras específicas do tipo de conta.

### Regras

* depósitos devem possuir valor positivo;
* saques devem possuir valor positivo;
* o saldo nunca pode ultrapassar o limite definido pela conta;
* contas só podem ser removidas quando o saldo for zero.

---

# Relacionamento Cliente → Conta

Um cliente pode possuir várias contas.

```text
Client
   │
   ├── Account
   ├── Account
   └── Account
```

O relacionamento é identificado através de:

```
UUID clientId
```

armazenado dentro da entidade `Account`.

O domínio não mantém referências diretas entre objetos.

Isso reduz acoplamento e simplifica persistência.

### Regras

* uma conta sempre pertence a um único cliente;
* um cliente pode possuir várias contas;
* ao remover um cliente, todas as suas contas devem ser removidas;
* o cliente só pode ser removido quando todas as contas estiverem aptas para exclusão.

---

# Estrutura da Entidade Transaction

A estrutura da transação é composta por:

```text
Transaction
 ├── Money
 ├── AccountIdentity (origem)
 └── AccountIdentity (destino)
```

A entidade `Transaction` é imutável após sua criação.

Seu objetivo é registrar eventos financeiros ocorridos dentro do sistema.

### Responsabilidades

* registrar movimentações;
* armazenar histórico;
* permitir geração de extratos.

### Regras

Cada tipo de transação possui estrutura própria:

#### Depósito

```text
Origem: null
Destino: conta
```

#### Saque

```text
Origem: conta
Destino: null
```

#### Transferência

```text
Origem: conta
Destino: conta
OperationId obrigatório
```

#### Rendimento

```text
Origem: null
Destino: conta
```

---

# Relacionamento Conceitual Conta → Transação

Uma conta pode possuir várias transações registradas em seu histórico.

```text
Account
   │
   ├── Transaction
   ├── Transaction
   ├── Transaction
   └── Transaction
```

As transações não alteram o saldo.

Elas representam apenas o histórico dos eventos já executados.

O saldo é controlado exclusivamente pela entidade `Account`.

Essa separação evita inconsistências entre histórico e estado atual.

---

# Transferências

Uma transferência gera duas transações independentes:

```text
Conta A
   ↓
TRANSFER_SENT

Conta B
   ↓
TRANSFER_RECEIVED
```

Ambas compartilham o mesmo:

```text
operationId
```

Isso permite rastrear uma operação completa de transferência.

As duas transações são criadas de forma atômica pelo TransactionService, garantindo consistência entre origem e destino.

Exemplo:

```text
operationId = 123

Conta Origem
└── TRANSFER_SENT

Conta Destino
└── TRANSFER_RECEIVED
```

---

# Rendimentos da Poupança

Os rendimentos não são tratados como simples atualização de saldo.

Cada rendimento aplicado gera:

```text
1 atualização de saldo
+
1 Transaction INTEREST
```

Isso garante:

* rastreabilidade;
* auditoria;
* consistência do extrato.

---

# Invariantes do Domínio

As seguintes regras devem ser sempre verdadeiras:

### Cliente

* CPF único.
* Email único.

### Conta

* Saldo nunca ultrapassa o limite permitido.
* Conta só pode ser removida quando seu saldo for zero.

### Conta Corrente

* Saldo mínimo permitido: -R$ 1.000,00.

### Conta Poupança

* Saldo nunca negativo.
* Juros aplicados apenas sobre saldo positivo.

### Transações

* Valor sempre maior que zero.
* Transações são imutáveis.
* Transferências sempre possuem `operationId`.

### Sistema

* Toda conta pertence a um cliente.
* Todo histórico financeiro é representado por transações.
* Nenhum rendimento pode ser perdido mesmo após longos períodos sem movimentação.

---

# Princípios de Modelagem Utilizados

Esta seção descreve os principais princípios adotados durante a modelagem do domínio, explicando as decisões arquiteturais e de design utilizadas para manter o sistema coeso, expressivo e de fácil evolução.

---

## Encapsulamento de Regras de Negócio

Uma das principais diretrizes do projeto é que as regras de negócio pertencem às próprias entidades do domínio.

Ao invés de concentrar validações e comportamentos na Camada de Serviço, cada entidade é responsável por proteger sua própria consistência interna.

Exemplos:

- `Account` valida depósitos e saques;
- `SavingsAccount` controla o cálculo de juros;
- `Transaction` valida a coerência de cada tipo de movimentação;
- `Client` controla alterações de seus próprios dados.

Isso reduz duplicação de regras e evita estados inválidos.

---

## Domain-Driven Design (DDD) Tático

Embora o projeto não implemente DDD completo, diversos conceitos táticos foram adotados.

### Entidades

Objetos com identidade própria.

Exemplos:

- `Client`
- `Account`
- `Transaction`

São comparados por identificador (`UUID`) e não pelos seus atributos.

---

### Value Objects

Objetos imutáveis que representam conceitos do domínio.

Exemplos:

- `Cpf`
- `Email`
- `PersonName`
- `Money`
- `AccountIdentity`

Benefícios:

- validação centralizada;
- imutabilidade;
- sem identidade própria;
- igualdade por valor.

---

### Serviços de Domínio

A camada de Serviço coordena operações entre entidades sem armazenar estado.

Exemplos:

- `ClientService`
- `AccountService`
- `TransactionService`

Seu papel é orquestrar casos de uso utilizando as regras já definidas pelas entidades.

---

## Rich Domain Model

O projeto segue uma abordagem de **Rich Domain Model**.

As entidades possuem comportamento próprio e não atuam apenas como estruturas de dados.

Exemplos:

```
account.deposit(amount);
account.withdraw(amount);
savingsAccount.applyPendingInterests(clock);
```

Em vez de possuir objetos anêmicos e concentrar toda lógica na camada de serviço, o comportamento permanece próximo dos dados que ele manipula.

---

## Imutabilidade Sempre que Possível

Todos os Value Objects são imutáveis.

Exemplos:

```
Cpf
Email
Money
PersonName
AccountIdentity
```

Após criados, seus estados nunca mudam.

Benefícios:

- maior previsibilidade;
- ausência de efeitos colaterais;
- segurança para compartilhamento de objetos;
- simplificação de testes.

---

## Fail Fast

O sistema valida estados inválidos o mais cedo possível.

Sempre que uma condição inválida é detectada, uma exceção é lançada imediatamente.

Exemplos:

- CPF inválido;
- email inválido;
- saldo insuficiente;
- transferência para a própria conta;
- criação de transação inconsistente.

Isso evita propagação de estados incorretos pelo sistema.

---

## Inversão de Dependência Temporal

O domínio não depende diretamente do relógio do sistema.

Sempre que informações temporais são necessárias, utiliza-se:

```
Clock
```

Exemplos:

```
LocalDateTime.now(clock);
```

Benefícios:

- testes determinísticos;
- simulação de passagem do tempo;
- cálculo previsível de juros.

---

## Persistência Ignorada pelo Domínio

As entidades não possuem qualquer conhecimento sobre repositórios.

Nenhuma entidade conhece:

```
Repositórios
Serviços
```

Exemplo:

`SavingsAccount` calcula juros, mas não cria transações nem salva dados.

Essa separação mantém o domínio independente da infraestrutura.

---

## Separação Entre Regra e Registro Histórico

Uma decisão importante do projeto foi separar responsabilidades entre execução das regras de negócio e registro histórico.

### Regra de negócio

Executada pelas entidades.

Exemplo:

```
SavingsAccount.applyPendingInterests()
```

### Registro histórico

Executado pela camada de serviço.

Exemplo:

```
TransactionService
```

Assim:

- a conta calcula juros;
- o serviço registra movimentações;
- o domínio permanece desacoplado da persistência.

---

## Template Method para Variações de Conta

As diferenças entre tipos de conta são implementadas utilizando o padrão **Template Method**.

A classe abstrata:

```
Account
```

define o comportamento comum:

```
deposit()
withdraw()
increaseBalance()
decreaseBalance()
```

e delega apenas a regra variável:

```
minimumAllowedBalance()
```

para as subclasses.

Exemplos:

### CheckingAccount

Permite saldo negativo até o limite do cheque especial.

### SavingsAccount

Não permite saldo negativo.

Dessa forma:

- elimina duplicação;
- mantém um fluxo único de saque;
- facilita criação de novos tipos de conta.

---

## Juros Sob Demanda (Lazy Interest Application)

A conta poupança utiliza uma estratégia de **Lazy Interest Application**.

Ao invés de depender de processos agendados para aplicar rendimentos mensalmente, os juros são calculados apenas quando a conta é acessada.

Eventos que disparam a atualização:

- consulta de saldo;
- depósito;
- saque;
- transferência;
- consulta de extrato.

### Juros Retroativos

Caso a conta permaneça vários meses sem movimentação, todos os rendimentos pendentes são calculados retroativamente.

Exemplo:

- conta criada em janeiro;
- nenhum acesso até junho;
- ao acessar a conta em junho, todos os meses pendentes são processados automaticamente.

### Atualização Transparente

A atualização ocorre antes da execução da operação solicitada.

Dessa forma:

- o saldo sempre reflete todos os rendimentos devidos;
- nenhuma ação manual é necessária;
- não existe dependência de infraestrutura externa.

### Benefícios

- elimina necessidade de schedulers;
- reduz complexidade operacional;
- mantém consistência financeira;
- garante cálculo correto mesmo após longos períodos sem movimentação.

---

## Consistência Antes de Conveniência

Sempre que há conflito entre simplicidade de implementação e consistência do domínio, a consistência é priorizada.

Exemplos:

- remoção de cliente exige todas as contas zeradas;
- remoção de conta exige saldo zero;
- transferências geram duas transações independentes;
- juros geram registros próprios no extrato.

Isso garante que o estado do sistema permaneça coerente em qualquer momento.