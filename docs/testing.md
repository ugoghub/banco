# Testes

## Visão Geral

Este documento descreve a estratégia de testes adotada no projeto Banco Digital e os cenários validados pela suíte automatizada.

O objetivo dos testes é garantir que as regras de negócio permaneçam corretas e estáveis durante a evolução do sistema, reduzindo riscos de regressão e fornecendo documentação executável para os comportamentos esperados do domínio.

A cobertura está organizada de acordo com as principais camadas da aplicação:

* Value Objects;
* Entidades de domínio;
* Serviços de aplicação;
* Operações financeiras;
* Regras de rendimento da conta poupança;
* Fluxos integrados da aplicação.

Os testes priorizam a validação de regras de negócio, comportamento do domínio e tratamento de cenários inválidos, verificando tanto fluxos de sucesso quanto condições excepcionais.

Entre os comportamentos cobertos estão:

* validação de objetos de valor;
* criação e gerenciamento de clientes;
* abertura e remoção de contas;
* depósitos, saques e transferências;
* controle de saldo e limite de cheque especial;
* cálculo e aplicação de juros da conta poupança;
* geração de histórico de transações;
* integração entre as camadas de serviço.

Cada seção deste documento apresenta os cenários testados e as garantias fornecidas pela suíte automatizada correspondente.

---

## Utilitários de Teste

Para reduzir duplicação e melhorar a legibilidade dos cenários foi criado o helper:

```text
AccountFactory
```

Esse utilitário fornece instâncias prontas de contas utilizadas pelos testes.

Exemplos:
```text
CheckingAccount account =
        AccountFactory.checking(clock);

SavingsAccount account =
        AccountFactory.savings(clock);
```
As contas geradas possuem:

* clientId válido;
* AccountIdentity válido;
* saldo inicial zero;
* Clock configurável.

O objetivo é permitir que cada teste foque apenas no comportamento que está sendo validado, eliminando código repetitivo de construção de objetos.

---

## Value Objects

Os Value Objects representam conceitos fundamentais do domínio e são responsáveis por garantir a integridade dos dados antes que estes sejam utilizados pelas entidades e serviços da aplicação.

Todos os Value Objects são imutáveis e validam seus próprios invariantes durante a construção.

### AccountIdentity

O Value Object `AccountIdentity` representa a identificação única de uma conta bancária através da combinação de agência e número da conta.

#### Cenários validados

**Criação**

* criação de identidade válida;
* suporte a agências e contas contendo zeros à esquerda.

**Validação**

* rejeição de agência com tamanho inválido;
* rejeição de agência contendo caracteres não numéricos;
* rejeição de agência nula;
* rejeição de agência vazia;
* rejeição de número de conta com formato inválido;
* rejeição de número de conta nulo;
* rejeição de número de conta contendo letras;
* rejeição de dígito verificador inválido.

**Igualdade**

* duas identidades com os mesmos valores são consideradas iguais;
* objetos equivalentes produzem o mesmo hash code.

---

### Cpf

O Value Object `Cpf` encapsula um CPF brasileiro válido e garante a consistência do documento através da validação dos dígitos verificadores.

#### Cenários validados

**Criação**

* criação de CPF válido;
* remoção automática de caracteres de formatação (`.` e `-`).

**Validação**

* rejeição de CPF nulo;
* rejeição de CPF contendo letras;
* rejeição de CPF composto por dígitos repetidos;
* rejeição de CPF com dígitos verificadores inválidos;
* rejeição de CPF contendo espaçamentos inválidos.

**Igualdade**

* CPFs equivalentes permanecem iguais independentemente da formatação utilizada;
* objetos equivalentes produzem o mesmo hash code.

---

### Email

O Value Object `Email` representa um endereço de e-mail válido e normalizado.

#### Cenários validados

**Criação**

* criação de e-mail válido;
* conversão automática para letras minúsculas;
* remoção de espaços externos.

**Normalização**

* e-mails equivalentes permanecem iguais após normalização;
* comparação independente de diferenças de capitalização.

**Validação**

