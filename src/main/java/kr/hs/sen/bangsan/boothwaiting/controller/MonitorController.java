package kr.hs.sen.bangsan.boothwaiting.controller;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin(origins = "*") // 로컬 파일(file://)에서 접근 허용
public class MonitorController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트 최초 연결 시 현재 상태 스냅샷
     * GET /api/monitor
     */
    @GetMapping("/api/monitor")
    public ResponseEntity<Map<String, Object>> getMonitorData() {
        return ResponseEntity.ok(buildMonitorData());
    }

    /**
     * 상태 변경 시 WebSocket으로 전체 브로드캐스트
     */
    public void broadcastCurrentAccounts() {
        messagingTemplate.convertAndSend("/topic/monitor", Optional.of(buildMonitorData()));
    }

    private Map<String, Object> buildMonitorData() {
        // 입장자 (ENTERED + TEMPORARILY_EXIT), id 순 정렬
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

        // 대기자, id(등록 순) 정렬
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