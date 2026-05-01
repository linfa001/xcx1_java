pipeline {
    agent any

    environment {
        PROJECT_NAME = "sso-project"
        APP_AUTH     = "xcx1-auth"
        APP_SYSTEM_A = "system-a"

        // 自动获取 Git 标签作为版本号
        VERSION = sh(
            script: 'git describe --tags --always 2>/dev/null || echo "latest"',
            returnStdout: true
        ).trim()
    }

    stages {

        stage(' 检出代码') {
            steps {
                echo '📥 从 Git 仓库拉取代码...'
                checkout scm  // 自动使用当前 Jenkins Job 配置的 Git 仓库
            }
        }

        stage('📦 安装 SSO Common 依赖') {
            steps {
                echo '🔨 构建 sso-common 并安装到本地仓库...'
                sh 'mvn install -pl sso-common -am -DskipTests'
            }
        }

        stage('☕ Maven 构建所有模块') {
            steps {
                echo "当前版本号：${VERSION}"
                sh '''
                    echo "📦 打包认证中心..."
                    mvn package -pl xcx1-auth -am -DskipTests

                    echo " 打包业务系统 A..."
                    mvn package -pl system-a -am -DskipTests
                '''
            }
        }

        stage('🐳 Docker Compose 构建并部署') {
            steps {
                sh '''
                    echo " 停止旧容器..."
                    docker compose down || true

                    echo " 构建镜像并启动服务..."
                    docker compose up -d --build

                    echo "🧹 清理无用镜像..."
                    docker image prune -f
                '''
            }
        }

        stage('✅ 健康检查') {
            steps {
                sh '''
                    echo " 等待服务启动..."
                    sleep 15

                    echo " 检查认证中心状态..."
                    curl -f http://localhost:3004/api/login/login -X POST \
                      -H "Content-Type: application/json" \
                      -d '{"username":"health","password":"check"}' || echo "认证中心未就绪"

                    echo "🔍 检查业务系统状态..."
                    curl -f http://localhost:3005/api/category/getAll || echo "业务系统未就绪"
                '''
            }
        }
    }

    post {
        success {
            echo "✅ 构建成功，版本：${VERSION}"
            echo "🌐 认证中心: http://localhost:3004"
            echo "🌐 业务系统 A: http://localhost:3005"
        }
        failure {
            echo "❌ 构建失败，请检查日志"
            sh 'docker compose logs --tail=100 || true'
        }
        always {
            cleanWs()
        }
    }
}