* rejeição de formato inválido;
* rejeição de valor nulo;
* rejeição de valor vazio;
* rejeição de endereço sem domínio;
* rejeição de endereço sem usuário;
* rejeição de múltiplos símbolos `@`;
* rejeição de domínio inválido;
* rejeição de espaços internos.

---

### Money

O Value Object `Money` representa valores monetários utilizados em todas as operações financeiras do sistema.

#### Cenários validados

**Operações monetárias**

* soma de valores;
* subtração de valores;
* multiplicação por taxa;
* negação de valor;
* comparação entre valores;
* identificação de valores zero;
* identificação de valores negativos.

**Precisão monetária**

* arredondamento automático para duas casas decimais;
* arredondamento de resultados de multiplicação;

**Imutabilidade**

* operações retornam novos objetos sem alterar a instância original.

**Validação**

* rejeição de valores inválidos;
* rejeição de valores nulos;
* rejeição de operações utilizando argumentos nulos:

    * soma;
    * subtração;
    * multiplicação;
    * comparação.

**Igualdade**

* valores monetários equivalentes são considerados iguais independentemente da representação utilizada;
* objetos equivalentes produzem o mesmo hash code.

---

### PersonName

O Value Object `PersonName` representa nomes de pessoas utilizados pelo domínio.

#### Cenários validados

**Criação**

* criação de nomes válidos;
* remoção de espaços excedentes nas extremidades;
* normalização de múltiplos espaços internos;
* suporte a nomes compostos;
* suporte a caracteres acentuados.

**Validação**

* rejeição de nomes com tamanho inválido;
* rejeição de nomes nulos;
* rejeição de nomes compostos apenas por espaços.

**Igualdade**

* nomes equivalentes após normalização são considerados iguais;
* objetos equivalentes produzem o mesmo hash code.

---

### Garantias fornecidas pelos Value Objects

A suíte de testes garante que:

* dados inválidos são rejeitados no momento da criação;
* todos os objetos permanecem imutáveis após construídos;
* regras de normalização são aplicadas de forma consistente;
* igualdade e hash code respeitam o valor representado e não a identidade do objeto;
* operações monetárias aplicam arredondamento e comparação de forma consistente.

---

# Entidades

As entidades representam as regras de negócio centrais do domínio bancário. Os testes desta camada validam criação de objetos, operações financeiras, regras de remoção de contas, limites operacionais e aplicação de juros.

## Cliente

A entidade `Client` representa o titular de contas bancárias dentro do sistema.

Os testes desta entidade têm como objetivo garantir que um cliente nunca seja criado em estado inválido.

### Validação de criação

Os testes verificam que a construção da entidade exige obrigatoriamente todos os seus atributos fundamentais:

* nome válido (`PersonName`);
* CPF válido (`Cpf`);
* e-mail válido (`Email`).

Os seguintes cenários são cobertos:

* rejeição de criação com nome nulo;
* rejeição de criação com CPF nulo;
* rejeição de criação com e-mail nulo.


A entidade não realiza validações de formato diretamente. Ela exige instâncias válidas de PersonName, Cpf e Email, delegando essas validações aos respectivos Value Objects.

### Garantias fornecidas

A suíte de testes garante que:

* todo cliente possui nome válido;
* todo cliente possui CPF válido;
* todo cliente possui e-mail válido;
* a entidade nunca pode ser instanciada com informações obrigatórias ausentes.

As regras específicas de validação de nome, CPF e e-mail são delegadas aos respectivos Value Objects e testadas individualmente em suas próprias suítes.

---

## Conta

A classe abstrata `Account` concentra o comportamento comum entre contas correntes e contas poupança.

### Criação da conta

Os testes garantem que:

* toda conta inicia com saldo zero;
* a data de criação é registrada utilizando o `Clock` fornecido;
* não é possível criar contas com:

    * `clientId` nulo;
    * `AccountIdentity` nulo;
    * `Clock` nulo.

### Depósitos

Os testes validam que:

* depósitos aumentam corretamente o saldo da conta;
* valores negativos não são aceitos;
* depósitos com valor zero são rejeitados.

### Saques

Os testes garantem que:

* saques reduzem corretamente o saldo;
* valores negativos não são aceitos;
* saques com valor zero são rejeitados.

### Remoção da conta

A remoção de uma conta só é permitida quando o saldo é exatamente zero.

