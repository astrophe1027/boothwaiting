package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingResisterRequest;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class WaitingRegisterController {
    @Autowired
    WaitingService waitingService;

    @PostMapping(path = "/api/waiting")
    public ResponseEntity<WaitingRegisterResponse> waitingRegister(@RequestBody WaitingResisterRequest waitingResisterRequest) {
        WaitingRegisterResponse response = waitingService.registerWaiting(waitingResisterRequest);
        if(response.getId() != -1){
            return ResponseEntity.badRequest().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }
}
