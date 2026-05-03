package kr.hs.sen.bangsan.boothwaiting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountCheckResponse {
    private boolean exist;
    private String message;
}
