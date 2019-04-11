package com.github.hongkaiwen.login2f.controller;

import com.github.hongkaiwen.login2f.store.BindInfoStore;
import com.github.hongkaiwen.login2f.util.GoogleUtil;
import com.google.zxing.WriterException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BindController {

    @Autowired
    private BindInfoStore bindInfoStore;

    @GetMapping("/bind")
    public void bind(HttpServletRequest request, HttpServletResponse response) throws IOException, WriterException {
        HttpSession session = request.getSession();
        if (session == null){
            throw new RuntimeException("not login");
        }
        Object user = session.getAttribute(LoginController.LOGIN_KEY);
        if(StringUtils.isEmpty(user)){
            throw new RuntimeException("not login");
        }
        String seed = bindInfoStore.bind(user.toString());
        String google = GoogleUtil.getGoogleAuthenticatorBarCode(seed, GoogleUtil.SYSTEM_ACCOUNT, GoogleUtil.ISSUER);

        GoogleUtil.createQRCode(google, 400, 400, response.getOutputStream());


    }

}