Os testes verificam que:

* contas sem saldo podem ser removidas;
* contas com saldo positivo não podem ser removidas;
* uma conta volta a ser removível após retornar ao saldo zero.

---

## ContaCorrente

A entidade `CheckingAccount` implementa o comportamento específico da conta corrente, incluindo suporte a cheque especial de R$ 1.000,00.

A conta corrente permite saldo negativo até o limite de cheque especial configurado.

### Limite de crédito

Os testes validam que:

* a conta pode utilizar integralmente o limite disponível;
* não é possível ultrapassar o limite definido;
* múltiplos saques sucessivos respeitam o limite acumulado.

### Recuperação de saldo negativo

Também é validado que:

* depósitos reduzem corretamente saldos negativos;
* a conta retorna para saldo positivo após depósitos suficientes;
* saques são permitidos quando a soma entre saldo disponível e limite cobre o valor solicitado.

---

## ContaPoupança

A entidade `SavingsAccount` implementa as regras de rendimento mensal da conta poupança.

### Aplicação de juros

Os testes cobrem os seguintes cenários:

* aplicação de juros após um mês completo;
* ausência de rendimento antes da data de aniversário;
* impossibilidade de aplicar juros duas vezes no mesmo período;
* aplicação sucessiva de juros em meses futuros;
* processamento de vários meses pendentes em uma única execução;
* cálculo correto da capitalização composta ao longo do tempo.

### Retorno do Método de Aplicação de Juros

O método responsável pela aplicação dos juros retorna uma lista contendo os rendimentos efetivamente aplicados durante a execução.

Essa abordagem permite que camadas superiores convertam os rendimentos em transações de histórico sem acoplar o domínio à persistência.

### Capitalização Composta

Os rendimentos são calculados utilizando capitalização composta, fazendo com que os juros de meses anteriores passem a compor a base de cálculo dos meses seguintes.


### Regras de aniversário

Os testes garantem que:

* o rendimento só ocorre após a data de aniversário da conta;
* datas anteriores ao aniversário não geram rendimento.

### Contas sem saldo

Também é validado que:

* contas com saldo zero não geram juros;
* meses passados sem saldo não produzem rendimentos retroativos após um depósito futuro.

### Precisão monetária

Os testes verificam:

* arredondamento correto dos valores de rendimento;
* manutenção da consistência para valores muito pequenos.

### Restrições de saque

Por se tratar de uma conta poupança:

* não é permitido que o saldo fique negativo;
* saques acima do saldo disponível geram exceção de saldo insuficiente.

---

## Transação

A entidade `Transaction` representa o registro imutável de uma movimentação financeira realizada no sistema.

Seu objetivo é preservar o histórico das operações executadas pelas contas, permitindo rastreabilidade, auditoria e geração de extratos.

Os testes validam a criação e o comportamento dos diferentes tipos de transação suportados pelo domínio:

* depósito (`DEPOSIT`);
* saque (`WITHDRAW`);
* transferência enviada (`TRANSFER_SENT`);
* transferência recebida (`TRANSFER_RECEIVED`);
* rendimento de poupança (`INTEREST`).

### Identificadores

Toda transação recebe um identificador único (`id`) gerado no momento da criação.

Os testes garantem que:

* transações distintas nunca compartilham o mesmo identificador;
* registros de uma mesma transferência possuem IDs diferentes;
* apenas o `operationId` é compartilhado entre as duas partes da transferência.

### Criação de transações

Os testes garantem que cada transação:

* recebe um identificador único;
* registra corretamente o valor da operação;
* utiliza o `Clock` fornecido para definição da data e hora;
* mantém consistência entre tipo, origem e destino da operação.

### Depósitos

Uma transação de depósito deve:

* possuir tipo `DEPOSIT`;
* registrar apenas a conta de destino;
* não possuir conta de origem;
* armazenar o valor depositado.

### Saques

Uma transação de saque deve:

* possuir tipo `WITHDRAW`;
* registrar apenas a conta de origem;
* não possuir conta de destino;
* armazenar o valor sacado.

### Transferências

Uma transferência é representada por duas transações distintas:

