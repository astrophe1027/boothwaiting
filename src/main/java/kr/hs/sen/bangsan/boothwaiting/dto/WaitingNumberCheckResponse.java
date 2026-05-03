package kr.hs.sen.bangsan.boothwaiting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WaitingNumberCheckResponse {
    private int number;
    private String message;
}
