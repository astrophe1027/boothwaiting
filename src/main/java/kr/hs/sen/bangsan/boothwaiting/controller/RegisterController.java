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

import java.util.Map;

@Controller
public class RegisterController {

    @Autowired
    private WaitingService waitingService;
    @Autowired
    private AccountService accountService;

    @PostMapping(path = "/api/waiting")
    public synchronized ResponseEntity<Map<String, String>> waitingRegister(@RequestBody @Valid WaitingRegisterRequest waitingRegisterRequest, Model model) {
        WaitingRegisterResponse response = waitingService.registerWaiting(waitingRegisterRequest);

        if (response.getId() == -1) {
            throw new IllegalArgumentException(response.getMessage());
        }

        int studentId = Integer.parseInt(waitingRegisterRequest.getStudentId());
        if(accountService.isWaiting(studentId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<String> handleException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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
