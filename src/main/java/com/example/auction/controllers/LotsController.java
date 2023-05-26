package com.example.auction.controllers;

import com.example.auction.models.Lot;
import com.example.auction.models.User;
import com.example.auction.services.LotService;
import com.example.auction.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class LotsController {

    private final LotService lotService;

    private final UserService userService;

    @GetMapping("/")
    public String lot(@RequestParam(name = "keyWord", required = false) String keyWord, Model model, @ModelAttribute("error") String error, @ModelAttribute("success") String success) {
        List<Lot> lots = lotService.listOfLots(keyWord);
        model.addAttribute("lots", lots);
        model.addAttribute("error", error);
        model.addAttribute("success", success);
        return "main";
    }

    @GetMapping("/lot/{id}")
    public String lotDetails(Principal principal, Model model, @PathVariable long id, RedirectAttributes redirectAttributes, @ModelAttribute("error") String error, @ModelAttribute("success") String success) {
        Lot lot = lotService.getLotById(id);
        if (lot == null) {
            redirectAttributes.addFlashAttribute("error", "Лот не знайдено");
            return "redirect:/";
        }
        User user = lotService.getUserByPrincipal(principal);
        model.addAttribute("user", user);
        if (lot.getIdOfWinner() != 0) {
            model.addAttribute("winnerUser", userService.findUserById(lot.getIdOfWinner()));
        }
        model.addAttribute("lot", lot);
        String lotUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080")
                .path("/lot/" + id)
                .toUriString();
        model.addAttribute("URL", lotUrl);
        model.addAttribute("error", error);
        model.addAttribute("success", success);
        return "lot_details";
    }

    @GetMapping("/lot/create")
    public String toCreatingLot(Model model, Principal principal, RedirectAttributes redirectAttributes, @ModelAttribute("error") String error) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Для створення лоту ви повинні авторизуватися");
            return "redirect:/";
        }
        model.addAttribute("error", error);
        return "createLot";
    }

    @PostMapping("/lot/create")
    public String creatingLot(Lot lot, Principal principal, RedirectAttributes redirectAttributes) {
        if (lot.getTitleOfLot().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Вкажіть назву лота");
            return "redirect:/lot/create";
        }
        if (lot.getDescription().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Заповніть поле \"опис лота\"");
            return "redirect:/lot/create";
        }
        lotService.saveLot(principal, lot);
        redirectAttributes.addFlashAttribute("success", "Лот успішно створено");
        return "redirect:/";
    }

    @PostMapping("/lot/makeRate")
    public String makeNewRate(@RequestParam("id") long id, @RequestParam double newRate, @RequestParam long idOfWinner, RedirectAttributes redirectAttributes) {
        Lot lot = lotService.getLotById(id);
        if (newRate > lot.getRate()) {
            lotService.makeRate(newRate, lot, idOfWinner);
            redirectAttributes.addFlashAttribute("success", "Ставка зроблена");
        } else redirectAttributes.addFlashAttribute("error", "Ви не можете зробити ставку, нижчу ніж поточна");
        return "redirect:/lot/" + id;
    }

    @GetMapping("/lot/edit")
    public String toEditLot(Model model, @RequestParam long id) {
        Lot lot = lotService.getLotById(id);
        model.addAttribute("lot", lot);
        return "edit";


    }

    @PostMapping("/lot/edit")
    public String editLot(RedirectAttributes redirectAttributes, @RequestParam long id, Lot lot) {
        if (lot.getTitleOfLot().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Назва лота не може бути пустою");
            return "redirect:/lot/" + id;
        }
        if (lot.getDescription().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Опис лота не може бути пустим");
            return "redirect:/lot/" + id;
        }
        lotService.editLot(lotService.getLotById(id), lot);
        redirectAttributes.addFlashAttribute("success", "Лот успішно змінено");
        return "redirect:/lot/" + id;
    }

    @PostMapping("/lot/activity")
    public String startOrStopLot(Principal principal, @RequestParam long id, RedirectAttributes redirectAttributes) {
        Lot lot = lotService.getLotById(id);
        lotService.setActivity(lot);
        if (lot.isActive()) redirectAttributes.addFlashAttribute("success", "Торги розпочато");
        else redirectAttributes.addFlashAttribute("success", "Торги зупинено");
        return "redirect:/lot/" + id;

    }

    @PostMapping("/lot/delete")
    public String deleteLot(Principal principal, @RequestParam long id, RedirectAttributes redirectAttributes) {
        User user = lotService.getUserByPrincipal(principal);
        if (lotService.deleteLot(user, id)) redirectAttributes.addFlashAttribute("success", "Лот видалено");
        else redirectAttributes.addFlashAttribute("error", "Сталася якось помилка");
        return "redirect:/";
    }

    @GetMapping("/myLots")
    public String viewMyLots(Principal principal, Model model) {
        User user = lotService.getUserByPrincipal(principal);
        model.addAttribute("myLots", lotService.findMyLots(user));
        return "myLots";
    }
}
