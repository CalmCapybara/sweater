package com.example.sweater.controller;

import com.example.sweater.Entity.User;
import com.example.sweater.Entity.dto.CaptchaResponseDto;
import com.example.sweater.service.UserService;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    @Value("${recaptha.URL}")
    private String CAPTCHA_URL;

    @Autowired
    private UserService userService;

    @Value("${recaptcha.secret}")
    private String secret;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam("g-recaptcha-response") String recaptchaResponse,
            @RequestParam("password2") String passwordConfirm,
            @Valid User user,
            BindingResult bindingResult,
            Model model) {


        String URL = String.format(CAPTCHA_URL, secret, recaptchaResponse);
        CaptchaResponseDto captchaResponseDto = restTemplate.postForObject(URL, Collections.EMPTY_LIST, CaptchaResponseDto.class);

        boolean successCaptcha = captchaResponseDto.isSuccess();
        if(!successCaptcha) {
            model.addAttribute("CaptchaError", "Подтвердите что вы не робот");
        }

        boolean password2Empty = ObjectUtils.isEmpty(passwordConfirm);
        if (password2Empty) {
            model.addAttribute("password2Error", "Введите повторый пароль");
        }

        if(user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "разные пароли");
        }
        if(password2Empty || bindingResult.hasErrors() || !successCaptcha) {
            Map<String, String> errors = UtillsController.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }


        if (!userService.addUser(user)) {
            model.addAttribute("usernameError", "User exist!");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        String errorMessage = "Неверное имя пользователя или пароль";
        model.addAttribute("errorMessage", errorMessage);
        return "login";
    }

    @GetMapping("activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);
        if (isActivated) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code is not found");
        }

        return "login";
    }

}
