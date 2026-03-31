package kr.hs.sen.bangsan.boothwaiting.dto;

import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WaitingResisterRequest {

    private int studentID;

    private String name;

    private String phoneNumber;

    public Waiting toEntity() {
        return new Waiting(this.studentID, this.name, this.phoneNumber);
    }
}
