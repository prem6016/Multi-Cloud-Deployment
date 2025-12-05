pipeline {
    agent any

    environment {
        IMAGE_NAME = "yourdockerhubuser/bankpro-core"
        IMAGE_TAG  = "${BUILD_NUMBER}"
        IMAGE_FULL = "${IMAGE_NAME}:${IMAGE_TAG}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn -B clean test'
                sh 'mvn -B -DskipTests package'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
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
