pipeline {
    agent any

    parameters {
        choice(
            name: 'REGION',
            choices: ['india', 'abroad'],
            description: 'Select Business Partner Location / Region (Local vs Abroad)'
        )
        choice(
            name: 'MODULE',
            choices: ['basic', 'purchase_order', 'regression', 'all'],
            description: 'Select Module / Test Suite to execute'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox'],
            description: 'Select Browser'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode on CI/Jenkins'
        )
    }

    stages {
        stage('Environment Info') {
            steps {
                echo "=========================================="
                echo "Running Tests for Region : ${params.REGION}"
                echo "Running Module(s)        : ${params.MODULE}"
                echo "Browser                  : ${params.BROWSER}"
                echo "Headless Mode            : ${params.HEADLESS}"
                echo "=========================================="
            }
        }

        stage('Execute Automation Tests') {
            steps {
                script {
                    def groupFlag = ""
                    if (params.MODULE != 'all') {
                        groupFlag = "-Dgroups=${params.MODULE}"
                    }
                    
                    // Run Maven with dynamic system properties passed to Playwright / TestNG
                    sh "mvn clean test -Dregion=${params.REGION} ${groupFlag} -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}"
                }
            }
        }
    }

    post {
        always {
            // Archive Extent Test Reports & TestNG Results
            publishHTML(target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'test-output',
                reportFiles: 'ExtentReport.html',
                reportName: 'Playwright Extent Report'
            ])
        }
    }
}
