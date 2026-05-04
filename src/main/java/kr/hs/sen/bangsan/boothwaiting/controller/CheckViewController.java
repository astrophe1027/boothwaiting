package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.dto.WaitingNumberCheckResponse;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckViewController {

    @Autowired
    private WaitingService waitingService;

    @GetMapping(path = "/check")
    public String check(Model model, @RequestParam(value = "token", required = false, defaultValue = "0")  String token) {
        int studentId = waitingService.getStudentIdByToken(token);

        WaitingNumberCheckResponse waitingCheckResponse = waitingService.checkWaiting(studentId);
        model.addAttribute("message", waitingCheckResponse.getMessage());
        model.addAttribute("number", waitingService.checkWaiting(studentId).getNumber());
        model.addAttribute("id", waitingService.getIdByStudentId(studentId));
        model.addAttribute("url", "localhost:8080/check?token=" + token);
        return "waitingCheck";
    }
}
