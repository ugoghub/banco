# UI Layer

## Visão Geral

A camada de interface é responsável pela interação entre o usuário e o sistema.

Seu objetivo é receber entradas, apresentar informações e encaminhar solicitações para a camada de aplicação.

Toda regra de negócio permanece fora da interface.

A UI atua apenas como um mecanismo de comunicação entre o usuário e os casos de uso disponíveis.

---

## Responsabilidades

A camada de interface é responsável por:

* exibir menus;
* coletar entradas do usuário;
* validar formatos básicos de entrada;
* exibir mensagens;
* apresentar resultados;
* encaminhar operações para a camada de aplicação.

Não é responsabilidade da UI:

* executar regras de negócio;
* acessar repositórios;
* manipular entidades diretamente;
* realizar cálculos financeiros.

---

## Estrutura Geral

A camada é composta pelos seguintes elementos:

```text
App
Controller(s)
InputReader
MenuRenderer
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
ClientController

AccountController

TransactionController
```

---

### Responsabilidades

* coletar dados do usuário;
* converter entradas em Value Objects;
* chamar ApplicationService;
* apresentar resultados;
* exibir mensagens de erro.

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

## MenuRenderer

Responsável exclusivamente pela apresentação visual dos menus.

---

### Objetivos

Centralizar a exibição das opções disponíveis.

Exemplo:

```text
1 - Criar cliente
2 - Abrir conta
3 - Depositar
4 - Transferir
0 - Sair
```

---

### Benefícios

A separação entre exibição e controle permite:

* menus mais organizados;
* menor duplicação;
* facilidade para alterar a aparência da interface.

---

## Fluxo de Execução

Uma operação típica segue o fluxo abaixo:

```text
Usuário
   │
   ▼
Controller
   │
   ▼
InputReader
   │
   ▼
ApplicationService
   │
   ▼
Service Layer
   │
   ▼
Domain
```

Após a execução:

```text
Resultado
   │
   ▼
Controller
   │
   ▼
MenuRenderer
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

A UI não tenta corrigir erros de domínio.

Ela apenas apresenta a mensagem adequada ao usuário.

---

## Conversão para Value Objects

A interface nunca envia dados primitivos diretamente para os serviços quando existe um Value Object correspondente.

Exemplo:

```text
String
   │
   ▼
Cpf

String
   │
   ▼
Email

String
   │
   ▼
PersonName
```

Isso garante que todas as validações ocorram antes da execução dos casos de uso.

---

## Dependências

A camada de UI depende apenas da camada de aplicação.

```text
UI
 │
 ▼
Application Layer
```

Ela não possui acesso direto a:

* Services;
* Repositories;
* Entidades;
* Estruturas internas do domínio.

---

## Benefícios da Estrutura

A organização atual proporciona:

* separação clara de responsabilidades;
* baixo acoplamento;
* facilidade de manutenção;
* melhor legibilidade;
* reaproveitamento da camada Application;
* possibilidade futura de substituir a interface sem alterar regras de negócio.

A arquitetura permite que a interface de console seja substituída futuramente por:

* interface gráfica;
* aplicação web;
* API REST;
* aplicação mobile.

Sem alterações significativas nas camadas inferiores do sistema.
