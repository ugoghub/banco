Conta Bancária (Account)

A entidade Account representa uma conta bancária pertencente a um cliente.

Ela concentra o comportamento comum compartilhado por todos os tipos de conta do sistema.

Responsabilidades
armazenar saldo;
realizar depósitos;
realizar saques;
controlar identidade da conta;
registrar data de criação;
validar operações monetárias básicas.
Invariantes

Uma conta sempre deve respeitar as seguintes regras:

possui um identificador único (UUID);
pertence obrigatoriamente a um cliente;
possui uma identidade bancária válida (AccountIdentity);
saldo inicial sempre é zero;
depósitos devem possuir valor positivo;
saques devem possuir valor positivo;
o saldo nunca pode ultrapassar o limite mínimo permitido pelo tipo de conta.
Remoção de conta

Uma conta somente pode ser removida quando:

saldo = 0

Contas com qualquer valor positivo ou negativo não podem ser removidas.

Conta Corrente (CheckingAccount)

Representa uma conta corrente com limite de cheque especial.

Regras de negócio

A conta corrente permite saldo negativo até o limite de:

R$ 1.000,00

Exemplos:

Saldo após saque	Resultado
-500	permitido
-1000	permitido
-1000,01	rejeitado
Invariantes
saldo mínimo permitido: -1000;
saques acima do limite lançam InsufficientBalanceException.
Conta Poupança (SavingsAccount)

Representa uma conta com rendimento mensal composto.

Taxa de rendimento
0,5% ao mês
Regras de negócio

A conta poupança:

não permite saldo negativo;
aplica juros compostos mensalmente;
pode acumular vários meses pendentes;
não reaplica juros para o mesmo período.
Aplicação de juros

Quando houver meses pendentes:

calcula o rendimento do saldo atual;
adiciona o rendimento ao saldo;
avança o período de referência em um mês;
repete até alcançar a data atual.
Exemplo

Saldo inicial:

R$ 1000,00

Após um mês:

R$ 1005,00

Após dois meses:

R$ 1010,02
Comportamento sem saldo

Caso o saldo seja:

R$ 0,00

nenhum rendimento é gerado.

Além disso, períodos passados sem saldo não geram juros retroativos após futuros depósitos.

Invariantes
saldo mínimo permitido: 0;
juros só podem ser aplicados após completar um mês;
juros nunca são aplicados duas vezes para o mesmo período;
saldo nunca fica negativo.

Cliente (Client)

A entidade Client representa o titular de contas bancárias.

Responsabilidades
armazenar dados cadastrais;
permitir atualização de nome;
permitir atualização de email;
manter CPF imutável após criação.
Regras de negócio

Após criado:

o CPF não pode ser alterado;
o identificador interno (UUID) não pode ser alterado;
apenas nome e email podem sofrer atualização.
Identidade da entidade

Clientes são identificados internamente por:

UUID

O CPF é utilizado como identificador de negócio (business key), porém a identidade da entidade permanece sendo o UUID.

Motivação

Permitir que informações cadastrais sejam alteradas sem modificar a identidade do cliente dentro do sistema.

Transação (Transaction)

A entidade Transaction representa um evento financeiro registrado no histórico do sistema.

Objetivo

Garantir rastreabilidade completa das movimentações.

Cada operação financeira gera uma ou mais transações persistidas no histórico.

Tipos de transação
Depósito

Possui:

origem      = null
destino     = conta
Saque

Possui:

origem      = conta
destino     = null
Transferência enviada

Possui:

origem      = conta remetente
destino     = conta destinatária
Transferência recebida

Possui:

origem      = conta remetente
destino     = conta destinatária
Rendimento

Possui:

origem      = null
destino     = conta

Representa juros gerados automaticamente pela conta poupança.

Operação de transferência

Uma transferência gera duas transações distintas:

TRANSFER_SENT
TRANSFER_RECEIVED

Cada transação possui:

ID próprio;
mesmo OperationId.

Exemplo:

Transferência
├─ Transaction A (TRANSFER_SENT)
└─ Transaction B (TRANSFER_RECEIVED)

OperationId = compartilhado

Isso permite:

auditoria;
rastreamento;
reconciliação;
histórico individual por conta.
Invariantes da transação

Toda transação deve respeitar:

tipo obrigatório;
valor obrigatório;
valor maior que zero;
horário obrigatório;
combinação válida de origem e destino.

Exemplos:

Válido
DEPOSIT
origem = null
destino = conta
Inválido
DEPOSIT
origem = conta
destino = conta