package kr.hs.sen.bangsan.boothwaiting.controller;

import ch.qos.logback.core.model.Model;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterRequest;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

@Controller
public class WaitingRegisterController {
    @Autowired
    private WaitingService waitingService;

    @ResponseBody
    @PostMapping(path = "/api/waiting")
    public /*ResponseEntity<WaitingRegisterResponse>*/String waitingRegister(WaitingRegisterRequest waitingRegisterRequest) {
        WaitingRegisterResponse response = waitingService.registerWaiting(waitingRegisterRequest);
        /*
        if(response.getId() == -1){
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok().body(response);
        }
        */
        //return "<div class='pico-color-green'>" + response.getMessage() + "</div>";

        //TODO: 타임리프 좀 쓰자
        if(Objects.equals(response.getMessage(), "등록되었습니다.")) {
            return "<ins>" + response.getMessage() + "</ins>\n" +
                    "<script>\n" +
                    "   alert(\"" + response.getMessage() + "\")\n" +
                    "</script>";
        } else {
            return "<mark>" + response.getMessage() + "</mark>\n" +
                    "<script>\n" +
                    "   alert(\"" + response.getMessage() + "\")\n" +
                    "</script>";
        }
    }

    @GetMapping(path = "/register")
    public String register(Model model) {
        return "register";
    }
}
