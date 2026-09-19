# Interview Preparation

An interview preparation application that helps users practice technical interview questions using AI-powered answer evaluation.

The application presents interview questions as cards and uses a weighted review algorithm to select questions based on the user's previous performance, the card's meeting probability, and how long ago the card was last answered.

## Features

- User registration and login
- JWT-based authentication
- Interview questions organized into desks and topics
- Start a review session with selected desks
- Weighted random card selection
- AI-powered answer evaluation
- AI-generated feedback and score
- Reveal the correct answer
- Answer history
- Daily, weekly, and all-time leaderboard
- Statistics for cards and desks
- Admin settings for configuring the review algorithm
- Admin management of cards and application settings

## How Review Works

When a user starts a review session, they can select which desks they want to practice.

The application then selects a card using a weighted selection algorithm. The weight of a card is affected by:

- **Difficulty** — cards that the user previously answered poorly receive a higher selection weight.
- **Meet chance** — cards with a higher `meet_chance` receive a higher selection weight.
- **Recency** — cards that have not been answered for a longer period receive a higher selection weight.

After answering a question, the answer is sent to the AI service. The AI returns:

- A score
- Feedback
- The correct answer

The result is saved in the user's answer history and is used for future card selection.

## Tech Stack

### Backend

- Kotlin
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring Data Redis
- Liquibase
- PostgreSQL
- JWT
- OpenAI API
- Springdoc OpenAPI
- Gradle

### Infrastructure

- Docker
- Docker Compose
- PostgreSQL 17
- Redis 7

### Frontend

The frontend is served as a Docker container and is available on port `5173`.

## Project Structure

```text
.
├── backend/
│   ├── src/
│   ├── Dockerfile
│   └── build.gradle.kts
├── frontend/
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

The backend is organized by application domain:

```text
backend/src/main/kotlin/com/backend/
├── answer/
├── card/
├── desk/
├── leaderboard/
├── review/
├── security/
├── settings/
└── user/
```

## Running the Application

### Requirements

- Docker
- Docker Compose

No local PostgreSQL, Redis, or Java installation is required when running the complete application with Docker Compose.

### Environment Variables

Create a `.env` file in the project root:

```env
POSTGRES_DB=interview_preparation
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password

DB_HOST=db
DB_PORT=5432
DB_NAME=interview_preparation
DB_USERNAME=postgres
DB_PASSWORD=your_password

OPENAI_BASE_URL=https://api.openai.com
OPENAI_API_KEY=your_api_key
OPENAI_MODEL=your_model

JWT_SECRET=your_jwt_secret

APP_FRONTEND_URL=http://localhost:5173
```

Adjust the OpenAI configuration according to the AI provider you are using.

### Start the Application

Run:

```bash
docker compose up --build
```

The services will be started in the following order:

```text
PostgreSQL
    ↓
Redis
    ↓
Backend
    ↓
Frontend
```

PostgreSQL and Redis have health checks, and the backend waits for both services to become healthy before starting.

### Access the Application

Frontend:

```text
http://localhost:5173
```

Backend:

```text
http://localhost:8080
```

The backend API is available under:

```text
http://localhost:8080/api
```

## Database

The application uses PostgreSQL for persistent data and Redis for temporary answer-processing data.

The main entities are:

- Users
- Topics
- Desks
- Cards
- Answers
- Application settings
- Recency multipliers
- Meet chance multipliers
- Difficulty multipliers
- Score colors

Database migrations are managed with Liquibase.

PostgreSQL data is persisted using a Docker volume, so restarting the containers does not remove the database.

## Review Algorithm Configuration

Administrators can configure the parameters used by the card selection algorithm.

The available settings include:

### Difficulty Multipliers

Controls how the user's previous answer score affects the probability of selecting a card.

```text
last answer score → multiplier
```

### Recency Multipliers

Controls how the time since the user's previous answer affects the probability of selecting a card.

```text
time since last answer → multiplier
```

### Meet Chance Multipliers

Controls how the card's `meet_chance` affects its probability of being selected.

```text
meet chance → multiplier
```

### Score Colors

Administrators can also configure colors associated with answer scores.

## Development

The backend uses Gradle and Kotlin.

To run the backend locally:

```bash
cd backend
./gradlew bootRun
```

To run tests:

```bash
./gradlew test
```

To check and format Kotlin code:

```bash
./gradlew spotlessCheck
./gradlew spotlessApply
```

## API Documentation

The backend uses OpenAPI/Swagger for API documentation.

When the backend is running, the generated API documentation can be accessed through the Springdoc/Swagger UI endpoint configured by the application.

## Docker Services

The application consists of four Docker Compose services:

| Service | Description | Port |
|---|---|---:|
| `db` | PostgreSQL database | `5432` |
| `redis` | Redis | `6379` |
| `backend` | Spring Boot API | `8080` |
| `frontend` | Web application | `5173` |

PostgreSQL and Redis use persistent Docker volumes:

```text
postgres_data_interview_preparation
redis_data_interview_preparation
```

## License

This project is for educational and interview preparation purposes.