pipeline {
    agent any

    environment {
        PROJECT_NAME = "xcx1-project"
    }

    stages {

        stage(' 检出代码') {
            steps {
                echo ' 从 Git 仓库拉取代码...'
                checkout scm
                script {
                    // 自动获取 Git 标签作为版本号
                    env.VERSION = sh(
                        script: 'git describe --tags --always 2>/dev/null || echo "latest"',
                        returnStdout: true
                    ).trim()
                }
            }
        }

        stage(' 安装 SSO Common 依赖') {
            steps {
                echo ' 构建 sso-common 并安装到本地仓库...'
                sh 'mvn install -pl sso-common -am -DskipTests'
            }
        }

        stage(' Maven 构建所有模块') {
            steps {
                echo "当前版本号：${VERSION}"
                sh '''
                    echo " 打包认证中心..."
                    mvn package -pl xcx1-auth -am -DskipTests

                    echo " 打包业务系统 A..."
                    mvn package -pl system-a -am -DskipTests

                    echo " 打包网关..."
                    mvn package -pl xcx1-gateway -am -DskipTests
                '''
            }
        }

        stage(' Docker 构建 (本地镜像)') {
            steps {
                sh """
                    echo " 构建认证中心镜像..."
                    docker build -t xcx1-auth:${VERSION} -f xcx1-auth/Dockerfile-auth .

                    echo " 构建业务系统镜像..."
                    docker build -t system-a:${VERSION} -f system-a/Dockerfile-system-a .

                    echo " 构建网关镜像..."
                    docker build -t xcx1-gateway:${VERSION} -f xcx1-gateway/Dockerfile-gateway .
                """
            }
        }

        stage(' 导入镜像到 K8s 集群') {
            steps {
                sh """
                    echo " 导入镜像到 k3d 集群..."
                    k3d image import system-a:${VERSION} -c my-cluster
                    k3d image import xcx1-auth:${VERSION} -c my-cluster
                    k3d image import xcx1-gateway:${VERSION} -c my-cluster
                """
            }
        }

        stage(' K8s 部署') {
            steps {
                script {
                    // 部署认证中心 (端口 3004)
                    stage('Deploying xcx1-auth') {
                        echo "========== 开始部署服务: xcx1-auth =========="

                        sh """
                            echo " 更新 xcx1-auth 镜像版本..."
                            sed -i 's|xcx1-auth:latest|xcx1-auth:${VERSION}|g' ./xcx1-auth/k8s-deploy.yaml

                            echo " 应用 K8s 部署配置..."
                            kubectl apply -f ./xcx1-auth/k8s-deploy.yaml

                            echo " 配置环境变量 (对应原 docker run -e)..."
                            kubectl set env deployment/xcx1-auth \
                                NACOS_ADDR=host.docker.internal:8848 \
                                MYSQL_HOST=host.docker.internal \
                                MYSQL_PORT=3306 \
                                MYSQL_USER=root \
                                REDIS_HOST=host.docker.internal \
                                JWT_SECRET=defaultSecretKeyForJWTTokensMustBeLongEnough2024
                            kubectl set env deployment/xcx1-auth MYSQL_PASSWORD="" --overwrite
                        """

                        echo " 等待认证中心启动..."
                        sh 'sleep 5'
                        echo "========== 服务 xcx1-auth 部署完成 (端口3004已隐藏) =========="
                    }

                    // 部署业务系统 (端口 3005)
                    stage('Deploying system-a') {
                        echo "========== 开始部署服务: system-a =========="

                        sh """
                            echo " 更新 system-a 镜像版本..."
                            sed -i 's|system-a:latest|system-a:${VERSION}|g' ./system-a/k8s-deploy.yaml

                            echo " 应用 K8s 部署配置..."
                            kubectl apply -f ./system-a/k8s-deploy.yaml

                            echo " 配置环境变量 (对应原 docker run -e)..."
                            kubectl set env deployment/system-a \
                                NACOS_ADDR=host.docker.internal:8848 \
                                MYSQL_HOST=host.docker.internal \
                                MYSQL_PORT=3306 \
                                MYSQL_USER=root \
                                REDIS_HOST=host.docker.internal \
                                SSO_ENABLE_FILTER=true \
                                SSO_SECRET_KEY=defaultSecretKeyForJWTTokensMustBeLongEnough2024
                            kubectl set env deployment/system-a MYSQL_PASSWORD="" --overwrite
                        """

                        echo "========== 服务 system-a 部署完成 (端口3005已隐藏) =========="
                    }

                    // 部署网关 (只暴露 80 端口)
                    stage('Deploying xcx1-gateway') {
                        echo "========== 开始部署服务: xcx1-gateway =========="

                        sh """
                            echo " 更新 xcx1-gateway 镜像版本..."
                            sed -i 's|xcx1-gateway:latest|xcx1-gateway:${VERSION}|g' ./xcx1-gateway/k8s-deploy.yaml

                            echo " 应用 K8s 部署配置..."
                            kubectl apply -f ./xcx1-gateway/k8s-deploy.yaml

                            echo " 配置环境变量 (对应原 docker run -e)..."
                            kubectl set env deployment/xcx1-gateway \
                                NACOS_ADDR=host.docker.internal:8848 \
                                JWT_SECRET=defaultSecretKeyForJWTTokensMustBeLongEnough2024

                            echo " 等待 Pod 就绪..."
                            kubectl rollout status deployment/xcx1-gateway --timeout=120s || true
                        """

                        echo "========== 服务 xcx1-gateway 部署完成 (仅暴露80端口) =========="
                    }
                }
            }
        }

        stage(' 健康检查') {
            steps {
                sh '''
                    echo " 等待 K8s 服务启动..."
                    sleep 15

                    echo " 检查 Pod 状态..."
                    kubectl get pods

                    echo " 检查 Pod 详细状态..."
                    kubectl describe pods

                    echo " 检查网关登录接口..."
                    curl -f http://localhost:80/auth/login/login -X POST \
                      -H "Content-Type: application/json" \
                      -d '{"username":"health","password":"check"}' || echo "网关未就绪"

                    echo " 检查业务系统接口..."
                    curl -f http://localhost:80/api/category/getAll || echo "业务系统未就绪"
                '''
            }
        }
    }

    post {
        success {
            echo "✅ 构建成功，版本：${VERSION}"
            echo " 网关 (唯一入口): http://localhost:80"
            echo " 后端服务端口已隐藏 (xcx1-auth:3004, system-a:3005)"
        }
        failure {
            echo "❌ 构建失败，请检查日志"
            sh 'kubectl logs --tail=100 deployment/xcx1-auth || true'
            sh 'kubectl logs --tail=100 deployment/system-a || true'
            sh 'kubectl logs --tail=100 deployment/xcx1-gateway || true'
            sh 'kubectl get pods || true'
        }
        always {
            cleanWs()
        }
    }
}