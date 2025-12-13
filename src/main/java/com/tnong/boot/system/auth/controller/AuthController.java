package com.tnong.boot.system.auth.controller;

import com.tnong.boot.common.util.PasswordUtil;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.system.auth.domain.dto.LoginDTO;
import com.tnong.boot.system.auth.domain.vo.LoginVO;
import com.tnong.boot.system.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        LoginVO loginVO = authService.login(loginDTO, request);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 用户退出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success("退出成功", null);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<LoginVO> getUserInfo() {
        // TODO: 从 UserContext 获取当前用户信息
        return Result.success();
    }

    /**
     * 测试接口：生成加密密码
     */
    @GetMapping("/encode-password")
    public Result<String> encodePassword(@RequestParam String password) {
        String encoded = PasswordUtil.encode(password);
        return Result.success("加密成功", encoded);
    }
}
