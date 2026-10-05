pipeline {
    agent any

    /* 
    // Uncomment and configure if Maven and JDK tools are configured in Jenkins Global Tool Configuration
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
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
            }
        }

        stage('Execute Automation Tests') {
            steps {
                script {
                    def groupFlag = ""
                    if (params.MODULE != 'all') {
                        groupFlag = "-Dgroups=${params.MODULE}"
                    }

                    // Execute Maven test with dynamic properties passed to DataManager and BaseTest
                    sh "mvn clean test -Dregion=${params.REGION} ${groupFlag} -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}"
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
