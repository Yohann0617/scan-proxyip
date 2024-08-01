import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: scan-proxyip
 * @package: PACKAGE_NAME
 * @className: Test
 * @author: Yohann
 * @date: 2024/8/1 1:07
 */
public class Test {
    public static void main(String[] args) {
        // 设置 token 载荷
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", "123456");
        payload.put("username", "exampleUser");
        payload.put("role", "user");
        byte[] key = "secretKey".getBytes();
        // 使用 JWTUtil 创建 token
        String token = JWTUtil.createToken(payload, key);

        System.out.println("Generated Token: " + token);

        JWT jwt = JWTUtil.parseToken(token);
        System.out.println((String) jwt.getPayload("userId"));

        boolean verify = JWTUtil.verify(token, key);
        System.out.println(verify);
    }
}
