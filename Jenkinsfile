pipeline {
    agent any

    tools {
        maven "maven3"
    }

    stages {
        stage('Build') {
            steps {
             withCredentials([usernamePassword(credentialsId: 'artifactory', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                sh "mvn -DmavenUsername=$USERNAME -DmavenPassword=$PASSWORD -Dmaven.test.failure.ignore=true clean package"
                }
                script{
                    docker.build("3logicgroup/profiles-container:latest","-f Dockerfile .")
                }
            }
        }
        stage('Deploy') {
            steps {
                sh "docker-compose -f /var/jenkins_home/grvt-test/docker-compose.yaml up -d profiles-container"
                sh "yes y | docker image prune"
            }
        }
    }
}
