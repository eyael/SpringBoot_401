package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String index(Model model) {

        model.addAttribute("message", messageRepository.findAll());

        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("message", new Message());
        model.addAttribute("user", new User());

        return "registration";
    }

    @PostMapping ("/register")
    public String processRegistrationPage (@Valid
    @ModelAttribute("user") User user, BindingResult result,Model model) {
        model.addAttribute("message", new Message());

        model.addAttribute("user", user);
        if (result.hasErrors()) {
            return "registration";
        } else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "index";

    }
        @GetMapping("/add")
        public String messageForm(Model model){
            model.addAttribute("user", new User());
            model.addAttribute("message",  new Message());
            return "messageform";
        }


    @PostMapping("/process")
    public String processForm(@Valid
                              @ModelAttribute Message message, BindingResult result,@RequestParam("file") MultipartFile file ) {

        if (file.isEmpty()) {
            return "redirect:/add";
        }
        if (result.hasErrors()){
            return "messageform";
        }
        messageRepository.save(message);
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            message.setHeadshot(uploadResult.get("url").toString());
            messageRepository.save(message);
        }catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }
        return "redirect:/";


    }
    @RequestMapping("/login")
    public String login() {
        return "login";
    }

        @RequestMapping("/secure")
    public String secure(Principal principal, Model model){
        User myuser = ((CustomUserDetails)
                ((UsernamePasswordAuthenticationToken) principal)
                        .getPrincipal()).getUser();
        model.addAttribute("myuser",myuser);
        return "secure";
    }
    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model){

        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }

    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/";
    }



}

