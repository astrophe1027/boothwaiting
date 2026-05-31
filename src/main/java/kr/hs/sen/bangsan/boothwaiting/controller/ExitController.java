package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.service.AccountService;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ExitController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private WaitingService waitingService;

    @DeleteMapping(path="/api/entry")
    public ResponseEntity<Map<String, String>>  exit() {
        return ResponseEntity.ok(new HashMap<String, String>());
    }
}
