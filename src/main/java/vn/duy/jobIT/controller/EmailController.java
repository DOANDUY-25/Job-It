package vn.duy.jobIT.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.duy.jobIT.service.EmailService;
import vn.duy.jobIT.service.SubscriberService;
import vn.duy.jobIT.util.annotation.ApiMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${apiPrefix}/email")
public class EmailController {
    private final SubscriberService subscriberService;

    @GetMapping("")
    @ApiMessage("Send email")
    @Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public ResponseEntity<String> sendEmail(){
        this.subscriberService.sendSubscribersEmailJobs();
        return ResponseEntity.ok().body("Complete automatic email sending");
    }
}
