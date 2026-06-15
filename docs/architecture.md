# Arquitetura

## Visão Geral

O projeto foi desenvolvido utilizando uma arquitetura em camadas com forte separação de responsabilidades, buscando manter o domínio desacoplado de detalhes de infraestrutura, persistência e interface de usuário.

A estrutura foi projetada para permitir que as regras de negócio permaneçam concentradas no domínio, enquanto as demais camadas atuam apenas como coordenadoras das operações do sistema.

A arquitetura segue os princípios de:

* Separação de Responsabilidades (SoC);
* Princípio da Responsabilidade Única (SRP);
* Encapsulamento de regras de negócio;
* Modelo de Domínio Rico.

---

## Objetivos Arquiteturais

A arquitetura foi construída com os seguintes objetivos:

* manter regras de negócio isoladas da interface;
* evitar dependência direta entre domínio e persistência;
* facilitar manutenção e evolução do sistema;
* permitir substituição futura da camada de armazenamento;
* centralizar a orquestração dos casos de uso;
* tornar o domínio independente de frameworks.

---

## Estrutura Geral

A aplicação está organizada nas seguintes camadas:

```text
Interface de Usuário (UI)
    │
    ▼
Camada de Aplicação
(ApplicationService)
    │
    ▼
Camada de Serviço
 ├── ClientService
 ├── AccountService
 └── TransactionService
    │
    ├── Domínio
    └── Repositórios
```

Cada camada possui responsabilidades específicas e acessa apenas os componentes necessários para cumprir sua função, respeitando a direção definida das dependências.

### Interface de Usuário

Responsável por:

* menus
* entrada de dados
* formatação
* mensagens

Não possui regras de negócio.

### Camada de Aplicação

Responsável por:

* expor os casos de uso da aplicação;
* coordenar serviços internos;
* servir como fachada para a camada de UI.

Não contém regras de domínio.

### Camada de Serviço

Responsável por:

* executar operações do sistema;
* coordenar entidades de domínio;
* interagir com os repositórios;
* garantir a consistência das operações de negócio.
* orquestrar regras que envolvem múltiplas entidades;

### Repositórios

Responsável por:

* armazenar dados em memória
* fornecer consultas

### Domínio

Responsável por:

* regras de negócio;
* validações de domínio;
* invariantes;
* entidades;
* value objects;
* comportamento financeiro;
* políticas de negócio.

O domínio não possui dependência de:

* UI;
* Application;
* Services;
* Repositories;
* frameworks externos.

Todas as regras críticas do sistema estão concentradas nesta camada.
As entidades do domínio são responsáveis por proteger seus próprios invariantes e encapsular as regras de negócio relacionadas ao seu estado.

---

## Dependências Entre Camadas

As dependências do sistema seguem uma direção única, evitando acoplamento desnecessário entre as partes da aplicação.

```text
UI
 ↓
Application
 ↓
Services
 ↓
Repositories
```

O domínio é utilizado por todas as camadas, mas não possui conhecimento sobre nenhuma delas.

---

### Regras de Dependência

#### Interface de Usuário

A camada de interface pode acessar:

* Application

A camada de interface não deve acessar:

* Services diretamente;
* Repositories;
* detalhes internos das entidades.

---

#### Camada de Aplicação

A camada de aplicação pode acessar:

* Services;
* DTOs;
* tipos expostos pelos serviços.

A camada de aplicação atua como fachada do sistema, centralizando os casos de uso disponíveis para a interface.

---

#### Camada de Serviço

A camada de serviços pode acessar:

* Domain;
* Repositories.

Os serviços são responsáveis por coordenar operações que envolvem múltiplas entidades ou múltiplos repositórios.

---

#### Repositórios

A camada de repositórios é responsável apenas pelo armazenamento e recuperação de dados.

Não deve conter:

* regras de negócio;
* validações de domínio;
* lógica de aplicação.

---

#### Domínio

O domínio representa o núcleo do sistema e concentra todas as regras de negócio. Nenhuma outra camada é conhecida por ele.

---

### Fluxo de Dependência

A direção das dependências pode ser representada da seguinte forma:

```text
Interface de Usuário (UI)
    │
    ▼
Camada de Aplicação
(ApplicationService)
    │
    ▼
Camada de Serviço
    │
    ├── Domínio
    └── Repositórios
```

O domínio permanece independente, sendo utilizado pelas demais camadas sem conhecê-las.

