package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin(origins = "*")
public class MonitorController {

    @Value("${booth.client-password}")
    private String clientPassword;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/api/monitor")
    public ResponseEntity<?> getMonitorData(
            @RequestHeader(value = "X-Client-Password", required = false) String requestPassword) {

        if (clientPassword != null && !clientPassword.isEmpty()) {
            if (requestPassword == null || !clientPassword.equals(requestPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("message", "인증에 실패했습니다. 비밀번호를 확인하세요."));
            }
        }

        return ResponseEntity.ok(buildMonitorData());
    }

    public void broadcastCurrentAccounts() {
        messagingTemplate.convertAndSend("/topic/monitor", Optional.of(buildMonitorData()));
    }

    private Map<String, Object> buildMonitorData() {
        List<Map<String, Object>> accounts = Stream.concat(Stream.concat(
                        accountRepository.findAllByStatus(Account.AccountStatus.ENTERED).stream(),
                        accountRepository.findAllByStatus(Account.AccountStatus.TEMPORARILY_EXIT).stream()
                ), accountRepository.findAllByStatus(Account.AccountStatus.CALLED).stream())
                .sorted(Comparator.comparing(Account::getId))
                .map(account -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("studentId", account.getStudentId());
                    m.put("name", account.getName());
                    m.put("status", account.getStatus().name());
                    return m;
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> waitings = waitingRepository
                .findAll(Sort.by("id"))
                .stream()
                .map(waiting -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("studentId", waiting.getStudentId());
                    m.put("name", waiting.getName());
                    m.put("phoneNumber", waiting.getPhoneNumber());
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("accounts", accounts);
        data.put("waitings", waitings);
        return data;
    }
}