# WebAppDev-Ca

## To deploy the web application locally, follow these steps:

### 1. Clone the repository:
   ```
   git clone https://github.com/Fubuki233/WebAppDev-Ca.git
   ```
### 2. deploy the sql using the aori_depolyment.sql file in the aori directory to your local MySQL server.
### 3. Ensure you have Java 17 or higher, Maven, and npm installed on your machine.
### 4. Navigate to the backend directory and start the backend server:
   ```
   cd WebAppDev-Ca/aori
   mvn spring-boot:run
   ```
### 5. Navigate to the frontend directory and install the dependencies:
   ```
   cd WebAppDev-Ca/aori-app
   npm install
   ```
### 6. Start the development server:
   ```
   npm run dev
   ```