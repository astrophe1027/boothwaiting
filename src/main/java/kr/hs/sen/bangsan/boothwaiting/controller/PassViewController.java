package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.dto.WaitingNumberCheckResponse;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.service.AccountService;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PassViewController {

    @Autowired
    private WaitingService waitingService;
    @Autowired
    private AccountService accountService;

    @GetMapping(path = "/pass")
    public String passView(Model model, @RequestParam(value = "token", required = false, defaultValue = "0")  String token) {
        int studentId = waitingService.getStudentIdByToken(token);

        model.addAttribute("id", accountService.isCalled(studentId) ? 0 : -1);
        model.addAttribute("token", token);

        return "entryBarcode";
    }
}
