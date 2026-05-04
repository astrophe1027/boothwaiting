package kr.hs.sen.bangsan.boothwaiting.controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterRequest;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterResponse;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @Autowired
    private WaitingService waitingService;

    @PostMapping(path = "/api/waiting")
    public /*ResponseEntity<WaitingRegisterResponse>*/String waitingRegister(WaitingRegisterRequest waitingRegisterRequest, Model model, HttpServletResponse httpResponse) {
        WaitingRegisterResponse response = waitingService.registerWaiting(waitingRegisterRequest);
        /*
        if(response.getId() == -1){
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok().body(response);
        }
        */

        //return "<div class='pico-color-green'>" + response.getMessage() + "</div>";

        model.addAttribute("id", response.getId());
        model.addAttribute("message", response.getMessage());
        model.addAttribute("number", waitingService.checkWaiting(waitingRegisterRequest.getStudentId()).getNumber());
        model.addAttribute("url", "localhost:8080/check?token=" + waitingService.getToken(waitingRegisterRequest.getStudentId()));

        httpResponse.setHeader("HX-Push-Url", "http://localhost:8080/check?token=" + waitingService.getToken(waitingRegisterRequest.getStudentId()));

        return "waitingCheck :: register-response";
    }

}
