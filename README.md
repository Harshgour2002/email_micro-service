# Email Microservice

Production-ready Java 21 + Spring Boot 3.x email microservice for e-commerce order confirmation notifications.

## Features

- Layered architecture (`controller`, `service`, `service.impl`, `dto`, `config`, `exception`, `template`, `util`)
- Asynchronous email dispatch with retry
- Standardized API response wrapper
- Global exception handling
- Optional API key security (`X-API-KEY`)
- Gmail SMTP configuration via environment variable
- Dockerized deployment

## Tech Stack

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Mail
- Spring Validation
- Lombok
- Maven

## Configuration

Set the following environment variables before running:

```bash
export EMAIL_PASSWORD="your-gmail-app-password"
export API_KEY_ENABLED=true
export EMAIL_SERVICE_API_KEY="your-secret-api-key"
```

> Use a Gmail **App Password** (not your normal Gmail account password).

The application SMTP config is in `src/main/resources/application.yml`:

- `spring.mail.host=smtp.gmail.com`
- `spring.mail.port=587`
- `spring.mail.username=harshgour8909@gmail.com`
- `spring.mail.password=${EMAIL_PASSWORD}`

## Run Locally

```bash
./mvnw spring-boot:run
```

Service starts on `http://localhost:8080`.

## Run with Docker

Build image:

```bash
docker build -t email-microservice:latest .
```

Run container:

```bash
docker run --rm -p 8080:8080 \
  -e EMAIL_PASSWORD="your-gmail-app-password" \
  -e API_KEY_ENABLED=true \
  -e EMAIL_SERVICE_API_KEY="your-secret-api-key" \
  email-microservice:latest
```

## API Endpoints

### Health

`GET /api/v1/email/health`

Response:

```text
Email service is running
```

### Send Order Confirmation

`POST /api/v1/email/order-confirmation`

Headers:

- `Content-Type: application/json`
- `X-API-KEY: your-secret-api-key` (only when API key protection is enabled)

Sample cURL:

```bash
curl -X POST "http://localhost:8080/api/v1/email/order-confirmation" \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: your-secret-api-key" \
  -d '{
    "email": "customer@email.com",
    "customerName": "John Doe",
    "orderId": "ORD12345",
    "orderAmount": 799.00,
    "paymentMethod": "UPI",
    "orderDate": "2026-03-09",
    "items": [
      {"name": "Product 1", "quantity": 1, "price": 499},
      {"name": "Product 2", "quantity": 1, "price": 300}
    ]
  }'
```

Standard API response:

```json
{
  "success": true,
  "message": "Email sent successfully",
  "data": null
}
```

## Integration Flow

1. User completes payment.
2. Order service verifies payment success.
3. Order service calls `POST /api/v1/email/order-confirmation`.
4. Email service sends async order confirmation email.

## Testing with Postman

You can test this microservice end-to-end in Postman using the steps below.

### 1) Start the service

Make sure required env vars are set, then run:

```bash
./mvnw spring-boot:run
```

### 2) Import Postman collection

- Open Postman → **Import**
- Import file: `docs/postman/email-microservice.postman_collection.json`

### 3) Set variables in Postman

Collection variables:

- `baseUrl`: `http://localhost:8080`
- `apiKey`: your key value (only needed if API key auth is enabled)

### 4) Test health endpoint

Request:

- `GET {{baseUrl}}/api/v1/email/health`

Expected response:

- Status: `200 OK`
- Body: `Email service is running`

### 5) Test order confirmation endpoint

Request:

- `POST {{baseUrl}}/api/v1/email/order-confirmation`
- Header: `Content-Type: application/json`
- Header: `X-API-KEY: {{apiKey}}` (only if `API_KEY_ENABLED=true`)
- Body: use sample JSON from the collection

Expected success response:

```json
{
  "success": true,
  "message": "Email sent successfully",
  "data": null
}
```

### 6) Validate failure scenarios in Postman

- Invalid email (e.g. `"email": "abc"`) → expect `400` with validation message.
- Missing required fields (e.g. no `orderId`) → expect `400`.
- Wrong API key (when enabled) → expect `401` with message `Invalid or missing API key`.

### Notes

- Gmail SMTP needs `EMAIL_PASSWORD` to be a **Gmail App Password**.
- Because email sending is async, the API can return success quickly while dispatch runs in background.
- Check service logs for send attempts/retries/failures.
