pipeline {
    agent any

    environment {
        APP_NAME     = "xcx1-app"
//         DOCKER_REG  = "docker.io"
//         DOCKER_REPO = "你的dockerhub用户名/xcx1-app"

        // 自动获取 Git 标签作为版本号
        VERSION = sh(
            script: 'git describe --tags --always',
            returnStdout: true
        ).trim()
    }

    stages {

/*         stage('🔍 检出代码') {
            steps {
                git(
                    url: 'https://github.com/linfa001/xcx1_java.git',
                    branch: 'main',
                    credentialsId: 'dd571900-ed8c-459e-883c-47d6d0539c09'
                )
            }
        } */

        stage('☕ Maven 构建') {
            steps {
                sh '''
                    echo "当前版本号：${VERSION}"
                    mvn clean package -DskipTests
                '''
            }
        }

        stage('🐳 构建 Docker 镜像') {
            steps {
                sh '''
                    docker build \
                      --build-arg VERSION=${VERSION} \
                      -t ${APP_NAME}:${VERSION} .
                '''
            }
        }

        stage('🚀 部署容器') {
            steps {
                sh '''
                    docker stop ${APP_NAME} || true
                    docker rm ${APP_NAME} || true

                    docker run -d \
                      --name ${APP_NAME} \
                      -p 3003:3001 \
                      --restart=always \
                      ${APP_NAME}:${VERSION}
                '''
            }
        }
    }

    post {
        success {
            echo "✅ 构建成功，版本：${VERSION}"
        }
        failure {
            echo "❌ 构建失败"
        }
        always {
            cleanWs()
        }
    }
}