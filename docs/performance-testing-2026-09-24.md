# Performance Testing — Transfer Service

**Data:** 2026-09-24  
**Projeto:** RabbitMQ Lab  
**Serviço:** `transfer-service`

## Objetivo

Registrar o ciclo de testes de performance realizado no fluxo síncrono atual do `transfer-service`, antes da introdução do RabbitMQ.

Fluxo testado:

```text
HTTP POST /transfers
        ↓
CreateTransferService
        ↓
Validação de idempotência
        ↓
PostgreSQL
        ↓
Resposta HTTP
```

Objetivos principais:

- medir throughput e latência;
- identificar a faixa de saturação;
- observar o comportamento do HikariCP;
- analisar estados de espera no PostgreSQL;
- verificar impacto de CPU;
- estabelecer um baseline para comparação futura com a arquitetura assíncrona.

---

## Ambiente

Tecnologias envolvidas:

- Java 21
- Spring Boot 4.1.1
- Spring MVC
- Spring Data JPA / Hibernate
- PostgreSQL 16
- HikariCP
- Flyway
- Spring Boot Actuator
- Docker Compose
- k6

Observação importante: Spring Boot, PostgreSQL, k6, IDE, navegador e sistema operacional estavam rodando na mesma máquina local. Por isso, os resultados representam um **baseline local**, e não a capacidade absoluta da aplicação.

---

## Teste com k6

O script de stress foi configurado para criar uma nova `Idempotency-Key` por requisição.

Exemplo:

```javascript
import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 45,
    duration: '30s',

    summaryTrendStats: [
        'avg',
        'min',
        'med',
        'max',
        'p(90)',
        'p(95)',
        'p(99)',
    ],

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<100'],
    },
};

export default function () {
    const url = 'http://localhost:8080/transfers';

    const payload = JSON.stringify({
        senderId: '820ce3fa-7131-4566-8413-2ad8d698b769',
        receiverId: '550e8400-e29b-41d4-a716-446655440001',
        amount: 300,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Idempotency-Key': crypto.randomUUID(),
        },
    };

    const response = http.post(url, payload, params);

    check(response, {
        'status is 201': (r) => r.status === 201,
    });
}
```

---

## Resultados iniciais

### 25 VUs

```text
Throughput: ~999.9 req/s
Média: 24.31 ms
p95: 41.9 ms
p99: 55.2 ms
Erros: 0%
```

### 40 VUs

```text
Throughput: ~1047.1 req/s
Média: 37.52 ms
p95: 75.39 ms
p99: 99.37 ms
Erros: 0%
```

### 45 VUs

```text
Throughput: ~1042.7 req/s
Média: 42.48 ms
p95: 89.12 ms
p99: 117.24 ms
Erros: 0%
```

### 50 VUs

```text
Throughput: ~1037.6 req/s
Média: 47.53 ms
p95: 102.61 ms
p99: 135.73 ms
Erros: 0%
```

### Primeira conclusão

O throughput praticamente estabilizou em torno de:

```text
~1000–1050 req/s
```

enquanto a latência continuou aumentando.

Isso indicou uma região de saturação entre aproximadamente 40 e 50 VUs.

---

## Spring Boot Actuator

Foi adicionado o Actuator para observar métricas internas.

Configuração:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "health,metrics"

  endpoint:
    health:
      show-details: always
```

Endpoints utilizados:

```text
/actuator/metrics
/actuator/metrics/hikaricp.connections.active
/actuator/metrics/hikaricp.connections.pending
/actuator/metrics/hikaricp.connections.max
/actuator/metrics/hikaricp.connections.acquire
/actuator/metrics/hikaricp.connections.usage
/actuator/metrics/hikaricp.connections.timeout
/actuator/metrics/process.cpu.usage
/actuator/metrics/system.cpu.usage
```

---

## HikariCP — pool inicial

O pool padrão estava em:

```text
maximumPoolSize = 10
```

Em um teste isolado com 45 VUs:

```text
Requests: 25,953
Acquire count: 51,906
Usage count: 51,906
```

Isso mostrou aproximadamente:

```text
2 aquisições de conexão por request
```

Média aproximada de aquisição:

```text
~14.5 ms
```

Média aproximada de uso:

```text
~4.6 ms
```

A aquisição de conexão estava consumindo tempo relevante.

---

## Experimento com @Transactional

O `CreateTransferService` foi anotado com:

```java
@Transactional
```

Objetivo:

```text
SELECT idempotency key
+
INSERT transferência
```

passarem a ocorrer dentro da mesma transação.

Após a alteração:

```text
Requests: 26,599
Acquire count: 26,599
```

Resultado:

```text
1 aquisição de conexão por request
```

Com pool 10:

```text
Throughput: ~885 req/s
p95: 117.3 ms
Acquire médio: ~32.3 ms
Usage médio: ~9.9 ms
Timeouts: 0
```

A quantidade de aquisições caiu, mas cada conexão passou a ser mantida por mais tempo durante a transação.

---

## Aumento do pool Hikari

O pool foi alterado de 10 para 20:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
```

