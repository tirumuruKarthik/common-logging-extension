pipeline {
    agent any 
 
    parameters {
        booleanParam(name: 'IS_MAVEN_RELEASE',
                defaultValue: false,
                description: 'Enable if you would like to build maven release candidate.'
        )
	
	booleanParam(name: 'IS_PUBLISH_REQUIRED',
                defaultValue: false,
                description: 'Enable if you would like to publish into maven repo.'
        )
        	    
        gitParameter name: 'BRANCH_TAG',
                     type: 'PT_BRANCH_TAG',
                     defaultValue: 'develop',
                     description: 'Choose a git branch to start CI/CD pipeline.'	
    	
    }
    
 	environment {
		MULE_ENV = "dev"
		GITHUB_CREDENTIAL_ID = credentials('Jenkins-Fatface-Pat')
 		MULE_CLOUDHUB_URI = 'https://anypoint.mulesoft.com'
 		MULE_CLOUDHUB_USER = 'jenkins@fatface.com'
 		MULE_CLOUDHUB_PASSWORD = 'jenkins123'
 		MULE_CLOUDHUB_BUSINESS_GROUP = 'fatface'
 		MULE_CLOUDHUB_REGION = 'eu-west-1'
 		MULE_CLOUDHUB_WORKER_TYPE = 'Micro'
 		MULE_CLOUDHUB_WORKERS = '1'

 	}
 
    triggers {
            pollSCM('0 7,16 * * *')
    }
	
	
    tools {
            maven 'maven-3.5.4'
	    jdk 'jdk8u172-b11'
    }
	
    stages {

	stage ('Prepare: Git checkout') {
		steps {	
			
			script {
			        checkout([$class: 'GitSCM',
				  branches: [[name: "${params.BRANCH_TAG}"]],
				  doGenerateSubmoduleConfigurations: false,
				  extensions: [[$class: 'WipeWorkspace'], 
						[$class: 'LocalBranch', localBranch: "${params.BRANCH_TAG}"]],
				  gitTool: 'Default',
				  submoduleCfg: [],
					  userRemoteConfigs: [[url: "https://$GITHUB_CREDENTIAL_ID@github.com/FatFace/common-logging-extension.git", credentialsId: "${GITHUB_CREDENTIAL_ID}"]]
					])	   
				
			      
			}
		}
	}

	
        stage ('Build: Package'){
            when {
            		expression {return params.IS_MAVEN_RELEASE == false}
			expression { BRANCH_TAG ==~ '^(?:.*develop|.*master|.*feature/.*|.*release/.*|.*fix/.*|.*hotfix/.*)$' }
            } 
            steps {
                script {               
			def lastCommit = sh returnStdout: true, script: 'git log -1'
	                if (lastCommit.contains("[maven-release-plugin]")){
	                    echo  "Deployment skipped because it has been managed by release plugin"
	                } else {

				echo  "Building artifact.."
			     	withMaven(globalMavenSettingsConfig: 'global-maven-settings-xml-id') {
					sh   'mvn clean package -DskipMunitTests'
				 }
   			}
                 }
            }
        } //end:stage:build
	    
        stage ('Peform: Test and QA'){
            when {
                 expression {return params.IS_MAVEN_RELEASE == false}
		 expression {return params.IS_PUBLISH_REQUIRED == false}
                 expression { BRANCH_TAG ==~ '^(?:.*develop)$' }            
            } 
            steps {
                    
                script {
                            def lastCommit = sh returnStdout: true, script: 'git log -1'
                            if (lastCommit.contains("[maven-release-plugin]")){
                                echo "Deployment skipped because it has been managed by release plugin"
                            } else {
                                echo  "Build and test artifact.."
                                withMaven(globalMavenSettingsConfig: 'global-maven-settings-xml-id') {
                                   sh 'mvn clean package -Dmule.env="${MULE_ENV}" -Dencryption.key="${ENCRYPTION_KEY}"'
                            	}

                           }
               }
                    
           }
	}//end:stage:Test and QA
		
	stage ('Publish: To maven repo'){
            when {
                   expression {return params.IS_MAVEN_RELEASE == false}
                   expression {return params.IS_PUBLISH_REQUIRED}
                   expression { BRANCH_TAG ==~ '^(?:.*develop|.*release/.*|.*fix/.*|.*hotfix/.*)$' }         
                } 
            steps {
                    
                script {
                    def lastCommit = sh returnStdout: true, script: 'git log -1'
                    if (lastCommit.contains("[maven-release-plugin]")){
                        echo  "Deployment skipped because it has been managed by release plugin"
                    } else {
                        withMaven(globalMavenSettingsConfig: 'global-maven-settings-xml-id') {
                            	echo  "Deploying package to maven repository"
                                sh   'mvn clean package deploy -DskipMunitTests'
                        }
                        
                    }
                }
                    
            }
        }//end:stage:Publish
        
        stage ('Deploy: To Mule runtime..'){
			when {
                  		expression {return params.IS_MAVEN_RELEASE == false}
				expression {return params.IS_PUBLISH_REQUIRED == false}
			 	expression {return params.IS_MULE_DEPLOYMENT_REQUIRED}
			      	expression { BRANCH_TAG ==~ '^(?:.*develop|.*release/.*|.*fix/.*|.*hotfix/.*)$' } 				
	        	} 
	 		steps {
	 			    
					script {
	                    def lastCommit = sh returnStdout: true, script: 'git log -1'
	                    if (lastCommit.contains("[maven-release-plugin]")){
	                        echo  "Deployment skipped because it has been managed by release plugin"
	                    } else {
	                        echo  "Deploying application to Mule runtime" 
	                         withMaven(globalMavenSettingsConfig: 'global-maven-settings-xml-id') {
	                            	echo  "Deploying package to maven repository"
	                             sh   'mvn clean package deploy -DmuleDeploy -DskipMunitTests'
                        		}
	                    }
	                }
					
	 		}
 		} //end:stage:Publish

	   stage('Perform: Maven release') {
            when {
                expression {
                    return params.IS_MAVEN_RELEASE
                }
				
		branch 'master'
                
            } 
            steps {
                script {
                    def lastCommit = sh returnStdout: true, script: 'git log -1'
                    if (lastCommit.contains("[maven-release-plugin]")){
                         echo  "Commit done by Maven Release Plugin - build is being skipped"

                    } else {
                    //checkout needs to be done, because Jenkins uses shallow clones, which causes 
                    //“Git fatal: ref HEAD is not a symbolic ref” exception while using Maven Release Plugin
                         //sh "git checkout ${env.BRANCH_NAME}"
						//sh "git reset --hard origin/${env.BRANCH_NAME}"
						try{
							withMaven(globalMavenSettingsConfig: 'global-maven-settings-xml-id') {
								 sh """
									git config --global user.email jenkins@fatface.com
									git config --global user.name Jenkins-FatFace
									"""
	                            				echo  "Performing maven release..."
	                                 			sh 'mvn clean --batch-mode -Dmule.env="${MULE_ENV}" -Dencryption.key="${ENCRYPTION_KEY}" release:prepare release:perform'
	                        			
								 
	                         			}

	                       
						} catch (Exception ex) {
						 	withMaven(globalMavenSettingsConfig: 'global-maven-settings-xml-id') {
	                            	echo  "Error occured and performing clean release process..."
	                                 sh 'mvn release:clean'
	                        			 
	                         }
						}
                    }
                } 
            }
        }//end:stage:Release
               
    }//end:stages
post {
        always {
            script {
                def lastCommit = sh returnStdout: true, script: 'git log -1'
                if (lastCommit.contains("[maven-release-plugin]")){
                    echo "Commit done by Maven Release Plugin - build is being skipped"
                }else {
                    cleanWs()
                }
            }
        }
    }     				    
 }//end:pipeline
