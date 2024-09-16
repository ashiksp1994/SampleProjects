pipeline {
    agent any
    
    environment {
        GCR_PROJECT = 'physiotherapy-clinic-431211'
        GCP_KEYFILE = credentials('gcr-service-account')
        GCP_CLUSTERNAME = "autopilot-cluster-1"
        GCP_CLUSTERZONE = "europe-central2"
        BUILD_NUMBER = "${env.BUILD_NUMBER ?: 'latest'}"
        SERVICE_NAME = "api-gateway"
        API_NAME = "physiotherapy-api"
        API_CONFIG_NAME = "physiotherapy-api-config"
        GCP_LOCATION = "europe-west1"
        //MAX_WAIT_TIME = 600 // maximum time to wait for the IP in seconds (e.g., 10 minutes)
        //POLL_INTERVAL = 30 // polling interval in seconds
    }

    stages {
        stage('Verify Tools') {
            steps {
                script {
                    sh "kubectl version --client"
                    sh "gcloud --version"
                }
            }
        }

        stage('Checkout Code') {
            parallel {
                stage('Checkout Backend') {
                    steps {
                        script {
                             dir('backend') {
                           git branch: 'main', credentialsId: "github-pat-credentials", url: 'https://github.com/ashiksp1994/DevOpsProject.git'
                           //sh 'ls -la'
                             }
                        }
                    }
                }
                stage('Checkout Frontend') {
                    steps {
                        script {
                            dir('frontend') {
                                git credentialsId: "github-pat-credentials", url: 'https://github.com/ashiksp1994/physiotherapy-clinic.git'
                            }
                        }
                    }
                }
            }
        }

      //  stage('Run Tests') {
           // parallel {
        stage('Backend Tests') {
            steps {
              
                    script {
                        def services = ['appointment-service', 'token-service', 'user-service']
                        for (service in services) {
                            dir("backend/${service}") {
                                echo "Service build : ${service}"
                                sh 'chmod +x gradlew || true'
                                sh './gradlew clean build --no-daemon -x test'

                                // Check if WAR file exists
                                def warFile = fileExists("build/libs/${service}-0.0.1-SNAPSHOT.war")
                                if (!warFile) {
                                    error("${service} build failed: WAR file not found!")
                                } else {
                                    echo "${service} WAR file exists."
                                }
                            }
                        }
                    }
               
            }
        }
        stage('Run Integration Tests for Backend') {
            steps {
                script {
                    def services = ['user-service']
                    for (service in services) {
                        dir("backend/${service}") {
                            echo "Running integration tests for ${service}"
                            sh './gradlew clean build test --scan --no-daemon --console=plain -Dscan.termsOfService.accept=true'
                        }
                    }
                }
            }
        }
                /* stage('Frontend Tests') {
                    steps {
                        script {
                            dir('frontend') {
                                echo "Service build : frontend"
                               // sh 'npm install'
                                // sh 'npm run build'
                                //sh 'npm test'
                            }
                        }
                    }
                } */
           // }
       // }

        stage('Trigger Cloud Build for Build and Deployment') {
            steps {
                script {
                    echo "Triggering Cloud Build"
                    def buildID = ""
                    def buildStatus = ""
                    try {
                        buildID = sh(script: """
                            gcloud builds submit --config=cloudbuild.yaml --project ${GCR_PROJECT} --format='value(id)' --async
                        """, returnStdout: true).trim()
                        echo "Build submitted with ID: ${buildID}"

                        // Poll for build status until it's complete
                        while (true) {
                            buildStatus = sh(script: """
                                gcloud builds describe ${buildID} --project ${GCR_PROJECT} --format='value(status)'
                            """, returnStdout: true).trim()

                            if (buildStatus == "SUCCESS" || buildStatus == "FAILURE" || buildStatus == "CANCELLED") {
                                break
                            }
                            echo "Current build status: ${buildStatus}. Waiting..."
                            sleep(30) // Wait for 30 seconds before polling again
                        }

                        echo "Final Build status: ${buildStatus}"

                        if (buildStatus != 'SUCCESS') {
                            echo "Build ID: ${buildID} failed. Fetching logs for more details..."
                            def buildLogs = sh(script: "gcloud builds log ${buildID} --project ${GCR_PROJECT}", returnStdout: true)
                            echo buildLogs
                            error("Cloud Build failed with status: ${buildStatus}")
                        }

                    } catch (Exception e) {
                        echo "Failed to submit build or retrieve status: ${e.message}"
                        error("Build submission failed.")
                    }
                }
            }
        }
        
        stage('Fetch Backend Service IPs') {
            steps {
                script {
                    echo "Retrieving external IPs for backend services..."

                    def userServiceIP = ""
                    def appointmentServiceIP = ""
                    def tokenServiceIP = ""
                    def startTime = System.currentTimeMillis()
                    
                    while (true) {
                        // Check if the maximum wait time has been exceeded
                        def elapsedTime = (System.currentTimeMillis() - startTime) / 1000
                        if (elapsedTime > 600) {
                            error("ERROR: Timed out waiting for external IPs")
                        }

                        // Fetch external IPs
                        userServiceIP = sh(script: "kubectl get svc user-service -n default -o jsonpath='{.status.loadBalancer.ingress[0].ip}'", returnStdout: true).trim()
                        appointmentServiceIP = sh(script: "kubectl get svc appointment-service -n default -o jsonpath='{.status.loadBalancer.ingress[0].ip}'", returnStdout: true).trim()
                        tokenServiceIP = sh(script: "kubectl get svc token-service -n default -o jsonpath='{.status.loadBalancer.ingress[0].ip}'", returnStdout: true).trim()

                        // Check if all IPs have been assigned
                        if (userServiceIP && appointmentServiceIP && tokenServiceIP) {
                            echo "User Service IP: ${userServiceIP}"
                            echo "Appointment Service IP: ${appointmentServiceIP}"
                            echo "Token Service IP: ${tokenServiceIP}"
                            break
                        }

                        // Wait for the next poll
                        echo "IP not assigned yet. Waiting for 30 seconds..."
                        sleep(30)
                    }

                    // Update OpenAPI specification with the correct IPs
                    sh """
                        sed -i "s|USER_SERVICE_IP|${userServiceIP}|" openapi.yaml
                        sed -i "s|APPOINTMENT_SERVICE_IP|${appointmentServiceIP}|" openapi.yaml
                        sed -i "s|TOKEN_SERVICE_IP|${tokenServiceIP}|" openapi.yaml
                    """
                }
            }
        }

        stage('Deploy API Gateway') {
            steps {
                script {
                    echo "Deploying API Gateway"
                    // Check if the gateway exists and delete it
                    def gatewayExists = sh(script: """
                        gcloud api-gateway gateways list \
                        --location=${GCP_LOCATION} --project=${GCR_PROJECT} --format='value(name)' | grep ${SERVICE_NAME} || true
                    """, returnStdout: true).trim()

                    if (gatewayExists) {
                        echo "API Gateway ${SERVICE_NAME} exists. Deleting..."
                        sh """
                            gcloud api-gateway gateways delete ${SERVICE_NAME} \
                            --location=${GCP_LOCATION} --project=${GCR_PROJECT} --quiet
                        """
                    }
                    // Check if the API config exists and delete if it does
                    def apiConfigExists = sh(script: """
                        gcloud api-gateway api-configs list \
                        --api=${API_NAME} --project=${GCR_PROJECT} --format='value(name)' | grep ${API_CONFIG_NAME} || true
                    """, returnStdout: true).trim()

                    if (apiConfigExists) {
                        echo "API config ${API_CONFIG_NAME} exists. Deleting..."
                        
                        sh """
                            gcloud api-gateway api-configs delete ${API_CONFIG_NAME} \
                            --api=${API_NAME} --project=${GCR_PROJECT} --quiet
                        """
                    }

                    
                    // Create the new API config
                    echo "Creating API config ${API_CONFIG_NAME}..."
                    sh """
                        gcloud api-gateway api-configs create ${API_CONFIG_NAME} \
                        --api=${API_NAME} \
                        --openapi-spec=openapi.yaml \
                        --project=${GCR_PROJECT}
                    """

                    // Create the new API Gateway
                    echo "Creating API Gateway ${SERVICE_NAME}..."
                    sh """
                        gcloud api-gateway gateways create ${SERVICE_NAME} \
                        --api=${API_NAME} \
                        --api-config=${API_CONFIG_NAME} \
                        --location=${GCP_LOCATION} \
                        --project=${GCR_PROJECT}
                    """
                }
            }
        }

    }

    post {
        always {
                        
            // Archive test reports (HTML and JUnit results)
            archiveArtifacts artifacts: '**/build/reports/tests/test/index.html', allowEmptyArchive: true
            junit '**/build/test-results/test/TEST-*.xml'

            // Clean workspace always, even if there are errors
            cleanWs()
        }
        success {
            echo 'Build and deployment successful!'
        }
        failure {
            echo 'Build or deployment failed!'
        }
    }
}
