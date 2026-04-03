package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.dto.WaitingCheckResponse;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class waitingCheckController {
    @Autowired
    WaitingService waitingService;

    @GetMapping(path = "/api/waiting/{studentid}")
    public ResponseEntity<WaitingCheckResponse> waitingRegister(@PathVariable("studentid") int studentID) {
        int response = waitingService.checkWaiting(studentID);
        if(response == -1){
            return ResponseEntity.badRequest().body(new WaitingCheckResponse(response, "등록되지 않은 학번입니다."));
        } else return ResponseEntity.ok(new WaitingCheckResponse(response, "앞에서 대기중인 팀의 수를 정상적으로 불러왔습니다."));
    }
}