* `TRANSFER_SENT`, associada à conta de origem;
* `TRANSFER_RECEIVED`, associada à conta de destino.

Os testes verificam que:

* ambas registram corretamente origem e destino;
* cada registro possui identificador próprio;
* os dois registros compartilham o mesmo `operationId`, permitindo relacionar as duas partes da mesma operação.

### Rendimentos

Os rendimentos de contas poupança são registrados através de transações do tipo `INTEREST`.

Os testes garantem que:

* a transação é criada corretamente;
* não existe conta de origem;
* a conta beneficiada é registrada como destino;
* o valor do rendimento é preservado.

### OperationId

Transferências utilizam um identificador de operação compartilhado (`operationId`).

Esse identificador permite relacionar os registros de envio e recebimento pertencentes à mesma transferência.

Os testes garantem que:

* o `operationId` é preservado em ambos os registros;
* transferências não podem ser criadas com `operationId` nulo.

### Validações

Todos os tipos de transação exigem um valor monetário positivo.

Os testes garantem que não é possível criar transações com:

* contas nulas;
* `Clock` nulo;
* `operationId` nulo em transferências;
* valor nulo;
* valor igual a zero;
* valor negativo;

Essas validações garantem que apenas movimentações consistentes possam ser registradas no histórico financeiro do sistema.

---

# Testes da Camada de Serviço

Os testes da camada de serviços validam os casos de uso da aplicação e garantem a correta coordenação entre entidades, repositórios e regras de negócio.

Enquanto os testes de domínio verificam comportamentos isolados de entidades e value objects, os testes de serviço asseguram que operações completas sejam executadas corretamente, preservando a integridade dos dados e o estado do sistema.

---

## AccountServiceTest

O `AccountService` é responsável pela criação, consulta e remoção de contas bancárias.

### Criação de contas

Os testes verificam a criação correta dos diferentes tipos de conta suportados pelo sistema:

* criação de contas correntes (`CheckingAccount`);
* criação de contas poupança (`SavingsAccount`);
* recuperação da conta criada através de sua identidade bancária;
* listagem das contas pertencentes a um cliente;
* retorno de lista vazia para clientes sem contas.

Além disso, é validado que a implementação cria efetivamente instâncias do tipo correto de acordo com o `AccountType` informado.

### Consulta de contas

Os testes garantem:

* recuperação de uma conta através da sua identidade;
* recuperação de todas as contas vinculadas a um cliente;
* comportamento correto para clientes sem contas cadastradas.

### Remoção de contas

A remoção de contas é validada considerando as regras de negócio do domínio:

* contas com saldo zerado podem ser removidas;
* contas com saldo diferente de zero não podem ser removidas;
* remoção individual de contas válidas;
* remoção em lote de todas as contas pertencentes ao cliente;
* impossibilidade de consultar contas removidas.

### Regras para remoção de clientes

O serviço também valida se um cliente pode ter todas as suas contas removidas:

* clientes sem contas podem ser removidos;
* clientes cujas contas possuem saldo exatamente igual a zero podem ser removidos;
* clientes que possuem qualquer conta com saldo diferente de zero não podem ser removidos.

### Validação para remoção de clientes

Antes da remoção de um cliente, o serviço verifica se todas as suas contas estão aptas para exclusão.

Os testes garantem que:

* clientes sem contas podem ser removidos;
* clientes cujas contas possuem saldo igual a zero podem ser removidos;
* a existência de qualquer conta com saldo diferente de zero impede a remoção;
* a validação considera corretamente múltiplas contas vinculadas ao mesmo cliente.

### Validações

São testados cenários inválidos para garantir robustez da aplicação:

* consulta de conta inexistente;
* tentativa de remoção de conta inexistente;
* tentativa de remoção de cliente que possui contas com saldo não zerado.

---

## ClientServiceTest

O `ClientService` é responsável pelo gerenciamento dos clientes cadastrados no sistema.

### Cadastro de clientes

Os testes verificam:

* criação de clientes e posterior recuperação de seus dados de identificação;
* recuperação do CPF através do e-mail;
* recuperação do identificador interno (`UUID`) através do CPF.

### Regras de unicidade

O sistema impõe restrições para evitar duplicidade de dados.