---

### Benefícios da Estrutura

Esta organização proporciona:

* baixo acoplamento;
* alta coesão;
* facilidade de manutenção;
* maior testabilidade;
* isolamento das regras de negócio;
* possibilidade de substituir a interface sem impactar o domínio;
* possibilidade de substituir a estratégia de persistência sem alterar as regras do sistema.


---

## Fluxo Geral de Execução

Uma operação típica segue o fluxo abaixo:

```text
Usuário
   │
   ▼
Controller (UI)
   │
   ▼
ApplicationService
   │
   ▼
Service
   │
   ├── Domínio
   └── Repositórios
```

Exemplo:

```text
Usuário realiza depósito

AccountTransactionController
        │
        ▼
ApplicationService.deposit(...)
        │
        ▼
TransactionService.deposit(...)
        │
        ▼
Account.deposit(...)
        │
        ▼
Persistência da transação
TransactionRepository.save(...)
```

A interface nunca manipula entidades diretamente.

Todo acesso ao domínio ocorre através da camada de aplicação.

---

## Papel do ApplicationService

O ApplicationService atua como fachada da aplicação.

Seu objetivo é fornecer uma interface única para os casos de uso consumidos pela camada de UI.

Ele não contém regras de negócio nem lógica de persistência, sendo responsável apenas por coordenar os serviços internos e expor operações de alto nível para a interface.

## Application Context

O projeto utiliza um **Composition Root manual**, implementado através da classe `ApplicationContext`.

Essa classe é responsável por criar e conectar todos os componentes necessários para o funcionamento da aplicação.

A inicialização ocorre sem o uso de frameworks de injeção de dependência, mantendo o projeto totalmente independente de bibliotecas externas.

---

### Objetivo

O principal objetivo do `ApplicationContext` é centralizar a construção das dependências do sistema.

Em vez de permitir que cada classe crie suas próprias dependências, toda a composição ocorre em um único local.

Isso reduz acoplamento e facilita futuras alterações na infraestrutura.

---

### Estrutura

Durante a inicialização são criados:

#### Repositórios

```text
ClientRepository
AccountRepository
TransactionRepository
```

Responsáveis pelo armazenamento dos dados em memória.

---

#### Camada de Serviço

```text
ClientService
AccountService
TransactionService
```

Responsáveis pela execução das regras de aplicação e coordenação das operações do sistema.

---

#### Camada de Aplicação

```text
ApplicationService
```

Responsável por fornecer uma interface única para os casos de uso da aplicação.

---

### Fluxo de Construção

A composição ocorre na seguinte ordem:

```text
Repositories
      │
      ▼
Services
      │
      ▼
ApplicationService
```

Representação simplificada:

```text
ClientRepository
        │
        ▼
 ClientService

AccountRepository
        │
        ▼
 AccountService

TransactionRepository
        │
        ▼
TransactionService
```

Após a criação dos serviços, eles são injetados no `ApplicationService`.

---

### Uso do Clock

O sistema recebe uma instância de `Clock` durante a construção do contexto:

```java
ApplicationContext context =
        new ApplicationContext(clock);
```

Essa abordagem permite:

* controle determinístico do tempo;
* testes automatizados mais previsíveis;
* simulação de passagem de tempo;
* desacoplamento de `LocalDateTime.now()`.

O mesmo `Clock` é compartilhado entre os componentes que dependem de operações temporais.

Atualmente ele é utilizado principalmente por:

* `SavingsAccount`;
* `Transaction`;
* `TransactionService`.

---

### Benefícios

A utilização de um Composition Root manual proporciona:

* centralização das dependências;
* baixo acoplamento entre componentes;
* maior clareza arquitetural;
* facilidade de testes;
* independência de frameworks;
* possibilidade futura de migrar para um container de DI sem alterações significativas nas regras de negócio.

---

### Decisão Arquitetural

Foi escolhido um mecanismo manual de composição porque:

* o projeto possui escopo educacional;
* o número de dependências ainda é pequeno;
* evita complexidade desnecessária;
* permite compreender explicitamente o fluxo de criação dos objetos.

Essa abordagem segue os mesmos princípios utilizados por frameworks como Spring, porém de forma simplificada e transparente.

---

## Estratégia de Persistência

O sistema utiliza uma camada de repositórios em memória baseada em coleções Java.

