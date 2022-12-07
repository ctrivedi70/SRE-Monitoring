def label = "mypod-${UUID.randomUUID().toString()}"
def serviceaccount = "jenkins-admin"

podTemplate(label: label, serviceAccount: serviceaccount, containers: [
	containerTemplate(name: 'maven', image: 'localhost:32121/root/docker_registry/aiindevops.azurecr.io/maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat',resourceRequestCpu: '150m',resourceLimitCpu: '4000m',resourceRequestMemory: '100Mi',resourceLimitMemory: '7000Mi'),
	containerTemplate(name: 'git-secrets', image: 'localhost:32121/root/docker_registry/aiindevops.azurecr.io/git-secrets:0.1', ttyEnabled: true, alwaysPullImage: true, command: 'cat'),
	containerTemplate(name: 'jq', image: 'localhost:32121/root/docker_registry/aiindevops.azurecr.io/jq', ttyEnabled: true, command: 'cat'),
	containerTemplate(name: 'jmeter', image: 'localhost:32121/root/docker_registry/aiindevops.azurecr.io/jmeter', ttyEnabled: true, command: 'cat'),
	containerTemplate(name: 'clair-scanner', image: 'localhost:32121/root/docker_registry/aiindevops.azurecr.io/clair-scanner:0.1', ttyEnabled: true, alwaysPullImage: true, command: 'cat', ports: [portMapping(name: 'clair-scanner', containerPort: '9279')],envVars: [
            envVar(key: 'DOCKER_SOCK', value: "$DOCKER_SOCK"),envVar(key: 'DOCKER_HOST', value: "$DOCKER_HOST")],
					volumes: [hostPathVolume(hostPath: "$DOCKER_SOCK", mountPath: "$DOCKER_SOCK")]),
	containerTemplate(name: 'kubeaudit', image: 'localhost:32121/root/docker_registry/aiindevops.azurecr.io/kube-audit:0.1', ttyEnabled: true, alwaysPullImage: true, command: 'cat'),
	containerTemplate(name: 'kubectl', image: 'localhost:32121/root/docker_registry/aiindevops.azurecr.io/docker-kubectl:19.03-alpine', ttyEnabled: true, command: 'cat'),
	containerTemplate(name: 'ansible', image: 'ansibleplaybookbundle/s2i-apb', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'docker', image: 'localhost:32121/root/docker_registry/aiindevops.azurecr.io/docker:1.13', ttyEnabled: true, command: 'cat',envVars: [
            envVar(key: 'DOCKER_SOCK', value: "$DOCKER_SOCK"),envVar(key: 'DOCKER_HOST', value: "$DOCKER_HOST")])],
			volumes: [hostPathVolume(hostPath: "$DOCKER_SOCK", mountPath: "$DOCKER_SOCK")],
		imagePullSecrets: ['gcrcred']
) {
    node(label) {  
        def GIT_URL= 'http://gitlab.ethan.svc.cluster.local:8084/gitlab/user07cl3/monitoring.git'
		def GIT_CREDENTIAL_ID ='gitlab'
		def GIT_BRANCH='master'
        		
		/***COMPONENT_KEY value should be same as what is given as ProjectName in in sonar.properies file ***/		
		def COMPONENT_KEY='carts';
		def rootDir = pwd()	
		def SONAR_UI='http://sonar.ethan.svc.cluster.local:9001/sonar/api/measures/component?metricKeys=';
		
		/**** provide Sonar metrickeys which needs to be published to Jenkins console ***/
		String metricKeys = "coverage,code_smells,bugs,vulnerabilities,sqale_index,tests,ncloc,quality_gate_details,duplicated_lines_density,major_violations,minor_violations,critical_violations,blocker_violations,security_rating,complexity,violations,open_issues,test_success_density,test_errors,test_execution_time,security_remediation_effort,uncovered_conditions,classes,functions,line_coverage,sqale_rating,sqale_debt_ratio,reliability_remediation_effort,coverage,code_smells,bugs,vulnerabilities,sqale_index,tests,ncloc,quality_gate_details,duplicated_lines,cognitive_complexity";
		
		/*** Below variables used in the sonar maven configuration ***/
		def SONAR_SCANNER='org.sonarsource.scanner.maven'
		def SONAR_PLUGIN='sonar-maven-plugin:3.2'
		def SONAR_HOST_URL='http://sonar.ethan.svc.cluster.local:9001/sonar'
		
		/***  DOCKER_HUB_REPO_URL is the URL of docker hub ***/
		
		def GCR_HUB_ACCOUNT = 'localhost:32121'
		def GCR_HUB_ACCOUNT_NAME = 'root'
		def GCR_HUB_REPO_NAME='docker_registry'
		def DOCKER_IMAGE_NAME = 'java'
		def ACR_HUB_ACCOUNT = 'aiindevops.azurecr.io'
        def IMAGE_TAG = '1.7.6'

        def  JOBNAME = "${JOB_NAME.split('/')[1]}"
				
		def K8S_DEPLOYMENT_NAME = 'pet-clinic'
   
                        
     try {
		
		stage('Git Checkout') {
			
			git branch: GIT_BRANCH, url: GIT_URL,credentialsId: GIT_CREDENTIAL_ID
            def function = load "${WORKSPACE}/JenkinsFunctions_Java.groovy"
			def Nap = load "${WORKSPACE}/git_scan_nonallowed.groovy"
			def Ap = load "${WORKSPACE}/git_scan_allowed.groovy"
			
			// Below two lines are to publish last commited user name and email into jenkins console logs
            sh 'GIT_NAME=$(git --no-pager show -s --format=\'%an\' $GIT_COMMIT)'
            sh 'GIT_EMAIL=$(git --no-pager show -s --format=\'%ae\' $GIT_COMMIT)'
			
        stage('Git Secret') {
		    container('git-secrets') {
		        echo "${IMAGE_TAG}"
				Nap.nonAllowedPattern()
				Ap.AllowedPattern()	
				sh 'git secrets --scan'
			}
		}
		
		committerEmail = sh (
        script: 'git --no-pager show -s --format=\'%ae\'',
        returnStdout: true
        ).trim()
        echo "$committerEmail"
        sh '''
        committerEmail='''+ committerEmail +'''
        echo "committerEmail is $committerEmail"
        '''
        sh '''
        GIT_NAME=$(git --no-pager show -s --format=\'%an\' $GIT_COMMIT)
        echo "GIT_NAME is ${GIT_NAME}"
        '''

        wrap([$class: 'BuildUser'])
        {
            sh 'echo ${BUILD_USER}'
			sh '''
			committerEmail='''+ committerEmail +'''
			BUILDUSER=`echo $committerEmail | awk -F@ '{print $1}'`
			echo ${BUILDUSER}
		    sed -i "s/builduser/${BUILDUSER}/g" grafanadeploy.yaml
		    sed -i "s/builduser/${BUILDUSER}/g" grafanaservice.yaml
            sed -i "s/builduser/${BUILDUSER}/g" namespace.yaml
		    '''
        }
				
	
		/*stage ('Kubectl Check')
           {*/
               container('kubectl')
               {
         
         
           sh 'kubectl get nodes  >> nodes.txt'
            sh label: '', script: '''
            if grep -q master nodes.txt; then
            echo "master found" >> master.txt
            else
            echo not found
            fi
           echo cat master.txt
           '''
               }
          
          if (fileExists('master.txt'))
          {           
            echo " It is  non managed kubernetes service hence executing kube audit and kube bench stages"
            /*stage('Kube-Bench Scan') {*/
            container('kubectl') {       
            sh '''
			v=`kubectl version --short | grep -w 'Server Version'`
			v1=`echo $v | awk -F: '{print $2}'`
			v2=`echo $v1 | sed 's/v//g'`
			versionserver=`echo $v2 | cut -f1,2 -d'.'`
			echo kubernetes version is ${versionserver}
            kubectl run --rm -i kube-bench-master-frontend-${BUILD_ID} --image=aquasec/kube-bench:0.6.0 --restart=Never --overrides="{ \\"apiVersion\\": \\"v1\\", \\"spec\\": { \\"imagePullSecrets\\": [ { \\"name\\": \\"gcrcred\\" } ], \\"hostPID\\": true, \\"nodeSelector\\": { \\"kubernetes.io/role\\": \\"master\\" }, \\"tolerations\\": [ { \\"key\\": \\"node-role.kubernetes.io/master\\", \\"operator\\": \\"Exists\\", \\"effect\\": \\"NoSchedule\\" } ] } }" -- master --version ${versionserver}
            kubectl run --rm -i kube-bench-node-frontend-${BUILD_ID} --image=aquasec/kube-bench:0.6.0 --restart=Never --overrides="{ \\"apiVersion\\": \\"v1\\", \\"spec\\": { \\"imagePullSecrets\\": [ { \\"name\\": \\"gcrcred\\" } ], \\"hostPID\\": true } }" -- node --version ${versionserver}
            '''
            }      
         
        }
        else
        {
            echo " It is Managed Kubernetes service hence executing kubebench and kube-audit only on nodes "
            /*stage('Kube-Bench Scan') {*/
            container('kubectl') {   
           
            sh '''
			v=`kubectl version --short | grep -w 'Server Version'`
			v1=`echo $v | awk -F: '{print $2}'`
			v2=`echo $v1 | sed 's/v//g'`
			versionserver=`echo $v2 | cut -f1,2 -d'.'`
			echo kubernetes version is ${versionserver}
            kubectl run --rm -i kube-bench-node-frontend-${BUILD_ID} --image=aquasec/kube-bench:latest --restart=Never --overrides="{ \\"apiVersion\\": \\"v1\\", \\"spec\\": { \\"hostPID\\": true } }" -- node --version ${versionserver}
            '''
            }
          
				
      
           }
           /*}*/
			
		stage('Graphana_Container') {
			
			
			container('kubectl') {
			   sh("kubectl apply -f namespace.yaml")
			   try{
                   sh '''
			committerEmail='''+ committerEmail +'''
			BUILDUSER=`echo $committerEmail | awk -F@ '{print $1}'`
            kubectl get deployment/grafana-deployment ${BUILDUSER}
            '''   
               }
               catch (Exception e){
                    sh '''
			committerEmail='''+ committerEmail +'''
			BUILDUSER=`echo $committerEmail | awk -F@ '{print $1}'`
                    kubectl apply -f grafanadeploy.yaml
                    '''
                    sleep 10
               }
               try{
                   sh '''
			committerEmail='''+ committerEmail +'''
			BUILDUSER=`echo $committerEmail | awk -F@ '{print $1}'`
            kubectl get service/grafana-service ${BUILDUSER}
            '''
                    
               }
               catch (Exception e){
                    sh '''
			committerEmail='''+ committerEmail +'''
			BUILDUSER=`echo $committerEmail | awk -F@ '{print $1}'`
                    kubectl apply -f grafanaservice.yaml
                    '''
                    sleep 30
               }
               sh '''
			committerEmail='''+ committerEmail +'''
			BUILDUSER=`echo $committerEmail | awk -F@ '{print $1}'`
            kubectl get pods -n ${BUILDUSER}
            kubectl get svc grafana-service -n ${BUILDUSER} 
            kubectl rollout status deployment/grafana-deployment -n ${BUILDUSER}
            '''
               
             sleep 60 // seconds 
            
             LB = sh (returnStdout: true, script: '''committerEmail='''+ committerEmail +''' ; BUILDUSER=`echo $committerEmail | awk -F@ '{print $1}'`;kubectl get svc grafana-service -n ${BUILDUSER} -o jsonpath="{.status.loadBalancer.ingress[*]['ip', 'hostname']}" ''')
             echo "LB: ${LB}"
             def loadbalancer = "http://"+LB+":8081"

             echo "loadbalancer: ${loadbalancer}"
			 echo "application_url: ${loadbalancer}"
				
			}
        }
        }
	currentBuild.result = 'SUCCESS'
	echo "RESULT: ${currentBuild.result}"
	echo "Finished: ${currentBuild.result}"
              
	} catch (Exception err) {
        currentBuild.result = 'FAILURE'
        
        echo "RESULT: ${currentBuild.result}"
        echo "Finished: ${currentBuild.result}"
        
		}               
     //logstashSend failBuild: false, maxLines: -1
     }
     }