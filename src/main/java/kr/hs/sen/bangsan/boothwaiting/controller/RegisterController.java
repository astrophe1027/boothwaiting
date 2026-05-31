package kr.hs.sen.bangsan.boothwaiting.controller;

import jakarta.validation.Valid;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterRequest;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterResponse;
import kr.hs.sen.bangsan.boothwaiting.service.AccountService;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    @Autowired
    private WaitingService waitingService;
    @Autowired
    private AccountService accountService;

    @PostMapping(path = "/api/waiting")
    public /*ResponseEntity<WaitingRegisterResponse>*/String waitingRegister(@RequestBody @Valid WaitingRegisterRequest waitingRegisterRequest, Model model) {
        WaitingRegisterResponse response = waitingService.registerWaiting(waitingRegisterRequest);
        /*
        if(response.getId() == -1){
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok().body(response);
        }
        */

        //return "<div class='pico-color-green'>" + response.getMessage() + "</div>";

        if (response.getId() == -1) {
            throw new IllegalArgumentException(response.getMessage());
        }

        int studentId = Integer.parseInt(waitingRegisterRequest.getStudentId());
        if(accountService.isCalled(studentId)) {
            return "redirect:/pass?token=" + waitingService.getToken(studentId);
        }

        return "redirect:/check?token=" + waitingService.getToken(studentId);
       //return "waitingCheck";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(IllegalArgumentException e) {
        System.out.print(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<String> handleException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "입력값이 올바르지 않습니다.";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }
}
