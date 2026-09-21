# RabbitMQ Lab

## Sobre o projeto

Este projeto é um laboratório prático de Backend Java criado para estudar como sistemas corporativos são estruturados e implementados no dia a dia.

A proposta é construir uma aplicação distribuída utilizando Java e Spring Boot, aplicando conceitos de arquitetura, persistência, mensageria, testes, observabilidade e performance de forma prática.

O projeto será composto por dois microsserviços independentes, cada um com sua própria responsabilidade dentro do fluxo da aplicação.

## Objetivo

O principal objetivo é aprender construindo.

Durante o desenvolvimento, o foco será entender:

- como separar responsabilidades entre serviços;
- como estruturar uma aplicação utilizando arquitetura hexagonal;
- como integrar aplicações com banco de dados e mensageria;
- como executar e configurar a infraestrutura com Docker;
- como testar o comportamento da aplicação;
- como analisar performance e comportamento sob carga;
- como evoluir uma aplicação simples para uma arquitetura mais próxima de sistemas corporativos reais.

## Arquitetura

A aplicação seguirá princípios de arquitetura hexagonal, mantendo o domínio desacoplado de frameworks e detalhes de infraestrutura.

Estrutura conceitual:

```text
Presentation
    ↓
Application
    ↓
Domain
    ↓
Ports
    ↓
Adapters
    ↓
Infrastructure
```

O domínio não conhece detalhes como JPA, PostgreSQL ou RabbitMQ. Essas tecnologias ficam concentradas nas camadas externas da aplicação.

## Microsserviços

O projeto será composto por dois microsserviços:

### Transfer Service

Responsável pelo fluxo de transferências.

Entre suas responsabilidades estão:

- exposição da API REST;
- validação das regras de negócio;
- persistência das transferências;
- integração com os demais componentes da arquitetura.

### Serviço de processamento de comprovantes

Responsável pelo processamento assíncrono relacionado aos comprovantes das transferências.

Esse serviço será independente do `transfer-service`, permitindo que cada aplicação tenha seu próprio ciclo de execução e evolução.

## Tecnologias

As principais tecnologias utilizadas no projeto são:

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Bean Validation
- Docker
- Docker Compose
- RabbitMQ
- Spring AMQP
- Maven
- JUnit
- Mockito
- k6

## Ambiente de desenvolvimento

Durante o desenvolvimento, a infraestrutura é executada com Docker Compose, enquanto a aplicação Spring Boot pode ser executada localmente utilizando o Maven Wrapper.

Exemplo:

```bash
docker compose up -d
```

E, dentro do microsserviço:

```bash
./mvnw spring-boot:run
```

Essa abordagem facilita o ciclo de desenvolvimento e permite alterações mais rápidas no código sem a necessidade de reconstruir a imagem Docker da aplicação a cada mudança.

## Propósito

Este repositório não tem como objetivo apenas demonstrar código funcionando.

A intenção é estudar, implementar, testar, observar falhas e compreender as decisões técnicas utilizadas na construção de aplicações Backend Java distribuídas.
