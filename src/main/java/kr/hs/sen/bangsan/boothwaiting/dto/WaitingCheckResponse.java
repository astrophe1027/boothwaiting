package kr.hs.sen.bangsan.boothwaiting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WaitingCheckResponse {
    private int number;
    private String message;
}
