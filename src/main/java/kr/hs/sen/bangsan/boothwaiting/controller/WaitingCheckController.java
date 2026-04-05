package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.dto.WaitingCheckResponse;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class WaitingCheckController {
    @Autowired
    WaitingService waitingService;

    @GetMapping(path = "/api/waiting/{studentid}")
    public ResponseEntity<WaitingCheckResponse> waitingRegister(@PathVariable("studentid") int studentID) {
        WaitingCheckResponse response = waitingService.checkWaiting(studentID);
        if(response.getNumber() == -1){
            return ResponseEntity.badRequest().body(response);
        } else return ResponseEntity.ok(response);
    }
}
