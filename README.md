# SearchTool

A simple web application that enables searching through the Google Custom Search API. The user enters a keyword, the backend sends a request to Google Custom Search API, and the frontend displays only organic results from the first page. All results can be downloaded with a single click as a structured JSON file.

## Technologies

**Backend:**
- Java 17
- Spring Boot 3.5.7
- Maven
- Lombok
- Jackson

**Frontend:**
- HTML5
- Bootstrap 5.3.8
- Vanilla JavaScript (ES6+)

**Testing:**
- JUnit 5
- Mockito

## Prerequisites

- Java 17+
- Maven 3.6+
- Google Custom Search API key and CX

## Configuration

Create `src/main/resources/application.yaml`:

```yaml
google:
  api-key: YOUR_API_KEY
  cx: YOUR_CX
  url-prefix: https://www.googleapis.com/customsearch/v1?
```

## Running the Application

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Or run the JAR
java -jar target/SearchToolServer-1.0-SNAPSHOT.jar
```

The application will be available at `http://localhost:8080`

## Running Tests

```bash
mvn test
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/search?query={query}` | Returns search results as JSON |
| GET | `/search/download?query={query}` | Downloads search results as JSON file |

### Example Response

```json
[
  {
    "title": "Example Title",
    "link": "https://example.com",
    "snippet": "Description of the search result..."
  }
]
```

## Project Structure

```
src/
├── main/
│   ├── java/cz/searchtool/
│   │   ├── SearchToolMain.java
│   │   ├── configuration/
│   │   │   └── ApplicationConfig.java
│   │   ├── controller/
│   │   │   ├── HomeController.java
│   │   │   ├── SearchController.java
│   │   │   └── advice/
│   │   │       ├── HttpClientErrorExceptionAdvice.java
│   │   │       └── IOExceptionAdvice.java
│   │   ├── dto/
│   │   │   └── Item.java
│   │   └── service/
│   │       ├── SearchService.java
│   │       └── SearchServiceImpl.java
│   └── resources/
│       ├── application.yaml
│       └── public/
│           ├── index.html
│           └── script/script.js
└── test/
    └── java/cz/searchtool/service/
        └── SearchServiceTest.java
```

## Docker

```bash
# Build image
docker build -t searchtool .

# Run container
docker run -p 8080:8080 searchtool
```
