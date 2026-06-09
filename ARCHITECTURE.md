Hierarquia de Contas

O sistema utiliza herança para modelar comportamentos específicos de tipos de conta.

Account
├── CheckingAccount
└── SavingsAccount

A classe abstrata Account concentra o comportamento comum.

As subclasses definem apenas regras específicas.

Template Method para Controle de Saldo

O método:

minimumAllowedBalance()

é utilizado para permitir que cada tipo de conta defina seu limite mínimo.

A lógica de saque permanece centralizada em Account.

Benefícios
elimina duplicação de código;
mantém validações em um único local;
facilita criação de novos tipos de conta.

Exemplo:

CheckingAccount -> -1000
SavingsAccount  -> 0
Encapsulamento das Alterações de Saldo

As alterações de saldo são realizadas exclusivamente pelos métodos protegidos:

increaseBalance()
decreaseBalance()

Isso impede que subclasses manipulem o saldo diretamente.

Benefícios
preserva invariantes;
reduz risco de inconsistências;
centraliza regras de alteração de estado.
Responsabilidade dos Juros

A lógica de cálculo dos juros pertence à entidade:

SavingsAccount

A entidade conhece:

taxa de rendimento;
período de aplicação;
cálculo composto.

Por outro lado, a entidade não conhece:

histórico;
transações;
repositories;
services.
Motivação

Manter a regra de negócio dentro do domínio e evitar acoplamento com infraestrutura.

Aplicação Tardia de Juros

O sistema não utiliza processamento agendado.

Os juros são calculados sob demanda através de:

applyPendingInterests(...)

Quando necessário, vários meses pendentes são aplicados de uma única vez.

Benefícios
não exige scheduler;
mantém o saldo consistente;
simplifica a arquitetura.

Factory Methods em Transaction

A criação de transações é centralizada através de métodos fábrica:

Transaction.deposit(...)
Transaction.withdraw(...)
Transaction.transferSent(...)
Transaction.transferReceived(...)
Transaction.interest(...)
Benefícios
elimina construtores complexos;
impede estados inválidos;
torna a intenção explícita;
melhora legibilidade.

Exemplo:

Transaction.deposit(...)

é mais expressivo que:

new Transaction(...)
Validação de Estado Interno

A entidade Transaction valida sua consistência internamente.

Exemplo:

validateTransactionState(...)

Isso garante que objetos inválidos nunca existam no sistema.

Benefício

A regra fica próxima dos dados que ela protege.

Enum como Linguagem Ubíqua

O sistema utiliza enums para representar conceitos do domínio.

AccountType
CHECKING
SAVINGS

Representa tipos de conta válidos.

TransactionType
DEPOSIT
WITHDRAW
TRANSFER_SENT
TRANSFER_RECEIVED
INTEREST

Representa eventos financeiros válidos.

Benefícios
elimina strings mágicas;
reduz erros;
melhora legibilidade;
facilita manutenção.