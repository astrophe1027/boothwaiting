package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.service.AccountService;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CancelController {

    @Autowired
    WaitingService waitingService;
    @Autowired
    AccountService accountService;

    @DeleteMapping(path = "/api/waiting")
    public synchronized ResponseEntity<Map<String, String>> waitingRefresh(@RequestParam(value = "token", defaultValue = "") String token) {
        /*
        Map<String, String> response = new HashMap<>();
        response.put("message", waitingService.cancelWaiting(waitingService.getStudentIdByToken(token)));
        return ResponseEntity.ok(response);
         */
        return ResponseEntity.badRequest().build();
        //TODO: 관리자가 관리하는 새로운 취소 시스템 제작
    }
}