Os testes garantem:

* impossibilidade de cadastrar dois clientes com o mesmo CPF;
* impossibilidade de cadastrar dois clientes com o mesmo e-mail;
* impossibilidade de alterar o e-mail para um endereço já utilizado por outro cliente.

### Atualização de dados

As operações de alteração cadastral são validadas através dos seguintes cenários:

* alteração de nome;
* alteração de e-mail;
* persistência correta das alterações realizadas;
* recuperação dos novos valores após a atualização;
* rejeição de alteração para o mesmo nome atual do cliente.
* rejeição de alteração para o mesmo email atual do cliente.
* rejeição de alteração para um email já utilizado por outro cliente.

Também é validado que, após a alteração do e-mail, o endereço antigo deixa de ser utilizado como chave de busca.

### Remoção de clientes

Os testes garantem:

* remoção correta de clientes existentes;
* impossibilidade de localizar clientes removidos por CPF;
* impossibilidade de localizar clientes removidos por e-mail.

### Validações

São verificados diversos cenários de erro:

* alteração de nome de cliente inexistente;
* alteração de e-mail de cliente inexistente;
* remoção de cliente inexistente;
* consulta de e-mail inexistente;

### Garantias fornecidas pela suíte

Os testes asseguram que:

* CPF e e-mail permaneçam únicos no sistema;
* alterações cadastrais atualizem corretamente os mecanismos de busca;
* clientes removidos deixem de estar acessíveis por qualquer forma de consulta;
* as exceções de domínio sejam lançadas para operações inválidas.

Esses testes asseguram que o serviço responda adequadamente a operações inválidas e mantenha a consistência dos dados da aplicação.

---

## TransactionServiceTest

O `TransactionService` concentra as operações financeiras da aplicação e é responsável por coordenar alterações de saldo, validações de negócio e geração do histórico de transações.

Os testes desta classe garantem a consistência das movimentações financeiras e a integridade dos registros gerados pelo sistema.

### Operações financeiras gerais

Os testes verificam cenários envolvendo múltiplas operações consecutivas para assegurar a consistência dos cálculos financeiros.

São validados:

* manutenção correta do saldo após diversas operações;
* preservação da precisão monetária;
* ausência de erros de arredondamento em operações sucessivas;
* consistência do saldo final independentemente da sequência de movimentações realizadas.

### Depósitos

Os testes garantem:

* realização de depósitos válidos;
* atualização correta do saldo da conta;
* rejeição de depósitos com valor zero;
* rejeição de depósitos com valores negativos;
* validação da existência da conta de destino.

Também é verificado que depósitos em contas inexistentes resultam na exceção apropriada.

### Saques

Os testes cobrem:

* realização de saques válidos;
* atualização correta do saldo após saque;
* rejeição de valores iguais a zero;
* rejeição de valores negativos;
* validação da existência da conta.

Além disso, são verificadas as regras específicas de cada tipo de conta.

#### Conta corrente

Os testes garantem:

* utilização do limite de cheque especial;
* alcance exato do limite permitido;
* bloqueio de saques que ultrapassem o limite configurado.

#### Conta poupança

Os testes garantem:

* impossibilidade de saldo negativo;
* bloqueio de saques superiores ao saldo disponível.

### Transferências

As transferências representam operações compostas e exigem validações adicionais.

Os testes verificam:

* atualização simultânea das contas de origem e destino;
* rejeição de transferências para a própria conta;
* rejeição de transferências com valor zero;
* rejeição de transferências com valor negativo;
* validação da existência da conta de destino.

Também são validados cenários relacionados à consistência transacional.
 
#### Consistência da Transferência

Os testes garantem que:

* o crédito somente ocorra após a conclusão bem-sucedida do débito;
* falhas em qualquer etapa impeçam movimentações parciais;
* nenhuma alteração de saldo seja aplicada quando a transferência é rejeitada;
* os saldos permaneçam consistentes após falhas.

Isso assegura que a transferência seja tratada como uma operação indivisível.

### Histórico de transações

O serviço é responsável pela criação e manutenção do extrato financeiro das contas.

Os testes verificam:

