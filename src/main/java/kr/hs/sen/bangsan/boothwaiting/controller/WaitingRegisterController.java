package kr.hs.sen.bangsan.boothwaiting.controller;

import ch.qos.logback.core.model.Model;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingResisterRequest;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WaitingRegisterController {
    @Autowired
    WaitingService waitingService;

    @ResponseBody
    @PostMapping(path = "/api/waiting")
    public /*ResponseEntity<WaitingRegisterResponse>*/String waitingRegister(WaitingResisterRequest waitingResisterRequest) {
        WaitingRegisterResponse response = waitingService.registerWaiting(waitingResisterRequest);
        /*
        if(response.getId() == -1){
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok().body(response);
        }
        */
        return "<div class='pico-color-green'>" + response.getMessage() + "</div>";

    }

    @GetMapping(path = "/register")
    public String register(Model model) {
        return "register";
    }
}
