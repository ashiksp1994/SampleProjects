# Physiotherapy Clinic API and Frontend

This repository contains the source code for the Physiotherapy Clinic project. It includes the backend services, frontend, and Google Cloud API Gateway setup.

## Project Structure

- **backend/**: Contains the backend services (`appointment-service`, `token-service`, `user-service`).
- **frontend/**: Contains the frontend code for the Physiotherapy Clinic UI.
- **cloudbuild.yaml**: Configuration file for building and deploying the services on Google Cloud.
- **Jenkinsfile**: Pipeline configuration for automating the CI/CD process using Jenkins.
- **openapi.yaml**: API specification for Google API Gateway.

## Prerequisites

- **Google Cloud SDK** installed and configured on your local machine.
- **Jenkins** with appropriate credentials setup.
- A Google Cloud project with **API Gateway**, **GKE**, **Cloud Run**, and **Cloud Build** enabled.
- **Docker** and **Kubernetes** should be configured for local testing and deployment.

## Google Cloud Configuration

### 1. Google API Gateway

The backend services are exposed through **Google API Gateway** using the OpenAPI specification. This allows secure and efficient communication between the frontend hosted on **Cloud Run** and backend services running on **GKE**.

- **Gateway Host**: `https://physiotherapy-api-europe-central2.gateway.dev`

The backend IPs in the **OpenAPI specification** are dynamically updated via the Jenkins pipeline during the deployment process.

### 2. Google Kubernetes Engine (GKE)

The backend services are deployed on **GKE** using Kubernetes manifests. The Jenkins pipeline retrieves the dynamically assigned external IPs for the services and updates the OpenAPI spec accordingly.

- **Services**:
  - `user-service`
  - `appointment-service`
  - `token-service`

### 3. Cloud Build

**Cloud Build** is used to build Docker images and deploy the services to **GKE** and **Cloud Run**.

## How to Run

### 1. Backend Services

- **Build and Run Locally**:
Navigate to the service directory and build using Gradle:

- `cd backend/<service-name>`
- `./gradlew clean build`

### Deploy on Google Cloud:
The Jenkins pipeline or Cloud Build will automatically handle the deployment of backend services to GKE.

---

## 2. Frontend

### Build and Deploy on Cloud Run:
Deploy the frontend to Cloud Run using the `gcloud` command:

- `gcloud run deploy frontend --source frontend/ --platform managed --region europe-central2 --allow-unauthenticated`

### Access the Frontend:
The frontend is deployed on Cloud Run and interacts with the backend services via the API Gateway.

---

## 3. Running the Jenkins Pipeline

The `Jenkinsfile` provided in the repository automates the following:

- Checking out code from GitHub.
- Building backend services using Gradle.
- Deploying the services to GKE using Kubernetes.
- Fetching external IPs for the services and updating the OpenAPI specification.
- Deploying API Gateway configurations on Google Cloud.

### Run the Jenkins pipeline to automate the build and deployment process:
1. Log in to Jenkins.
2. Trigger the pipeline.

---

## 4. Registration Page (Frontend)

The `RegistrationPage.js` is a React component that handles user registration. It submits the registration form to the backend via the Google API Gateway.

---

## Pipeline Structure

- **Verify Tools:** Checks the presence of `kubectl` and `gcloud`.
- **Checkout Code:** Pulls the latest code for both frontend and backend from GitHub.
- **Build Backend:** Builds the backend services using Gradle.
- **Trigger Cloud Build:** Initiates the Google Cloud build process.
- **Fetch Backend Service IPs:** Fetches dynamically assigned external IPs for backend services from GKE.
- **Deploy API Gateway:** Deploys the API Gateway using the OpenAPI spec and updates backend service IPs.

---

## OpenAPI Specification

The `openapi.yaml` file specifies the API configuration for the Google API Gateway. During the pipeline execution, the dynamically assigned backend IPs are substituted into the OpenAPI file.

---

## License

This project is licensed under the MIT License.


