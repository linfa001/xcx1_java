@echo off
chcp 65001 >nul
echo ============================================
echo   检查 Nacos 服务注册详情
echo ============================================
echo.

echo 正在检查 xcx1-auth 服务详情...
echo.
curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=xcx1-auth" | findstr "ip" 
echo.

echo ============================================
echo 正在检查 system-a 服务详情...
echo.
curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=system-a" | findstr "ip"
echo.

echo ============================================
echo 正在检查 xcx1-gateway 服务详情...
echo.
curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=xcx1-gateway" | findstr "ip"
echo.

echo ============================================
echo 查看网关容器日志（最近50行）
echo.
docker logs xcx1-gateway --tail 50
echo.

pause