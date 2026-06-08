package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.service.AccountService;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ExitController {

    @Autowired
    private AccountService accountService;

    @Value("${booth.client-password}")
    private String clientPassword;

    @DeleteMapping(path = "/api/entry")
    public ResponseEntity<Map<String, String>> exit(@RequestParam(name = "studentId", defaultValue = "0") int studentId, @RequestHeader(value = "X-Client-Password", defaultValue = "") String password) {
        if (!clientPassword.equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "인증되지 않은 요청입니다."));
        }
        return ResponseEntity.ok(Map.of("message", accountService.exit(studentId)));
    }
}