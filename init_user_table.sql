-- 用户表初始化 SQL
-- 执行前请确保已创建数据库

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用（1-启用，0-禁用）',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试用户
-- 密码使用 BCrypt 加密，加密后的值如下：
-- admin 用户，密码：123456
-- user 用户，密码：password

-- 注意：以下 BCrypt 哈希值需要使用程序生成，这里提供示例
-- 你可以在应用启动后通过 PasswordEncoder 生成新的哈希值

-- 方式1：使用应用生成密码（推荐）
-- 启动应用后，调用以下代码生成 BCrypt 密码：
-- BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
-- System.out.println(encoder.encode("123456"));  // admin 密码
-- System.out.println(encoder.encode("password")); // user 密码

-- 方式2：手动插入（需要先运行一次应用生成密码哈希）
-- 临时插入明文密码（首次登录会失败，需要重新生成 BCrypt 密码）
-- INSERT INTO user (username, password, enabled) VALUES
-- ('admin', '123456', 1),
-- ('user', 'password', 1);

-- 正确的方式是运行下面的 Java 代码生成 BCrypt 密码：
/*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("admin密码: " + encoder.encode("123456"));
        System.out.println("user密码: " + encoder.encode("password"));
    }
}
*/

-- 生成后的 SQL 示例（请替换为实际生成的哈希值）：
-- INSERT INTO user (username, password, enabled) VALUES
-- ('admin', '$2a$10$YourGeneratedHashFor123456...', 1),
-- ('user', '$2a$10$YourGeneratedHashForPassword...', 1);