Com 45 VUs:

```text
Throughput: ~882 req/s
p95: 103.47 ms
Acquire médio: ~5.5 ms
Usage médio: ~19.0 ms
Timeouts: 0
```

Conclusão:

- o tempo de aquisição caiu bastante;
- o throughput praticamente não aumentou;
- o gargalo foi deslocado para além do pool de conexões.

---

## PostgreSQL

Durante os testes foi monitorado:

```sql
SELECT
    state,
    wait_event_type,
    wait_event,
    COUNT(*)
FROM pg_stat_activity
WHERE datname = 'transfer_db'
GROUP BY state, wait_event_type, wait_event;
```

Foram observadas várias conexões em:

```text
idle in transaction
```

Isso é compatível com o uso do `@Transactional`, mantendo conexões abertas durante o caso de uso.

Também apareceram eventos:

```text
IO → WALSync
LWLock → WALWrite
```

Isso mostra pressão relacionada a escrita e commit no Write-Ahead Log do PostgreSQL.

Não foi identificado um padrão dominante de lock de linha/tabela.

---

## CPU

Durante um teste de 45 VUs foram observados aproximadamente:

```text
process.cpu.usage ≈ 0.53
system.cpu.usage ≈ 0.996
```

Interpretação:

- o processo Java chegou a cerca de 53% de CPU naquele instante;
- a máquina inteira chegou praticamente a 100%.

Isso confirmou que o próprio ambiente local passou a ser parte importante do gargalo.

---

## Baseline final

Configuração final observada:

```text
45 VUs
30 segundos
Hikari pool = 20
@Transactional habilitado
```

Resultado:

```text
Requests: 29,021
Throughput: ~966.6 req/s
Média: 45.02 ms
Mediana: 40.86 ms
p90: 73.13 ms
p95: 85.85 ms
p99: 113.23 ms
Máxima: 198.07 ms
Erros: 0%
```

Thresholds:

```text
p95 < 100 ms ✅
error rate < 1% ✅
```

---

## Conclusões

O ciclo de testes mostrou que a degradação não estava concentrada em um único componente.

Fluxo observado:

```text
Concorrência aumenta
        ↓
contenção no pool Hikari
        ↓
pool aumenta de 10 para 20
        ↓
tempo de acquire cai
        ↓
throughput praticamente não cresce
        ↓
PostgreSQL mostra WALWrite / WALSync
        ↓
CPU do sistema chega próximo de 100%
```

Principais conclusões:

- o pool Hikari com 10 conexões contribuía para espera;
- aumentar para 20 reduziu aquisição de conexão;
- aumentar o pool não aumentou de forma significativa o throughput;
- o gargalo passou a envolver PostgreSQL e recursos da máquina;
- o `@Transactional` foi mantido por consistência do caso de uso;
- os números medidos representam o ambiente local;
- o teto observado ficou aproximadamente entre 900 e 1000 req/s nesse ambiente.

---

## Próxima fase

Com o baseline síncrono registrado, a próxima etapa será introduzir RabbitMQ e um segundo microsserviço.

Próximos componentes:

```text
Spring AMQP
→ Exchange
→ Queue
→ Routing Key
→ TransferCompletedEvent
→ Producer
→ Consumer
→ ACK
→ Retry
→ DLQ
```

Depois da implementação da mensageria, os testes com k6 poderão ser repetidos para comparar o comportamento antes e depois da arquitetura assíncrona.
