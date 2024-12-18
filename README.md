
#  API for a currency calculator

This repository contains a Spring Boot application written in Kotlin, integrated with a PostgreSQL database. Docker Compose is used to orchestrate the application and database containers.
API for a currency calculator with configurable conversion fees.

## Prerequisites

- **Docker**: Ensure Docker is installed and running.
- **Maven**: Ensure Maven is installed to build the application.

## Project Structure

```
/currency-calculator
├── Dockerfile
├── docker-compose.yml
├── README.md
├── pom.xml
├── src/
└── target/
```

## How to Run the Application

### 0. Clone the Project
To get started with the project, first, clone the repository from GitHub:

```bash
git clone https://github.com/ibelecka/currency-calculator.git
```

After cloning, navigate into the project root directory :

```bash
cd your-repository
```

### 1. Build the Application

First, package the application using Maven:

```bash
mvn clean package
```


This will generate a JAR file in the `target` directory.

### 2. Build and Start Containers

Use Docker Compose to build the application image and start all containers (can take up to 10 minutes):

```bash
docker-compose up --build
```

This will:
- Build the `spring-boot-app` container.
- Start the PostgreSQL database container.
- Link the two containers together.

### 3. Access the Application

Once the containers are up, the Spring Boot application will be available at:

```
http://localhost:8080
```

### 4. Stop the Containers

To stop the containers and clean up resources:

```bash
docker-compose down
```

## Additional Notes

- **Database Credentials**:
    - The application connects to the PostgreSQL database using the following default credentials (defined in `docker-compose.yml`):
        - **Database Name**: `currency-calculator`
        - **Username**: `currency-admin`
        - **Password**: `currency-admin`
    - These values are configurable in the `docker-compose.yml` file under the `environment` section.

- **Data Persistence**:
    - PostgreSQL data is stored in a Docker volume named `postgres-data` to ensure persistence across container restarts.

    
# API Endpoints

### 1. **Get All Currency Pairs**
- **Endpoint**: `GET /admin/currency-pair`
- **Description**: Retrieves a list of all currency pairs with customised fee.
- **Response**:
    - `200 OK`: List of currency pairs.
    - `500 Internal Server Error`: If an error occurs while fetching the data.

### 2. **Create Currency Pair**
- **Endpoint**: `POST /admin/currency-pair`
- **Description**: Creates a new currency pair with custom fee.
- **Request Body**: A `CurrencyPairRequest` object containing `fromCurrency`, `toCurrency`, `fee`. `rate` will be retrieved from ECB.
- **Example**: 
```bash
  {
    "currencyFrom": "CNY",
    "currencyTo" : "EUR",
    "fee" : 0.45
  }
```   
- **Response**:
    - `201 Created`: If the currency pair is successfully created.
    - `400 Bad Request`: If no rate is found for the currency pair from ECB.
    - `409 Conflict`: If the currency pair already exists.
    - `500 Internal Server Error`: If an error occurs during the creation process.

### 3. **Update Fee for a Currency Pair**
- **Endpoint**: `PUT /admin/currency-pair`
- **Description**: Updates the fee for an existing currency pair.
- **Request Body**: A `CurrencyPairRequest` object containing `fromCurrency`, `toCurrency`, and the new `fee`.
- **Example**:
```bash
  {
    "currencyFrom": "CNY",
    "currencyTo" : "EUR",
    "fee" : 0.45
  }
```  
- **Response**:
    - `200 OK`: If the currency pair is successfully updated.
    - `404 Not Found`: If no currency pair with the given parameters is found.
    - `500 Internal Server Error`: If an error occurs during the update.

### 4. **Delete Currency Pair by ID**
- **Endpoint**: `DELETE /admin/currency-pair/{id}`
- **Description**: Deletes a currency pair by its ID.
- **Response**:
    - `204 No Content`: If the currency pair is successfully deleted.
    - `404 Not Found`: If the currency pair with the given ID is not found.

### 5. **Refresh Currency Pairs**
- **Endpoint**: `GET /admin/currency-pair/refresh`
- **Description**: Fetches the latest exchange rates from the European Central Bank (ECB) and updates the currency pairs.
- **Response**:
    - `200 OK`: If the data is successfully refreshed.
    - `500 Internal Server Error`: If an error occurs during the refresh process.
      """

### 6. **Convert Currency**
- **Endpoint**: `GET /public/convert`
- **Description**: Converts an amount from one currency to another based on the available exchange rate.
- **Request Parameters**:
    - `currencyFrom`: The source currency.
    - `currencyTo`: The target currency.
    - `amount`: The amount to convert.
- **Response**:
    - `200 OK`: If the currency pair is found and the conversion is successful.
    - `404 Not Found`: If no currency pair with the specified parameters is found.



