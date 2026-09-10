# Simple RAG with Spring AI

Ekta minimal RAG (Retrieval-Augmented Generation) app — Spring Boot + Spring AI +
Postgres/pgvector (vector store) + Claude (chat/generation) + OpenAI (embeddings only).

## Kivabe kaj kore

1. `POST /api/rag/ingest` — text dile, seta chunk kore, embedding banaye, Postgres/pgvector-e store kore.
2. `GET /api/rag/ask?question=...` — question-take embedding banaye, vector store theke sobcheye relevant chunk khuje ber kore, tারপর Claude-ke সেই context diye answer generate koray.

## Setup

### 1. Postgres + pgvector extension enable koro
```bash
# Docker diye shobcheye shohoj:
docker run --name rag-postgres -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=ragdb -p 5432:5432 -d pgvector/pgvector:pg16
```

### 2. Environment variables set koro
```bash
export ANTHROPIC_API_KEY=your_anthropic_key
export OPENAI_API_KEY=your_openai_key   # embedding-er jonyo
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

### 3. Run koro
```bash
mvn spring-boot:run
```

## Test kora (curl diye)

```bash
# Ingest kichu HR policy text
curl -X POST http://localhost:8080/api/rag/ingest \
  -H "Content-Type: application/json" \
  -d '{"text": "Employees get 12 casual leaves per year. Sick leave is 10 days annually. Maternity leave is 26 weeks."}'

# Question jiggesh koro
curl "http://localhost:8080/api/rag/ask?question=How%20many%20casual%20leaves%20do%20I%20get%3F"
```

## Note

- Ekhane embedding-er jonyo OpenAI use kora hoyeche, karon Anthropic-er
  nijer kono embeddings API nei. Ei "mix and match" approach ta khub
  common practice — LLM ekta provider theke, embeddings arekta theke.
- Production-e gele: input validation, error handling, chunk size tuning,
  ebong authentication add korte hobe. Eta shudhu core concept dekhanor
  jonyo simplified version.
- Agentic banate chaile — RagService-er `ask()` method-take ekta "tool"
  hisebe register kore, Spring AI-r tool-calling support diye LLM-ke
  nijer theke decide korte deoya jay kobe RAG call korbe, kobe onno kono
  tool.
