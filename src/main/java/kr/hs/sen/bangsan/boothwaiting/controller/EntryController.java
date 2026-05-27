package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.service.AccountService;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(path="/api/entry")
    public ResponseEntity<Map<String, String>> enter(@RequestParam(value = "token", defaultValue = "") String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        Map<String, String> response = new HashMap<>();
        response.put("message", accountService.enter(waitingService.getStudentIdByToken(token)));
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
