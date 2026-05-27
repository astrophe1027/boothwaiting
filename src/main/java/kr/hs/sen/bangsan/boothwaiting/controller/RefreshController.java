package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.service.AccountService;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RefreshController {

    @Autowired
    WaitingService waitingService;
    @Autowired
    AccountService accountService;

    @GetMapping(path = "/api/waiting")
    public String waitingRefresh(Model model, @RequestParam(value = "token", defaultValue = "0") String token) {
        model.addAttribute("number", waitingService.checkWaiting(waitingService.getStudentIdByToken(token)).getNumber());
        return "waitingCheck :: refresh-response";
    }
}
