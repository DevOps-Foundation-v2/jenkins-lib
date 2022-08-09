def slackSend(){
withCredentials([string(credentialsId: 'token-slack', variable: 'TOKENPAT')]) {
            withEnv(["URL=${env.BUILD_URL}"]) {
                sh('curl -d "text=SE REPORTA QUE: \n Comienza la ejecucion de pipeline \n $URL" -d "channel=pipelines-devops" -H "Authorization: Bearer $TOKENPAT" -X POST https://slack.com/api/chat.postMessage')
            }
          }
}
