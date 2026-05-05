package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.service.AccountService;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CancelController {

    @Autowired
    WaitingService waitingService;
    @Autowired
    AccountService accountService;

    @DeleteMapping(path = "/api/waiting")
    public String waitingRefresh(Model model, @RequestParam(value = "token", defaultValue = "") String token) {
        model.addAttribute("message", waitingService.cancelWaiting(waitingService.getStudentIdByToken(token)));
        return "responseParts :: cancel-response";
    }
}
