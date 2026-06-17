# Interface de Usuário (UI)

## Visão Geral

A camada de interface é responsável pela interação entre o usuário e o sistema.

Seu objetivo é receber entradas, apresentar informações e encaminhar solicitações para a camada de aplicação.

Toda regra de negócio permanece fora da interface.

A Interface atua apenas como um mecanismo de comunicação entre o usuário e os casos de uso disponíveis.

---

## Responsabilidades

A camada de interface é responsável por:

* exibir menus;
* coletar entradas do usuário;
* validar formatos básicos de entrada;
* exibir mensagens;
* apresentar resultados;
* encaminhar operações para a camada de aplicação.

Não é responsabilidade da interface:

* executar regras de negócio;
* acessar repositórios;
* manipular entidades diretamente;
* realizar cálculos financeiros.

---

## Estrutura Geral

A camada é composta pelos seguintes elementos:

```text
App
Controllers
Menus
Formatters
Selectors
InputReader
ConsoleMessages
```

---

## App

Representa o ponto de entrada da interface.

Responsável por:

* iniciar a aplicação;
* exibir o menu principal;
* controlar o fluxo geral de navegação;
* delegar operações aos controllers apropriados.

Fluxo simplificado:

```text
Usuário
   │
   ▼
  App
   │
   ▼
Controller
```

---

## Controllers

Os controllers representam agrupamentos de funcionalidades da interface.

Cada controller é responsável por um conjunto específico de operações.

Exemplo conceitual:

```text
AuthController

ClientController

AccountController

AccountTransactionController
```

---

### Responsabilidades

* solicitar dados ao InputReader;
* encaminhar operações para o ApplicationService;
* apresentar resultados;
* exibir mensagens de erro;
* capturar DomainException e convertê-las em mensagens amigáveis ao usuário.

---

### AuthController

Responsável por:

* login via CPF;
* login via email;
* cadastro de clientes.

### ClientController

Responsável por:

* exibição dos dados do cliente;
* valteração de nome;
* alteração de email;
* remoção de cliente.

### AccountController

Responsável por:

* criação de contas;
* remoção de contas;
* seleção de contas;
* navegação para o menu da conta.

### AccountTransactionController

Responsável por:

* depósitos;
* saques;
* transferências;
* consulta de saldo;
* consulta de extrato.

---

### Benefícios

A separação por controllers permite:

* organização da interface;
* redução de métodos excessivamente grandes;
* facilidade de manutenção;
* maior legibilidade.

---

## InputReader

Classe utilitária responsável pela leitura e validação inicial dos dados digitados pelo usuário.

---

### Objetivos

Evitar repetição de código relacionado à entrada de dados.

Exemplos:

```text
Leitura de CPF

Leitura de Email

Leitura de Nome

Leitura de Valores Monetários

Leitura de Opções de Menu
```

---

### Estratégia Utilizada

A leitura normalmente segue o fluxo:

```text
Solicitar valor
      │
      ▼
Converter
      │
      ▼
Validar
      │
      ▼
Retornar objeto
```

Quando ocorre erro:

```text
Exceção
      │
      ▼
Mensagem ao usuário
      │
      ▼
Nova tentativa
```

Essa abordagem melhora a experiência de uso e evita propagação de entradas inválidas.

---

## Menu

Responsável exclusivamente pela apresentação visual dos menus.

---

### Objetivos

Centralizar a exibição das opções disponíveis.

Exemplo:

```text
1 - Criar conta bancária
2 - Acessar conta bancária
3 - Mostrar meus dados
4 - Alterar meus dados
5 - Excluir conta bancária
```

---

### Benefícios

A separação entre exibição e controle permite:

* menus mais organizados;
* menor duplicação;
* facilidade para alterar a aparência da interface.

---

## Formatters

Responsáveis por transformar objetos do domínio e DTOs em representações adequadas para exibição ao usuário.

Exemplos:

```text
CpfFormatter
MoneyFormatter
ClientFormatter
StatementFormatter
AccountIdentityFormatter
```

### Responsabilidades:

* formatação de CPF;
* formatação monetária;
* formatação de extratos;
* formatação de identidades de conta;
* formatação de dados do cliente.

### Benefícios
* centralização da lógica de apresentação;
* eliminação de duplicação;
* separação entre exibição e domínio.

---

## Selectors

Componentes auxiliares responsáveis por permitir que o usuário escolha um elemento dentre uma coleção disponível.

Exemplo:

```text
AccountSelector
```

### Responsabilidades
* exibir opções numeradas;
* validar a escolha do usuário;
* retornar o objeto selecionado.

### Benefícios
* reutilização da lógica de seleção;
* redução de duplicação nos controllers;
* maior organização da interface.

---

## ConsoleMessages

Classe utilitária responsável pela exibição padronizada de mensagens no console.

### Tipos de mensagem
```text
Info
Success
Highlight
Error
```
### Responsabilidades
* centralizar saída textual;
* padronizar mensagens;
* aplicar cores ANSI;
* melhorar legibilidade da interface.

---

## Fluxo de Execução

Uma operação típica segue o fluxo abaixo:

```text
Fluxo de entrada

Usuário
   │
   ▼
Controller
   │
   ├── InputReader
   │
   ▼
ApplicationService
   │
   ▼
Serviços
   ├── Domínio
   └── Repositórios
```

Após a execução:

```text
Fluxo de saída

Resultado
   │
   ▼
ApplicationService
   │
   ▼
Controllers
   │
   ▼
Interface (Formatação / Mensagens)
   │
   ▼
Usuário
```

---

## Tratamento de Exceções

A interface é responsável por capturar exceções geradas pelas camadas inferiores e convertê-las em mensagens amigáveis.

Exemplo:

```text
InsufficientBalanceException
            │
            ▼
"Saldo insuficiente"
```

A interface não tenta corrigir erros de domínio.

Ela apenas apresenta a mensagem adequada ao usuário.

---

## Conversão para Value Objects

A interface converte entradas em Value Objects antes de encaminhar a operação para o ApplicationService.

Exemplo:

```text
Cpf::new
Email::new
PersonName::new
Money.of(...)
```

Isso garante que todas as validações ocorram antes da execução dos casos de uso.

---

## Dependências

A interface depende apenas da camada de aplicação.

```text
Camada de Interface
 │
 ▼
Camada de Aplicação
```

Ela não possui acesso direto a:

- Serviços internos da aplicação;
- Repositórios;
- Entidades do domínio;
- Estruturas internas de persistência.

A interface pode utilizar Value Objects para validar e representar dados antes de encaminhar operações para a camada de aplicação.

---

## Autenticação

O sistema utiliza um mecanismo simplificado de autenticação para fins acadêmicos.

O acesso pode ser realizado através de:

* CPF;
* email.

Não existe controle de senha ou autenticação persistente.

---

## Benefícios da Estrutura

A organização atual proporciona:

* separação clara de responsabilidades;
* baixo acoplamento;
* facilidade de manutenção;
* melhor legibilidade;
* reaproveitamento da camada de aplicação;
* possibilidade futura de substituir a interface sem alterar regras de negócio.

A arquitetura permite que a interface de console seja substituída futuramente por:

* interface gráfica;
* aplicação web;
* API REST;
* aplicação mobile.

Sem alterações significativas nas camadas inferiores do sistema.
