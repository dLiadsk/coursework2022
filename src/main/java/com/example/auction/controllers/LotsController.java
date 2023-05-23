package com.example.auction.controllers;

import com.example.auction.models.Lot;
import com.example.auction.models.User;
import com.example.auction.services.LotService;
import com.example.auction.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
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
    public String toCreatingLot(Principal principal, RedirectAttributes redirectAttributes) {
        if (lotService.getUserByPrincipal(principal) == null) {
            redirectAttributes.addFlashAttribute("error", "Для створення лоту ви повинні авторизуватися");
            return "redirect:/";
        }
        return "createLot";
    }

    @PostMapping("/lot/create")
    public String creatingLot(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2, @RequestParam("file3") MultipartFile file3, Lot lot, Principal principal, RedirectAttributes redirectAttributes) throws IOException {
        lotService.saveLot(principal, lot, file1, file2, file3);
        redirectAttributes.addFlashAttribute("success", "Лот успішно створено");
        return "redirect:/";
    }

    @PostMapping("/lot/makeRate/{id}")
    public String makeNewRate(@PathVariable long id, @RequestParam double newRate, @RequestParam long idOfWinner, RedirectAttributes redirectAttributes) {
        Lot lot = lotService.getLotById(id);
        if (newRate > lot.getRate()) {
            lotService.makeRate(newRate, lot, idOfWinner);
            redirectAttributes.addFlashAttribute("success", "Ставка зроблена");
        } else redirectAttributes.addFlashAttribute("error", "Ви не можете зробити ставку, нижчу ніж поточна");
        return "redirect:/lot/{id}";
    }

    @GetMapping("/lot/edit/{id}")
    public String toEditLot(Model model, @PathVariable long id, Principal principal) {
        Lot lot = lotService.getLotById(id);
        if (lotService.getUserByPrincipal(principal).equals(lot.getUser())) {
            model.addAttribute("lot", lot);
            return "edit";
        }
        return "redirect:/";
    }

    @PostMapping("/lot/edit/{id}")
    public String editLot(RedirectAttributes redirectAttributes, @PathVariable long id, Lot lot) {
        lotService.editLot(lotService.getLotById(id), lot);
        redirectAttributes.addFlashAttribute("success", "Лот успішно змінено");
        return "redirect:/lot/{id}";
    }

    @GetMapping("/lot/activity/{id}")
    public String startOrStopLot(Principal principal, @PathVariable long id, RedirectAttributes redirectAttributes) {
        Lot lot = lotService.getLotById(id);
        if (lotService.getUserByPrincipal(principal).getUserId().equals(lot.getUser().getUserId())) {
            lotService.setActivity(lot);
            if (lot.isActive()) redirectAttributes.addFlashAttribute("success", "Торги розпочато");
            else redirectAttributes.addFlashAttribute("success", "Торги зупинено");
            return "redirect:/lot/{id}";
        }
        return "redirect:/";
    }

    @GetMapping("/lot/delete/{id}")
    public String deleteLot(Principal principal, @PathVariable long id, RedirectAttributes redirectAttributes) {
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
