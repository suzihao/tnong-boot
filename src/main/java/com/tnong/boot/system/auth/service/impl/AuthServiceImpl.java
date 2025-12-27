package com.tnong.boot.system.auth.service.impl;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.util.JwtUtil;
import com.tnong.boot.common.util.PasswordUtil;
import com.tnong.boot.common.util.SnowflakeIdGenerator;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.auth.domain.dto.LoginDTO;
import com.tnong.boot.system.auth.domain.dto.WecomUserInfo;
import com.tnong.boot.system.auth.domain.vo.LoginVO;
import com.tnong.boot.system.auth.service.AuthService;
import com.tnong.boot.system.log.domain.entity.SysLoginLog;
import com.tnong.boot.system.log.mapper.SysLoginLogMapper;
import com.tnong.boot.system.tenant.domain.entity.SysTenant;
import com.tnong.boot.system.tenant.mapper.SysTenantMapper;
import com.tnong.boot.system.user.domain.entity.SysUser;
import com.tnong.boot.system.user.domain.entity.SysUserThirdAccount;
import com.tnong.boot.system.user.mapper.SysUserMapper;
import com.tnong.boot.system.user.mapper.SysUserThirdAccountMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysTenantMapper sysTenantMapper;
    private final SysLoginLogMapper sysLoginLogMapper;
    private final SysUserThirdAccountMapper sysUserThirdAccountMapper;

    @Value("${wecom.corp-id:}")
    private String wecomCorpId;

    @Value("${wecom.agent-id:}")
    private String wecomAgentId;

    @Value("${wecom.secret:}")
    private String wecomSecret;

    @Value("${wecom.redirect-uri:http://localhost:8080/api/auth/wecom/callback}")
    private String wecomRedirectUri;

    @Override
    public LoginVO login(LoginDTO loginDTO, HttpServletRequest request) {
        // 1. 参数校验
        if (!StringUtils.hasText(loginDTO.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(loginDTO.getPassword())) {
            throw new BusinessException("密码不能为空");
        }

        // 2. 获取租户ID
        Long tenantId = getTenantId(loginDTO.getTenantId());
        
        // 3. 查询用户
        SysUser user = sysUserMapper.selectByUsername(loginDTO.getUsername(), tenantId);
        
        // 4. 验证用户
        if (user == null) {
            recordLoginLog(null, tenantId, loginDTO.getUsername(), 0, "用户不存在", request);
            throw new BusinessException("用户名或密码错误");
        }
        
        // 5. 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            recordLoginLog(user.getId(), tenantId, loginDTO.getUsername(), 0, "密码错误", request);
            throw new BusinessException("用户名或密码错误");
        }
        
        // 6. 检查用户状态
        if (!CommonConstant.STATUS_ENABLE.equals(user.getStatus())) {
            recordLoginLog(user.getId(), tenantId, loginDTO.getUsername(), 0, "用户已被禁用", request);
            throw new BusinessException("用户已被禁用");
        }
        
        // 7. 生成 Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), tenantId);
        
        // 8. 更新最后登录信息
        updateLastLogin(user, request);
        
        // 9. 记录登录成功日志
        recordLoginLog(user.getId(), tenantId, loginDTO.getUsername(), 1, "登录成功", request);
        
        // 10. 构造返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setTenantId(tenantId);
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        
        return loginVO;
    }

    @Override
    public LoginVO wecomLogin(String code, String state, HttpServletRequest request) {
        // 1. 获取租户ID（当前阶段默认使用 DEFAULT 租户）
        Long tenantId = getTenantId(null);

        // 2. 从企业微信获取用户信息（后续接入真实接口）
        WecomUserInfo wecomUser = getWecomUserInfoByCode(code);
        if (wecomUser == null || !StringUtils.hasText(wecomUser.getUserId())) {
            throw new BusinessException("企业微信用户信息获取失败");
        }

        // 3. 先看是否已经绑定
        SysUser user = null;
        SysUserThirdAccount bind = sysUserThirdAccountMapper
                .selectByTypeAndThirdUserId("wecom", wecomUser.getUserId());
        if (bind != null) {
            // 已绑定，直接获取用户
            user = sysUserMapper.selectById(bind.getUserId(), tenantId);
        } else {
            // 4. 未绑定时，先按 username 匹配（wecom_{企业微信userId}）
            String wecomUsername = "wecom_" + wecomUser.getUserId();
            user = sysUserMapper.selectByUsername(wecomUsername, tenantId);
            
            // 5. 如果还是没找到，尝试按手机号匹配
            if (user == null && StringUtils.hasText(wecomUser.getMobile())) {
                user = sysUserMapper.selectByMobile(wecomUser.getMobile(), tenantId);
            }

            // 6. 如果还是没找到，自动创建用户
            if (user == null) {
                user = createUserFromWecom(wecomUser, tenantId);
                log.info("企业微信登录：自动创建用户 userId={}, wecomUserId={}", 
                        user.getUserId(), wecomUser.getUserId());
            }

            // 7. 创建绑定关系
            SysUserThirdAccount account = new SysUserThirdAccount();
            account.setUserId(user.getUserId());
            account.setType("wecom");
            account.setThirdUserId(wecomUser.getUserId());
            account.setThirdUnionId(wecomUser.getUnionId());
            account.setExtraInfo(null);
            sysUserThirdAccountMapper.insert(account);
        }

        // 8. 检查用户状态
        if (!CommonConstant.STATUS_ENABLE.equals(user.getStatus())) {
            recordLoginLog(user.getUserId(), tenantId, user.getUsername(), 0, "用户已被禁用", request);
            throw new BusinessException("用户已被禁用");
        }

        // 9. 生成 Token
        String token = JwtUtil.generateToken(user.getUserId(), user.getUsername(), tenantId);

        // 10. 更新最后登录信息
        updateLastLogin(user, request);

        // 11. 记录登录成功日志（此处暂用同一日志类型）
        recordLoginLog(user.getUserId(), tenantId, user.getUsername(), 1, "企业微信登录成功", request);

        // 12. 构造返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getUserId());
        loginVO.setTenantId(tenantId);
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());

        return loginVO;
    }

    /**
     * 通过企业微信 code 换取用户信息
     */
    private WecomUserInfo getWecomUserInfoByCode(String code) {
        // 1. 检查配置是否完整
        if (!StringUtils.hasText(wecomCorpId) || !StringUtils.hasText(wecomSecret)) {
            throw new BusinessException("企业微信配置未完成，请先在配置文件中设置 wecom.corp-id / wecom.secret");
        }

        // 2. 获取 access_token
        String tokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="
                + wecomCorpId + "&corpsecret=" + wecomSecret;

        HttpClient client = HttpClient.newHttpClient();
        try {
           HttpRequest tokenRequest = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(tokenUrl))
                    .GET()
                    .build();

            HttpResponse<String> tokenResponse = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
            String tokenBody = tokenResponse.body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode tokenNode = mapper.readTree(tokenBody);
            int errcode = tokenNode.path("errcode").asInt(0);
            if (errcode != 0) {
                String errmsg = tokenNode.path("errmsg").asText(null);
                throw new BusinessException("获取企业微信access_token失败:" + errmsg);
            }

            String accessToken = tokenNode.path("access_token").asText(null);

            // 3. 通过code换取userId
            String userInfoUrl = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token="
                    + accessToken + "&code=" + code;

            HttpRequest userReq = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(userInfoUrl))
                    .GET()
                    .build();

            HttpResponse<String> userResp = client.send(userReq, HttpResponse.BodyHandlers.ofString());
            JsonNode userNode = mapper.readTree(userResp.body());
            int userErr = userNode.path("errcode").asInt(0);
            if (userErr != 0) {
                String errmsg = userNode.path("errmsg").asText(null);
                throw new BusinessException("获取企业微信用户ID失败:" + errmsg);
            }

            String userId = userNode.path("userid").asText(null);
            if (!StringUtils.hasText(userId)) {
                throw new BusinessException("企业微信返回的userid为空");
            }

            // 4. 通过userId获取详细信息
            String detailUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token="
                    + accessToken + "&userid=" + userId;

            HttpRequest detailReq = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(detailUrl))
                    .GET()
                    .build();

            HttpResponse<String> detailResp = client.send(detailReq, HttpResponse.BodyHandlers.ofString());
            JsonNode detailNode = mapper.readTree(detailResp.body());
            int detailErr = detailNode.path("errcode").asInt(0);
            if (detailErr != 0) {
                String errmsg = detailNode.path("errmsg").asText(null);
                throw new BusinessException("获取企业微信用户详情失败:" + errmsg);
            }

            WecomUserInfo info = new WecomUserInfo();
            info.setUserId(userId);
            info.setName(detailNode.path("name").asText(null));
            info.setEmail(detailNode.path("email").asText(null));
            info.setUnionId(detailNode.path("unionid").asText(null));
            info.setPosition(detailNode.path("position").asText(null));
            
            // 从 extattr.attrs 中提取工号和手机号
            JsonNode extattr = detailNode.path("extattr");
            if (!extattr.isMissingNode() && extattr.has("attrs")) {
                JsonNode attrs = extattr.get("attrs");
                if (attrs.isArray()) {
                    for (JsonNode attr : attrs) {
                        String attrName = attr.path("name").asText("");
                        String attrValue = attr.path("value").asText(null);
                        
                        if ("工号".equals(attrName) && attrValue != null) {
                            info.setEmployeeId(attrValue);
                        } else if ("同步手机".equals(attrName) && attrValue != null) {
                            info.setMobile(attrValue);
                        }
                    }
                }
            }
            
            return info;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("调用企业微信接口异常:" + e.getMessage());
        }
    }

    @Override
    public void logout() {
        // 清除用户上下文
        UserContext.clear();
        // 可以在这里记录退出日志
        log.info("用户退出登录");
    }

    /**
     * 从企业微信用户信息创建系统用户
     */
    private SysUser createUserFromWecom(WecomUserInfo wecomUser, Long tenantId) {
        SysUser user = new SysUser();
        
        // 生成业务用户ID
        user.setUserId(SnowflakeIdGenerator.generateId());
        user.setTenantId(tenantId);
        
        // 基本信息：使用企业微信userId作为用户名
        user.setUsername("wecom_" + wecomUser.getUserId());
        // 设置一个随机密码（企业微信登录不需要密码）
        user.setPassword(PasswordUtil.encode("WecomAutoCreate@" + System.currentTimeMillis()));
        user.setNickname(StringUtils.hasText(wecomUser.getName()) ? wecomUser.getName() : "企业微信用户");
        user.setMobile(wecomUser.getMobile());
        user.setEmail(wecomUser.getEmail());
        
        // 默认值
        user.setStatus(CommonConstant.STATUS_ENABLE);
        user.setDeptId(null); // 默认没有部门，后续可以维护
        user.setAvatar(null);
        user.setRemark("企业微信登录自动创建");
        user.setCreatedUser(0L); // 系统自动创建
        user.setUpdatedUser(0L);
        
        // 插入数据库
        int rows = sysUserMapper.insert(user);
        if (rows <= 0) {
            throw new BusinessException("创建用户失败");
        }
        
        return user;
    }

    /**
     * 获取租户ID
     */
    private Long getTenantId(Long tenantId) {
        // 如果没有传租户ID，使用默认租户
        if (tenantId == null) {
            tenantId = 1L;
        }
        
        SysTenant tenant = sysTenantMapper.selectByTenantId(tenantId);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        
        if (!CommonConstant.STATUS_ENABLE.equals(tenant.getStatus())) {
            throw new BusinessException("租户已被禁用");
        }
        
        return tenant.getId();
    }

    /**
     * 更新最后登录信息
     */
    private void updateLastLogin(SysUser user, HttpServletRequest request) {
        try {
            user.setLastLoginIp(getIpAddress(request));
            // 这里可以调用 Mapper 更新最后登录时间和IP
            // sysUserMapper.updateLastLogin(user);
        } catch (Exception e) {
            log.error("更新最后登录信息失败", e);
        }
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(Long userId, Long tenantId, String username, Integer status, String msg, HttpServletRequest request) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setTenantId(tenantId);
            loginLog.setUserId(userId);
            loginLog.setUsername(username);
            loginLog.setLoginType(0); // 0-账号密码登录
            loginLog.setStatus(status); // 1-成功，0-失败
            loginLog.setIpAddress(getIpAddress(request));
            loginLog.setUserAgent(request.getHeader("User-Agent"));
            loginLog.setMsg(msg);
            
            sysLoginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
