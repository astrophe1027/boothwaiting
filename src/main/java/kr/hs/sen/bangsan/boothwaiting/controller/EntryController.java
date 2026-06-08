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
public class EntryController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private WaitingService waitingService;

    @Value("${booth.client-password}")
    private String clientPassword;

    @PostMapping(path="/api/entry")
    @CrossOrigin(origins = "*")
    public synchronized ResponseEntity<Map<String, String>> enter(@RequestParam(value = "token", defaultValue = "") String token, @RequestHeader(value = "X-Client-Password", defaultValue = "") String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        if (!clientPassword.equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers)
                    .body(Map.of("message", "인증되지 않은 요청입니다."));
        }
        return new ResponseEntity<>(Map.of("message", accountService.enter(waitingService.getStudentIdByToken(token))), headers, HttpStatus.OK);
    }
}
