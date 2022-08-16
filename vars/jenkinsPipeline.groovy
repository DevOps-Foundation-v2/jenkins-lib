def call(pipelineType) {
  pipeline {
    agent any
    environment {
      DOCKER = tool 'docker'
      DOCKER_EXEC = '$DOCKER/docker'
    }

    stages {

      stage('SCM') {
        steps {
          figlet 'SOURCE-CONTROL-MANAGEMENT'
          checkout scm // clonacion de codigo en nodo
          script {
            slackOutput.slackSend()
          }
        }
      }

      stage('JIRA') {
        steps {
          withCredentials([string(credentialsId: 'token-jira', variable: 'JIRAPAT')]) {
            //jiraTransitionIssue idOrKey: 'DF-1', input: env.transitionInput
            sh('set -x; curl -u clagos353@gmail.com:$JIRAPAT -X POST --data "{\\"transition\\":{\\"id\\":\\"3\\"}}" -H "Content-Type: application/json" "https://fundamentosdevops.atlassian.net/rest/api/3/issue/DF-1/transitions"')
          }
        }
      }

      stage('BUILD') {
        steps {
          figlet 'BUILD'
          script {
            try {
              sh 'set -x; chmod +x gradlew'
              sh './gradlew clean build'
            } catch (all) {
              slackOutput.slackSend()
            }
          }
        }
      }

      stage('SAST') {
        steps {
          withCredentials([string(credentialsId: 'sonarcloud', variable: 'SONARPAT')]) {
            figlet 'SAST-SONARCLOUD'
            sh('set +x; ./gradlew sonarqube -Dsonar.login=$SONARPAT -Dsonar.branch.name=feature-jenkins')
          }
        }
      }

      stage('QA') {
        steps {
          figlet 'QA'
          withCredentials([string(credentialsId: 'token-jira', variable: 'JIRAPAT')]) {
            sh('set -x; curl -u clagos353@gmail.com:$JIRAPAT -X POST --data "{\\"transition\\":{\\"id\\":\\"4\\"}}" -H "Content-Type: application/json" "https://fundamentosdevops.atlassian.net/rest/api/3/issue/DF-1/transitions"')
          }
          input message: 'Pruebas QA'
        }
      }

      stage('Build Image') {
        steps {
          figlet 'IMAGE'
        }
      }

      stage('DEPLOY') {
        steps {
          figlet 'DEPLOY'
          withCredentials([string(credentialsId: 'token-jira', variable: 'JIRAPAT')]) {
            //jiraTransitionIssue idOrKey: 'DF-1', input: env.transitionInput
            sh('set -x; curl -u clagos353@gmail.com:$JIRAPAT -X POST --data "{\\"transition\\":{\\"id\\":\\"2\\"}}" -H "Content-Type: application/json" "https://fundamentosdevops.atlassian.net/rest/api/3/issue/DF-1/transitions"')
          }
        }
      }

    }
  }
}
