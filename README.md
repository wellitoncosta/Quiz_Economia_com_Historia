# Quiz Service

Servico Spring Boot independente para o modulo de quiz. O sistema principal consome este modulo por HTTP.

## Tecnologias

- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Bean Validation
- Flyway
- MySQL 8

## Como executar

Suba o MySQL:

```bash
docker compose up -d
```

Execute a API:

```bash
mvn spring-boot:run
```

A API fica em:

```text
http://localhost:8081
```

Variaveis aceitas:

```text
SERVER_PORT=8081
DB_URL=jdbc:mysql://localhost:3306/quiz_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=Kingrei124
```

## Fluxo principal

1. Criar quiz: `POST /api/quizzes`
2. Publicar quiz: `PATCH /api/quizzes/{id}/publish`
3. Iniciar tentativa: `POST /api/attempts`
4. Buscar perguntas da tentativa: `GET /api/attempts/{attemptId}/questions`
5. Responder pergunta: `POST /api/attempts/{attemptId}/answers`
6. Finalizar tentativa: `POST /api/attempts/{attemptId}/submit`
7. Consultar resultado: `GET /api/attempts/{attemptId}/result`

`PATCH http://localhost:8081/api/quizzes/1/archive`
## Exemplo de criacao de quiz

```json
{
  "title": "Economia com historia",
  "description": "Quiz de aprendizagem",
  "externalReference": "ECONOMIA_HISTORIA",
  "passPercentage": 70,
  "timeLimitMinutes": 30,
  "questions": [
    {
      "statement": "O que e escassez em economia?",
      "explanation": "Escassez ocorre quando recursos sao limitados diante de necessidades ilimitadas.",
      "position": 1,
      "points": 10,
      "active": true,
      "options": [
        {"text": "Recursos limitados para necessidades ilimitadas", "correct": true, "position": 1},
        {"text": "Excesso permanente de recursos", "correct": false, "position": 2},
        {"text": "Ausencia total de escolhas", "correct": false, "position": 3}
      ]
    }
  ]
}
```

## Exemplo de integracao do sistema principal

Iniciar tentativa usando referencia externa:

```json
POST /api/attempts
{
  "externalReference": "ECONOMIA_HISTORIA",
  "participantId": "usuario-123",
  "participantName": "Maria"
}
```

Responder:

```json
POST /api/attempts/1/answers
{
  "questionId": 1,
  "selectedOptionId": 1
}
```

## Testes

```bash
mvn test
```


Passo 1 — Cria tentativa nova (sem limite de tempo desta vez):
Primeiro, cria um quiz novo sem timeLimitMinutes, ou usa o quiz existente mas sem limite. Se o quiz 1 já está publicado, podes ir directo:
POST http://localhost:8081/api/attempts
json{
  "quizId": 1,
  "participantId": "welliton-001",
  "participantName": "Welliton"
}
→ Anota o "id" da resposta. Vamos chamar de N.
Passo 2 — Vê as perguntas:
GET http://localhost:8081/api/attempts/N/questions
→ Anota o id de cada pergunta e o id de cada opção.
Passo 3 — Responde (uma vez por pergunta):
POST http://localhost:8081/api/attempts/N/answers
json{
  "questionId": <id da pergunta>,
  "selectedOptionId": <id da opção escolhida>
}
Passo 4 — Submete:
POST http://localhost:8081/api/attempts/N/submit
Sem body.
Passo 5 — Resultado (método GET, não POST):
GET http://localhost:8081/api/attempts/N/result