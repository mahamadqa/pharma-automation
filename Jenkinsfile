pipeline {
    agent any

    /* 
    // If you configure JDK & Maven in Jenkins "Global Tool Configuration", you can uncomment this:
    tools {
        jdk 'JDK-17'
        maven 'Maven-3.9'
    }
    */

    parameters {
        choice(
            name: 'REGION',
            choices: ['localization', 'non-localization'],
            description: 'Select Test Data Environment (localization: Local data, non-localization: Non-local/Export data)'
        )
        choice(
            name: 'MODULE',
            choices: ['all', 'basic', 'purchase_order', 'regression'],
            description: 'Select Test Suite / TestNG Group to execute'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox'],
            description: 'Select Browser to run tests'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode (recommended for CI/Jenkins)'
        )
    }

    options {
        timeout(time: 60, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Environment Info') {
            steps {
                script {
                    echo "=================================================="
                    echo " Pharma Automation Test Execution Pipeline"
                    echo "=================================================="
                    echo " Test Data Profile (Region) : ${params.REGION}"
                    echo " Test Module / Groups       : ${params.MODULE}"
                    echo " Target Browser             : ${params.BROWSER}"
                    echo " Headless Mode              : ${params.HEADLESS}"
                    echo " Build ID                   : ${env.BUILD_ID}"
                    echo " Git Branch                 : ${env.GIT_BRANCH ?: 'N/A'}"
                    echo "=================================================="
                    
                    // Display Java & Maven version in build log
                    sh '''
                        if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
                            export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
                            export PATH="$JAVA_HOME/bin:$PATH"
                        elif [ -d "/usr/lib/jvm/java-1.17.0-openjdk-amd64" ]; then
                            export JAVA_HOME="/usr/lib/jvm/java-1.17.0-openjdk-amd64"
                            export PATH="$JAVA_HOME/bin:$PATH"
                        fi
                        echo "JAVA_HOME is set to: $JAVA_HOME"
                        java -version
                        javac -version || echo "javac not in default PATH"
                        mvn -version
                    '''
                }
            }
        }

        stage('Execute Automation Tests') {
            steps {
                script {
                    def groupFlag = ""
                    if (params.MODULE != 'all') {
                        groupFlag = "-Dgroups=${params.MODULE}"
                    }

                    // Run with Java 17 JDK environment
                    sh """
                        if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
                            export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
                            export PATH="\$JAVA_HOME/bin:\$PATH"
                        elif [ -d "/usr/lib/jvm/java-1.17.0-openjdk-amd64" ]; then
                            export JAVA_HOME="/usr/lib/jvm/java-1.17.0-openjdk-amd64"
                            export PATH="\$JAVA_HOME/bin:\$PATH"
                        fi

                        mvn clean test -Dregion=${params.REGION} ${groupFlag} -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}
                    """
                }
            }
        }
    }

    post {
        always {
            // Publish Extent Reports (HTML)
            publishHTML(target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'test-output',
                reportFiles: 'ExtentReport.html',
                reportName: 'Playwright Extent Report'
            ])

            // Publish TestNG / Surefire XML test results
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // Archive logs and test output artifacts
            archiveArtifacts allowEmptyArchive: true, artifacts: 'logs/**, test-output/**', fingerprint: true
        }
        success {
            echo "Automation Test Execution PASSED for [${params.REGION}] region!"
        }
        failure {
            echo "Automation Test Execution FAILED for [${params.REGION}] region. Check reports for details."
        }
    }
}