Atualmente não existe integração com banco de dados externo, permitindo que o foco do projeto permaneça na modelagem do domínio e na arquitetura da aplicação.

### Repositórios Existentes

```text
ClientRepository
AccountRepository
TransactionRepository
```

Cada repositório possui responsabilidade exclusiva sobre um conjunto específico de dados do domínio.

---

### Características

A implementação atual oferece:

* armazenamento em memória;
* consultas por índices;
* isolamento da lógica de persistência;
* independência de banco de dados.

Os serviços não possuem conhecimento sobre a estrutura interna utilizada pelos repositórios.

---

### Benefícios

Essa abordagem proporciona:

* simplicidade;
* facilidade de testes;
* foco na modelagem de domínio;
* possibilidade futura de substituição por banco de dados relacional ou NoSQL sem impacto significativo nas regras de negócio.

---

## Estratégia de DTOs

O sistema utiliza Data Transfer Objects (DTOs) para comunicação entre as camadas.

Os DTOs são implementados utilizando records imutáveis do Java.

Os DTOs são utilizados principalmente como contratos de saída da camada de aplicação, evitando que entidades do domínio sejam expostas diretamente para a UI.

### DTOs Existentes

```text
ClientData
StatementData
```

---

### Objetivos

Os DTOs possuem as seguintes responsabilidades:

* transportar dados entre camadas;
* desacoplar interface e domínio;
* evitar exposição de entidades;
* estabilizar contratos da aplicação.

---

### Benefícios

Essa estratégia permite:

* maior encapsulamento;
* menor acoplamento entre camadas;
* facilidade de evolução do domínio sem impactar consumidores.

---

## Estratégia de Tratamento de Exceções

Todas as exceções do sistema derivam de uma raiz comum:

```text
DomainException
```

A hierarquia foi organizada para separar erros de validação, regras de negócio e consultas inexistentes.

### Estrutura

```text
DomainException
│
├── ValidationException
│
├── BusinessRuleException
│
└── NotFoundException
```

---

### Objetivos

Essa organização permite:

* categorizar falhas do sistema;
* simplificar tratamento na interface;
* centralizar erros de domínio;
* tornar a intenção das exceções explícita.

---

### Benefícios

A interface pode capturar categorias inteiras de erro:


```
catch (ValidationException e)
```

sem depender de implementações específicas.

---

## Testabilidade

A arquitetura foi construída com forte preocupação em testabilidade.

Diversas decisões arquiteturais foram tomadas visando facilitar testes automatizados.

### Clock Injetável

O uso de `Clock` permite controlar o tempo durante os testes.

Isso possibilita validar cenários como:

* cálculo de rendimentos pendentes da poupança;
* passagem de meses;
* geração de transações dependentes de data e hora.

---

### Dependências Explícitas

Todos os componentes recebem suas dependências via construtor.

Exemplo:

```
TransactionService(
    accountService,
    transactionRepository,
    clock
)
```

Isso elimina dependências ocultas e facilita a criação de cenários de teste.

---

### Repositórios em Memória

A utilização de repositórios baseados em coleções permite:

* testes rápidos;
* ausência de infraestrutura externa;
* isolamento das regras de negócio.

---

## Possíveis Evoluções Arquiteturais

A arquitetura atual foi projetada para permitir crescimento incremental.

Algumas evoluções naturais incluem:

### Persistência Real

Substituição dos repositórios em memória por:

* PostgreSQL;
* MySQL;
* MongoDB.

Sem alterações significativas nas entidades e regras de domínio.

---

### Injeção de Dependência

Migração do `ApplicationContext` manual para um container de DI:

* Spring Framework;
* CDI;
* Guice.

---

### API REST

Criação de uma camada externa:

```text
Controller REST
        │
        ▼
ApplicationService
```

Mantendo o domínio inalterado.

---

### Autenticação

Possível inclusão futura de:

* login;
* senha;
* perfis de acesso;
* autorização.

Sem impacto relevante na modelagem financeira já existente.

---

## Considerações Finais

A arquitetura foi construída priorizando:

* simplicidade;
* clareza;
* separação de responsabilidades;
* independência tecnológica;
* foco em modelagem de domínio.

Embora o projeto utilize armazenamento em memória e composição manual de dependências, sua estrutura segue princípios aplicados em sistemas corporativos reais, permitindo evolução gradual sem necessidade de reestruturações significativas.