* retorno de histórico vazio para contas sem movimentação;
* preservação da ordem de registro das transações;
* geração de identificadores únicos para cada registro;
* criação correta de transações de depósito;
* criação correta de transações de saque;
* criação correta das transações TRANSFER_SENT e TRANSFER_RECEIVED.

### Histórico de depósitos

Os testes garantem que:

* depósitos gerem exatamente um registro;
* a transação seja classificada como `DEPOSIT`;
* a conta de destino seja registrada corretamente;
* não exista conta de origem associada.

### Histórico de saques

Os testes garantem que:

* saques gerem registros do tipo `WITHDRAW`;
* a conta de origem seja armazenada corretamente;
* não exista conta de destino associada.

### Histórico de transferências

Geração de identificadores únicos para cada transação.

Compartilhamento de operationId entre os dois lados da mesma transferência.

Os testes verificam a geração correta dos dois lados da operação:

#### Conta de origem

* criação de registro `TRANSFER_SENT`;
* associação correta da conta de origem;
* associação correta da conta de destino.

#### Conta de destino

* criação de registro `TRANSFER_RECEIVED`;
* associação correta da conta de origem;
* associação correta da conta de destino.

### Integridade dos registros de transferência

Os testes garantem que:

* cada lado da transferência possua um identificador próprio;
* os registros compartilhem o mesmo `operationId`;
* as informações de origem e destino permaneçam consistentes;
* a correlação entre as duas transações seja preservada.

### Falhas e histórico

Também são verificados cenários onde a operação não deve gerar registros:

* transferência rejeitada por saldo insuficiente;
* transferência para conta inexistente;
* falha durante as validações da operação.

Nesses casos, o histórico permanece inalterado, garantindo consistência entre o estado financeiro e os registros armazenados.

### Ordenação do histórico

Por fim, os testes validam que:

* a sequência das movimentações seja preservada;
* múltiplas transferências apareçam na ordem em que foram executadas;
* o extrato reflita corretamente a cronologia das operações realizadas.

---

## TransactionServiceInterestTest

O `TransactionServiceInterestTest` valida a integração entre o mecanismo de rendimento da conta poupança e o fluxo de operações financeiras da aplicação.

Enquanto os testes da entidade `SavingsAccount` garantem o cálculo correto dos juros, esta suíte verifica que os juros pendentes são aplicados automaticamente pelo serviço antes da execução de operações que dependem do saldo atualizado da conta.

### Aplicação automática de juros

Os testes garantem que juros pendentes sejam processados automaticamente antes de qualquer operação que dependa do estado atualizado da conta.

São verificados os seguintes cenários:

* aplicação de juros antes de depósitos;
* aplicação de juros antes de saques;
* aplicação de juros antes de transferências;
* aplicação de juros durante consultas de saldo;
* aplicação de juros durante consultas de extrato.

Essa abordagem garante que o saldo da conta permaneça consistente mesmo após longos períodos sem movimentação.

### Depósitos

Os testes verificam que:

* juros pendentes são aplicados antes da realização do depósito;
* o saldo utilizado na operação já considera os rendimentos acumulados;
* uma transação de rendimento é registrada antes da transação de depósito.

Também é validada a ordem correta dos registros no histórico:

1. depósito inicial;
2. rendimento;
3. depósito solicitado.

### Saques

Os testes garantem que:

* juros pendentes sejam processados antes do saque;
* o valor disponível para saque considere os rendimentos acumulados;
* o histórico registre primeiro a transação de rendimento e posteriormente o saque.

Dessa forma, a operação sempre utiliza o saldo mais atualizado possível.

### Consultas de saldo

Os testes verificam que:

* consultar o saldo dispara automaticamente a aplicação de juros pendentes;
* o saldo retornado já considera os rendimentos acumulados;
* consultas consecutivas não reaplicam juros para o mesmo período.

Esse comportamento impede duplicidade de rendimentos e garante idempotência da operação.

### Transferências

Os testes validam a integração entre juros e transferências bancárias.

São garantidos os seguintes comportamentos:

* aplicação de juros antes da transferência;
* utilização do saldo atualizado no momento do débito;
* manutenção da consistência dos saldos de origem e destino.

Também é validado que ambas as contas envolvidas recebem seus respectivos rendimentos pendentes antes da transferência ser processada.

