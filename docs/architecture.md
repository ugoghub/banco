# Arquitetura

## Visão Geral

O projeto foi desenvolvido utilizando uma arquitetura em camadas com forte separação de responsabilidades, buscando manter o domínio desacoplado de detalhes de infraestrutura, persistência e interface de usuário.

A estrutura foi projetada para permitir que as regras de negócio permaneçam concentradas no domínio, enquanto as demais camadas atuam apenas como coordenadoras das operações do sistema.

A arquitetura segue os princípios de:

* Separation of Concerns (SoC);
* Single Responsibility Principle (SRP);
* Dependency Inversion;
* Encapsulamento de regras de negócio;
* Rich Domain Model.

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
UI
 │
 ▼
Application
 │
 ▼
Services
 │
 ▼
Repositories

Domain
```

Cada camada possui responsabilidades específicas e depende apenas das camadas inferiores.

### UI

Responsável por:

* menus
* entrada de dados
* formatação
* mensagens

Não possui regras de negócio.

### Application

Responsável por:

* coordenar casos de uso
* integrar múltiplos serviços

Não contém regras de domínio.

### Services

Responsável por:

* executar operações do sistema
* aplicar regras de aplicação
* coordenar entidades

### Repositories

Responsável por:

* armazenar dados em memória
* fornecer consultas

### Domain

Responsável por:

* regras de negócio
* validações
* invariantes

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
   ▼
Entidade do Domínio
   │
   ▼
Repository
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
TransactionRepository.save(...)
```

A interface nunca manipula entidades diretamente.

Todo acesso ao domínio ocorre através da camada de aplicação.
