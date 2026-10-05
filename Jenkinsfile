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
        string(
            name: 'RECIPIENT_EMAIL',
            defaultValue: 'mmulla@logilite.com',
            description: 'Recipient email address(es) for test reports (comma-separated, leave blank to skip)'
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
                    echo " Recipient Email            : ${params.RECIPIENT_EMAIL}"
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

                    // Execute Maven test (wraps with xvfb-run if headed mode is selected on headless Linux)
                    sh """
                        if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
                            export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
                            export PATH="\$JAVA_HOME/bin:\$PATH"
                        elif [ -d "/usr/lib/jvm/java-1.17.0-openjdk-amd64" ]; then
                            export JAVA_HOME="/usr/lib/jvm/java-1.17.0-openjdk-amd64"
                            export PATH="\$JAVA_HOME/bin:\$PATH"
                        fi

                        if [ "${params.HEADLESS}" = "false" ] && [ -z "\$DISPLAY" ] && which xvfb-run >/dev/null 2>&1; then
                            echo "Headed mode requested without active display: Running with xvfb-run virtual display..."
                            xvfb-run --auto-servernum --server-args="-screen 0 1920x1080x24" mvn clean test -Dregion=${params.REGION} ${groupFlag} -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}
                        else
                            mvn clean test -Dregion=${params.REGION} ${groupFlag} -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}
                        fi
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

            // Send Email with Extent Report & Logs attached
            script {
                if (params.RECIPIENT_EMAIL && params.RECIPIENT_EMAIL.trim() != '') {
                    def buildStatus = currentBuild.currentResult ?: 'SUCCESS'
                    def statusColor = (buildStatus == 'SUCCESS') ? '#28a745' : '#dc3545'

                    try {
                        emailext(
                            to: params.RECIPIENT_EMAIL,
                            subject: "[Jenkins] ${buildStatus}: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} [${params.REGION}]",
                            attachmentsPattern: 'test-output/ExtentReport.html, logs/**',
                            body: """
                                <!DOCTYPE html>
                                <html>
                                <head>
                                    <style>
                                        body { font-family: Arial, sans-serif; color: #333; }
                                        .container { padding: 20px; }
                                        .header { font-size: 20px; font-weight: bold; color: ${statusColor}; }
                                        table { border-collapse: collapse; width: 100%; max-width: 600px; margin-top: 15px; }
                                        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
                                        th { background-color: #f8f9fa; }
                                        .btn { display: inline-block; padding: 8px 16px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px; margin-top: 15px; }
                                    </style>
                                </head>
                                <body>
                                    <div class="container">
                                        <div class="header">Playwright Automation Test Execution: ${buildStatus}</div>
                                        <p>Hello Team,</p>
                                        <p>The test execution for <b>${env.JOB_NAME}</b> has completed. Below is the execution summary:</p>
                                        
                                        <table>
                                            <tr><th>Build Number</th><td>#${env.BUILD_NUMBER}</td></tr>
                                            <tr><th>Status</th><td><b style="color: ${statusColor};">${buildStatus}</b></td></tr>
                                            <tr><th>Test Profile (Region)</th><td>${params.REGION}</td></tr>
                                            <tr><th>Test Module / Suite</th><td>${params.MODULE}</td></tr>
                                            <tr><th>Browser</th><td>${params.BROWSER}</td></tr>
                                            <tr><th>Headless</th><td>${params.HEADLESS}</td></tr>
                                            <tr><th>Executed On</th><td>${new Date().format("dd-MMM-yyyy HH:mm:ss")}</td></tr>
                                        </table>

                                        <p><b>Attached in this email:</b></p>
                                        <ul>
                                            <li><code>ExtentReport.html</code> - Interactive HTML Extent Report</li>
                                            <li>Execution Logs (under <code>logs/</code>)</li>
                                        </ul>

                                        <a href="${env.BUILD_URL}" class="btn">View Jenkins Build &amp; Report</a>
                                        <br/><br/>
                                        <p>Regards,<br/><b>QA Automation Team</b></p>
                                    </div>
                                </body>
                                </html>
                            """,
                            mimeType: 'text/html'
                        )
                    } catch (Exception e) {
                        echo "Warning: Could not send email via emailext plugin (${e.getMessage()}). Make sure Email Extension Plugin and SMTP settings are configured in Jenkins."
                    }
                }
            }
        }
        success {
            echo "Automation Test Execution PASSED for [${params.REGION}] region!"
        }
        failure {
            echo "Automation Test Execution FAILED for [${params.REGION}] region. Check reports for details."
        }
    }
}
