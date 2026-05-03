package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterViewController {

    @Autowired
    private WaitingService waitingService;

    @GetMapping(path = "/register")
    public String register() {
        return "register";
    }
}