### Geração de transações de rendimento

Uma das responsabilidades introduzidas pela refatoração foi tornar os rendimentos visíveis no extrato bancário.

Os testes verificam que:

* cada aplicação de juros gera uma transação do tipo `INTEREST`;
* as transações de rendimento são registradas no histórico da conta;
* os registros aparecem na ordem correta em relação às demais operações.

Essa validação garante que o histórico reflita todas as alterações de saldo realizadas automaticamente pelo sistema.

### Aplicação de múltiplos meses pendentes

Os testes cobrem cenários em que uma conta permanece sem movimentação durante vários meses.

São verificadas:

* aplicação de um rendimento para cada mês pendente;
* criação de uma transação INTEREST individual para cada mês pendente processado.
* atualização correta do saldo final após múltiplas capitalizações sucessivas.

Esse cenário garante que o sistema consiga recuperar corretamente períodos extensos sem movimentação financeira.

### Consulta de extrato

Os testes também verificam que:

* consultar o histórico da conta dispara a aplicação de juros pendentes;
* o extrato retornado já inclui as transações de rendimento geradas automaticamente;
* a consulta não produz duplicidade de registros.

Assim como na consulta de saldo, consultas repetidas ao extrato não geram novas aplicações de juros para períodos já processados.

Esse comportamento assegura que qualquer visualização do histórico reflita o estado financeiro real da conta no momento da consulta.

### Garantias fornecidas pela suíte

Em conjunto, os testes desta classe asseguram que:

* juros nunca sejam esquecidos antes de operações financeiras;
* rendimentos não sejam aplicados mais de uma vez para o mesmo período;
* todas as aplicações de juros sejam auditáveis através do histórico;
* saldos e extratos permaneçam sincronizados;
* contas poupança mantenham consistência mesmo após longos períodos sem movimentação.
* rendimentos sejam registrados no histórico antes das operações que dispararam sua aplicação.

---

# Testes da Camada de Aplicação

## ApplicationServiceTest

O `ApplicationService` atua como fachada da aplicação, centralizando o acesso aos serviços responsáveis pelo gerenciamento de clientes, contas e transações.

Os testes desta classe verificam os fluxos completos da aplicação, garantindo que as operações executadas através da camada de fachada produzam os resultados esperados.

### Criação de contas

Os testes garantem que:

* clientes existentes possam criar novas contas;
* a identidade da conta criada seja retornada corretamente;
* a integração entre cadastro de cliente e criação de conta funcione adequadamente.

Esses cenários validam o fluxo completo de abertura de contas através da API da aplicação.

### Consulta de dados do cliente

Os testes verificam:

* recuperação dos dados cadastrais do cliente;
* retorno correto de nome, CPF e e-mail;
* integridade das informações retornadas pela fachada.

Também é validada a recuperação da lista de contas vinculadas ao cliente.

### Consulta de contas do cliente

Os testes garantem que:

* contas correntes e poupanças sejam associadas corretamente ao cliente;
* a listagem de contas retorne todas as contas cadastradas;
* a quantidade retornada corresponda ao número de contas efetivamente criadas.

### Atualização de dados cadastrais

As operações de alteração de dados são verificadas através dos seguintes cenários:

* alteração de nome;
* alteração de e-mail;
* retorno do novo valor após a atualização.

Esses testes garantem que a fachada delegue corretamente as operações ao serviço responsável.

### Operações financeiras

Os testes validam fluxos financeiros completos utilizando apenas a interface pública da aplicação.

#### Depósitos

É garantido que:

* depósitos possam ser realizados em contas existentes;
* o saldo seja atualizado corretamente após a operação;
* a consulta de saldo reflita o valor depositado.

#### Transferências

Os testes verificam:

* transferência entre contas pertencentes ao mesmo cliente;
* atualização correta do saldo da conta de origem;
* atualização correta do saldo da conta de destino;
* integração adequada entre os serviços de conta e transação.

Esses cenários validam operações financeiras ponta a ponta através da camada de aplicação.

### Remoção de clientes

Os testes garantem que:

* clientes sem contas possam ser removidos;
* o processo seja concluído sem lançamento de exceções quando todas as regras forem satisfeitas.

