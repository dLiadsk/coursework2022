package com.example.auction.controllers;

import com.example.auction.models.User;
import com.example.auction.services.LotService;
import com.example.auction.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.security.Principal;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LotService lotService;

    @GetMapping("/login")
    public String toLogin() {
        return "login";
    }


    @GetMapping("/sing_up")
    public String singUpForm() {
        return "sing_up";
    }

    @PostMapping("/sing_up")
    public String singUp(@RequestParam(name = "avatarFile") MultipartFile avatarFile, User user, Model model, RedirectAttributes redirectAttributes) throws IOException {
        if (!userService.crateUser(user, avatarFile)) {
            model.addAttribute("error", "Користувач з такою поштою вже існує.");
            return "sing_up";
        }
        redirectAttributes.addFlashAttribute("success", "Реєстрація пройшла успішно.");
        return "redirect:/";
    }

    @GetMapping("/user/{user}")
    public String userInfo(@PathVariable("user") User user, Model model, Principal principal) {
        model.addAttribute("userInf", user);
        if (Objects.equals(user.getUserId(), lotService.getUserByPrincipal(principal).getUserId()))
            return "redirect:/myAccount";
        model.addAttribute("lots", lotService.findMyLots(user));
        return "user-info";
    }

    @GetMapping("/myAccount")
    public String myAccount(Principal principal, Model model) {
        User user = lotService.getUserByPrincipal(principal);
        model.addAttribute("user", user);
        return "myAccount";
    }
}
