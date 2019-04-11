package com.github.hongkaiwen.login2f.controller;


import com.github.hongkaiwen.login2f.form.LoginForm;
import com.github.hongkaiwen.login2f.store.BindInfoStore;
import com.github.hongkaiwen.login2f.totp.TOTP;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private BindInfoStore bindInfoStore;

    public static final String LOGIN_KEY = "_user_";

    @PostMapping("/login")
    public String login(@RequestBody LoginForm loginForm, HttpServletRequest request){
        if(!loginForm.getLoginName().equals(loginForm.getPassword())){
            throw new RuntimeException("password or login name wrong");
        }
        String seed = bindInfoStore.get(loginForm.getLoginName());
        if(StringUtils.isEmpty(seed)){
            doLogin(request, loginForm.getLoginName());
            return "login success";
        }

        String target = getTOTPCode(seed);

        if (!target.equals(loginForm.getFactor())){
            return "login failed";
        }

        doLogin(request, loginForm.getLoginName());
        return "login success";
    }

    private void doLogin(HttpServletRequest request, String loginName){
        HttpSession session = request.getSession(true);
        session.setAttribute(LOGIN_KEY, loginName);
    }

    public static String getTOTPCode(String secretKey) {
        String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(normalizedBase32Key);
        String hexKey = Hex.encodeHexString(bytes);
        long time = (System.currentTimeMillis() / 1000) / 30;
        String hexTime = Long.toHexString(time);
        return TOTP.generateTOTP(hexKey, hexTime, "6");
    }

}
