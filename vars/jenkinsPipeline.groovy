def call(pipelineType) {
  pipeline {
    agent any
    environment {
      DOCKER = tool 'docker'
      DOCKER_EXEC = '$DOCKER/docker'
    }

    stages {

      stage('HOLA MUNDO') {
        steps {
          figlet 'HOLA-MUNDO'
          script {
            int[] array = [0,1,2,3] 
		
            for(int i=0; i < 3; i++){
                println("Hola ${i}") 
            } 
            slackOutput.slackSend()
          }
        }
      }

    }
  }
}
