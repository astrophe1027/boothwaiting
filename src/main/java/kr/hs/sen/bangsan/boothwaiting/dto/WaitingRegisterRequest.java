package kr.hs.sen.bangsan.boothwaiting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WaitingRegisterRequest {


    @NotNull(message = "학번은 필수 입력 항목입니다.")
    @Pattern(regexp = "\\d{5}", message = "학번은 반드시 5자리 숫자여야 합니다.")
    private String studentId;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 5, message = "이름의 형식이 잘못되었습니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;

    public Waiting toEntity() {
        return new Waiting(Integer.parseInt(this.studentId), this.name, this.phoneNumber);
    }
}