### Validações

Também são verificados cenários inválidos para garantir o comportamento correto da aplicação.

Os testes cobrem:

* tentativa de criar conta para cliente inexistente;
* tentativa de remover cliente inexistente;
* tentativa de consultar contas de cliente inexistente.

Nesses casos, a aplicação deve propagar corretamente as exceções de domínio, preservando a consistência das regras de negócio.

### Garantias fornecidas pela suíte

Em conjunto, os testes do `ApplicationService` asseguram que:

* a fachada exponha corretamente os principais casos de uso do sistema;
* os serviços internos sejam coordenados adequadamente;
* o ApplicationContext componha corretamente os serviços utilizados pela aplicação;
* operações completas possam ser executadas através de uma única interface;
* exceções de domínio sejam propagadas corretamente;
* os fluxos principais da aplicação funcionem de forma integrada.

---

# Infraestrutura de Testes

A suíte de testes foi implementada utilizando **JUnit 5** e tem como objetivo principal validar regras de negócio, consistência do domínio e integração entre os serviços da aplicação.

Todos os testes são executados inteiramente em memória, sem dependência de banco de dados, sistemas externos ou mecanismos de mensageria. Essa abordagem proporciona execuções rápidas, previsíveis e independentes do ambiente.

Os testes utilizam implementações de repositório sem dependências externas, permitindo que cada cenário seja executado de forma isolada e determinística.

Funcionalidades dependentes de tempo são validadas através da injeção de instâncias de `Clock`, garantindo a reprodução exata de cenários relacionados ao cálculo de juros e demais regras temporais.

---

# Estratégia de Cobertura

A estratégia de testes prioriza a validação do comportamento da aplicação e das regras de negócio, evitando dependência de detalhes internos de implementação.

A cobertura está organizada de acordo com as camadas da arquitetura:

* Value Objects;
* Entidades de Domínio;
* Camada de Serviços;
* Camada de Aplicação.

A suíte contempla tanto a validação isolada de regras específicas quanto a validação de fluxos completos da aplicação.

Os principais cenários cobertos incluem:

* validação de dados;
* normalização de valores;
* contratos de igualdade e hash code;
* gerenciamento de clientes;
* gerenciamento de contas;
* cálculos monetários;
* depósitos;
* saques;
* transferências;
* geração de histórico de transações;
* regras de limite especial;
* cálculo de juros da poupança;
* aplicação automática de juros pendentes;
* integração entre serviços;
* orquestração realizada pela camada de aplicação.

O objetivo é garantir que os cenários críticos do domínio financeiro sejam executados corretamente, mantendo os testes simples, independentes e fáceis de manter.

---

# Resumo da Cobertura

A suíte atual fornece cobertura para os seguintes componentes:

## Value Objects

* AccountIdentity
* Cpf
* Email
* Money
* PersonName

## Entidades de Domínio

* Account
* CheckingAccount
* SavingsAccount
* Client
* Transaction

## Camada de Serviços

* ClientService
* AccountService
* TransactionService

## Processamento de Juros

* aplicação automática de juros;
* geração de transações de rendimento;
* recuperação de juros pendentes;
* integração dos rendimentos ao extrato da conta.

## Camada de Aplicação

* ApplicationService

---

# Observações

Componentes de infraestrutura não possuem atualmente suítes de testes dedicadas.

Os repositórios em memória são exercitados indiretamente pelos testes das camadas de serviço e aplicação. Como essas suítes utilizam as implementações reais de armazenamento em memória, operações de persistência, consulta, atualização e remoção são verificadas como parte dos fluxos de negócio testados.

Por outro lado, componentes como a camada de interface com o usuário (UI) e as classes de inicialização da aplicação não fazem parte da suíte de testes atual.

O foco dos testes está concentrado na validação das regras de negócio, na consistência das operações financeiras e na integração entre os serviços e o domínio, que representam o núcleo funcional da aplicação.

Dessa forma, a cobertura existente valida os principais fluxos da aplicação, os invariantes do domínio bancário e a integração entre as camadas da arquitetura, fornecendo um elevado grau de confiança na correção das regras de negócio implementadas.