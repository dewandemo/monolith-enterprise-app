// Snowman - Enterprise Application
//
// Declarative Jenkins pipeline. Kept alongside .travis.yml (legacy, now a
// dead CI provider) and .github/workflows/ci.yml - a very common real-world
// state for an enterprise codebase that has migrated CI providers more than
// once without fully retiring the old config.
//
// Requires a Jenkins agent with: JDK 8, Maven 3.6+, Docker (for the
// integration-test stage's MySQL/ActiveMQ containers).

pipeline {
    agent any

    tools {
        maven 'Maven-3.6'
        jdk 'JDK-8'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        MAVEN_OPTS = '-Dliquibase.should.run=false'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Unit Test') {
            steps {
                sh 'mvn -B clean test'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Static Analysis') {
            steps {
                // Advisory gate - see pom.xml (failOnViolation=false). Does
                // not fail the pipeline today; tighten once the backlog of
                // existing violations has been triaged.
                sh 'mvn -B checkstyle:checkstyle pmd:pmd pmd:cpd com.github.spotbugs:spotbugs-maven-plugin:spotbugs || true'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/checkstyle-result.xml,target/pmd.xml,target/cpd.xml,target/spotbugsXml.xml', allowEmptyArchive: true
                }
            }
        }

        stage('Dependency Vulnerability Audit') {
            steps {
                // Expected to surface real CVEs given the intentionally old
                // dependency set (Spring 4.3.x, mixed Hibernate 4/5, old
                // ActiveMQ/Quartz). Treated as backlog, not a build blocker.
                sh 'mvn -B org.owasp:dependency-check-maven:check || true'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/dependency-check-report.*', allowEmptyArchive: true
                }
            }
        }

        stage('Integration Test') {
            steps {
                sh 'docker compose up -d mysql activemq'
                sh 'sleep 20' // crude readiness wait - no real health-check gating here, deliberately legacy
                sh 'mvn -B liquibase:update || true'
                sh 'mvn -B verify || true'
            }
            post {
                always {
                    sh 'docker compose down -v || true'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B clean package -DskipTests'
                archiveArtifacts artifacts: 'target/Snowman.jar', fingerprint: true
            }
        }

        stage('Deploy -> Dev') {
            when { branch 'master' }
            steps {
                echo 'Deploying Snowman.jar to DEV environment (placeholder step)'
            }
        }

        stage('Approval: Promote to Staging') {
            when { branch 'master' }
            steps {
                input message: 'Promote build to Staging?', ok: 'Promote'
            }
        }

        stage('Deploy -> Staging') {
            when { branch 'master' }
            steps {
                echo 'Deploying Snowman.jar to STAGING environment (placeholder step)'
            }
        }

        stage('Approval: Promote to Production') {
            when { branch 'master' }
            steps {
                input message: 'Promote build to Production?', ok: 'Promote', submitterParameter: 'APPROVER'
            }
        }

        stage('Deploy -> Production') {
            when { branch 'master' }
            steps {
                echo "Deploying Snowman.jar to PRODUCTION environment, approved by ${env.APPROVER} (placeholder step)"
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
