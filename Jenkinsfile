pipeline {
    agent any

    environment {
        IMAGE_NAME = "prem6016/bankpro-core"
        IMAGE_TAG  = "${BUILD_NUMBER}"
        IMAGE_FULL = "${IMAGE_NAME}:${IMAGE_TAG}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // Run tests and build in one step (no skipping)
        stage('Build & Test') {
            steps {
                // run tests and package; do NOT skip tests
                sh 'mvn -B -DskipTests=false clean package'
            }
        }

        stage('Debug - show workspace and surefire reports') {
            steps {
                sh 'pwd || true'
                sh 'echo "Workspace root listing:" && ls -la'
                sh 'echo "Find surefire reports (full tree):" && find . -maxdepth 6 -type d -name target -exec sh -c "echo ==== {}; ls -la {}/surefire-reports || true" \\;'
                sh 'echo "List all xml files (first 200 lines):" && find . -name "*.xml" -print | sed -n "1,200p" || true'
            }
        }

        stage('Publish Test Results') {
            steps {
                script {
                    // Publish surefire XML reports. This will fail the build if none are found.
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: false, keepLongStdio: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_FULL} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')
                ]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${IMAGE_FULL}
                    """
                }
            }
        }

        stage('Deploy via Ansible') {
            steps {
                withCredentials([
                    sshUserPrivateKey(credentialsId: 'ansible-ssh-key', keyFileVariable: 'SSH_KEY'),
                    string(credentialsId: 'ansible-user', variable: 'ANSIBLE_USER'),
                    string(credentialsId: 'ansible-host', variable: 'ANSIBLE_HOST')
                ]) {
                    sh """
                        chmod 600 ${SSH_KEY}
                        ssh -o StrictHostKeyChecking=no -i ${SSH_KEY} ${ANSIBLE_USER}@${ANSIBLE_HOST} \\
                            'cd ~/ansible && ansible-playbook -i inventory/hosts deploy.yml \\
                             --extra-vars "docker_image=${IMAGE_FULL}"'
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Deployment successful: ${IMAGE_FULL}"
        }
    }
}
