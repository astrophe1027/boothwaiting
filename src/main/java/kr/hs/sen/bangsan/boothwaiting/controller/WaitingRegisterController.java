package kr.hs.sen.bangsan.boothwaiting.controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingCheckResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterRequest;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WaitingRegisterController {
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
        model.addAttribute("url", "localhost:8080/register?studentId=" + waitingRegisterRequest.getStudentId());

        httpResponse.setHeader("HX-Push-Url", "?studentId=" + waitingRegisterRequest.getStudentId());
        /*
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
        */

        return "waitingCheck :: registerResponse";
    }

    @GetMapping(path = "/register")
    public String register(Model model, @RequestParam(value = "studentId", required = false, defaultValue = "0")  int studentId) {
        if (studentId == 0) {
            return "register";
        } else {
            WaitingCheckResponse waitingCheckResponse = waitingService.checkWaiting(studentId);
            model.addAttribute("message", waitingCheckResponse.getMessage());
            model.addAttribute("number", waitingService.checkWaiting(studentId).getNumber());
            model.addAttribute("id", waitingService.getIdByStudentId(studentId));
            model.addAttribute("url", "localhost:8080/register?studentId=" + studentId);
            return "waitingCheck";
        }
    }
}
