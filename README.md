# Multi Tenant Application
This is a Spring Boot-based multi-tenant application that demonstrates how to implement authentication and querying functionalities with tenant-specific data using JWT tokens. The application uses MyBatis for database access and supports multiple tenants with separate databases.

## Authentication

### Credentials

#### Test Tenant 1 (Testing Purposes Only)

- **Username:** `user1@tenant_1`
- **Password:** `Cd7aRow2kF6WcDFPhAJa`

#### Test Tenant 2 (Testing Purposes Only)

- **Username:** `user2@tenant_2`
- **Password:** `XSj1LvNW8IvHDv8W65qO`

### Authentication Endpoint

- **Endpoint:** `POST /api/v1/auth`

- **Request Body:**

  ```json
  {
      "username": "user1@tenant_1",
      "password": "Cd7aRow2kF6WcDFPhAJa"
  }
  ```

- **Response:**

  ```json
  {
      "username": "user1@tenant_1",
      "token": "<token>"
  }
  ```

## Querying Users

### Users Endpoint

- **Endpoint:** `GET /api/v1/user`

- **Headers:**

    - Authorization: `Bearer <token>`

- **Response:**

  ```json
  [
      {
          "id": 1,
          "username": "user2@tenant_2",
          "name": "User Two",
          "email": "user2@tenant.com"
      }
  ]
  ```

  > **Note:** The response will depend on which tenant's JWT token is used. For example, if a user from Test Tenant 1 authenticates and queries users, they will receive user data from the Test Tenant 1 database. Similarly, Test Tenant 2 users will receive data specific to the Test Tenant 2 database.

## Technologies Used

- **Java**: Version 17
- **Spring Boot**: 3.4.1
- **MyBatis**: 3.0.4
- **JWT**: JJWT API, JJWT Implementation, and JJWT Jackson (Version 0.12.6)
- **MySQL**: MySQL Connector 8.0.33
- **Lombok**
- **Spring Security**
- **Spring Web**

## Notes

- These credentials are for **testing purposes only** and should not be used in a production environment.
- Always include the `Authorization` header with the Bearer token in every request except for the `POST /api/v1/auth` endpoint.
- Tokens are tenant-specific and must be used accordingly.

