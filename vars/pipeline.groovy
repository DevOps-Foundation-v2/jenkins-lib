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
            withCredentials([string(credentialsId: 'token-slack', variable: 'TOKENPAT')]) {
            withEnv(["URL=${env.BUILD_URL}"]) {
                sh('curl -d "text=SE REPORTA QUE: \n Comienza la ejecucion de pipeline \n $URL" -d "channel=pipelines-devops" -H "Authorization: Bearer $TOKENPAT" -X POST https://slack.com/api/chat.postMessage')
            }
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
                     sh 'set +x; chmod 777 mvnw'
                     //sh './mvnw clean install'
                }catch(all){
                    withEnv(["URL=${env.BUILD_URL}"]) {
                        sh('curl -d "text=SE REPORTA QUE: \n Pipeline ha fallado en Etapa de Build \n $URL" -d "channel=pipeline-devops" -H "Authorization: Bearer xoxb-3797904255923-3909536351217-Cyr3dj1QXDmCJyRGpk9Lo1on" -X POST https://slack.com/api/chat.postMessage')
                    }
                }
            }
         }
      }
        
      stage('SAST') {
         steps {
            withCredentials([string(credentialsId: 'sonarcloud', variable: 'SONARPAT')]) {
                 figlet 'SAST-SONARCLOUD'
                 //sh('set +x; ./gradlew sonarqube -Dsonar.login=$SONARPAT -Dsonar.branch.name=feature-jenkins')
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
