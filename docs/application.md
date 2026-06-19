# Camada de Aplicação

## Visão Geral

A camada de aplicação é responsável por expor os casos de uso do sistema para a interface do usuário.

Seu principal objetivo é fornecer uma API simples e estável para que a camada de UI execute operações sem precisar conhecer detalhes internos dos serviços, repositórios ou entidades do domínio.

Esta camada não contém regras de negócio.

As regras de domínio permanecem concentradas nas entidades e Value Objects, enquanto os serviços coordenam os fluxos de aplicação e a interação com os repositórios.

---

## Responsabilidades

A camada de aplicação é responsável por:

* expor casos de uso do sistema;
* coordenar chamadas entre múltiplos serviços;
* servir como ponto único de entrada para a Interface de Usuário;
* reduzir acoplamento entre interface e serviços;
* simplificar o acesso às operações da aplicação.

Não é responsabilidade da camada de aplicação:

* validar regras de negócio;
* manipular persistência;
* implementar cálculos financeiros;
* armazenar estado.
* acessar repositórios diretamente.

---

## Estrutura

A camada é composta por duas classes principais:

```text
ApplicationContext
ApplicationService
```

---

## ApplicationContext

Responsável pela composição manual das dependências da aplicação.

Atua como Composition Root do sistema.

Durante sua inicialização são criados:

```text
Repositórios
    ↓
Serviços
```

A construção ocorre uma única vez durante o início da aplicação.

Exemplo simplificado:

```java
ApplicationContext context =
        new ApplicationContext(clock);
```

Após a criação do contexto, os componentes já estão conectados e prontos para uso.

---

### Controle temporal
O ApplicationContext recebe uma instância de Clock durante sua construção e a compartilha com os componentes que dependem de informações temporais.

Isso permite execução determinística de testes e controle da passagem do tempo sem dependência direta do relógio do sistema.

---

## ApplicationService

Representa a fachada principal da aplicação.

Toda interação da interface com o sistema ocorre através desta classe.

O ApplicationService atua como um Facade sobre os serviços internos da aplicação.

O ApplicationService representa a única dependência da camada de interface em relação às camadas inferiores da aplicação.

Exemplo:

```text
UI
 ↓
ApplicationService
 ↓
Camada de Serviço
```

Isso impede que a interface acesse diretamente:

* Services;
* Repositórios;
* Entidades de negócio.
---

## Organização dos Casos de Uso

Os métodos do ApplicationService estão agrupados de acordo com os principais conceitos do domínio.

### Cliente

Operações relacionadas ao gerenciamento de clientes.

Exemplos:

```
createClient(...)
removeClient(...)
changeName(...)
changeEmail(...)
getClientData(...)
```

---

### Conta

Operações relacionadas à abertura e encerramento de contas.

Exemplos:

```
createAccount(...)
removeAccount(...)
getClientAccountsIdentity(...)
```

---

### Transações

Operações financeiras do sistema.

Exemplos:

```
deposit(...)
withdraw(...)
transfer(...)
getAccountBalance(...)
getAccountTransactions(...)
```

---

## Conversão de referências
Em diversos casos de uso o ApplicationService converte referências públicas utilizadas pela interface em identificadores internos utilizados pelos serviços.

Exemplo:

CPF → ClientId

Essa conversão reduz o acoplamento da interface com a estrutura interna do domínio.

---

## Relação com a Camada de Serviços

A camada de aplicação não substitui os serviços.

Os serviços continuam responsáveis pela execução dos casos de uso e pelas interações com o domínio.

O ApplicationService atua como uma fachada que organiza e expõe essas operações para a interface do usuário.

---

## Coordenação de Serviços

Em alguns casos de uso, o ApplicationService precisa coordenar múltiplos serviços para concluir uma operação.

Exemplo:

```
removeClient(...)
```

Fluxo simplificado:

```text
ApplicationService
      │
      ├── ClientService
      │
      ├── AccountService
      │
      └── ClientService
```

Passos executados:

1. localizar cliente;
2. obter identificador interno do cliente;
3. validar se as contas podem ser removidas;
4. remover contas do cliente;
5. remover cliente.

A coordenação ocorre na camada de aplicação, mantendo cada serviço focado em sua própria responsabilidade.

---

## Fluxo Típico

Exemplo de uma transferência:

```text
Interface de Usuário
 │
 ▼
ApplicationService.transfer(...)
 ↓
TransactionService.transfer(...)
 ↓
AccountService
 ↓
Account.withdraw(...)
Account.deposit(...)
 ↓
TransactionRepository.save(...)
```

A camada de aplicação normalmente delega a execução para os serviços responsáveis, mas também pode coordenar múltiplos serviços quando um caso de uso exige mais de uma operação.

---

## Benefícios da Camada de Aplicação

A existência do ApplicationService proporciona:

* menor acoplamento da Interface de Usuário;
* centralização dos casos de uso;
* simplificação da interface pública do sistema;
* maior flexibilidade para mudanças internas;
* melhor organização arquitetural.

Além disso, permite que novas interfaces (Web, API REST, Desktop, Mobile) reutilizem exatamente os mesmos casos de uso sem alterações nas regras de negócio.