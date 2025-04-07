package org.example.mailservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.mailservice.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RequiredArgsConstructor
@RestController
public class MailController {
    private final MailService mailService;

    @PostMapping("/send/")
    public ResponseEntity<Void> sendMail (@RequestParam final Long id,
                                          @RequestParam final String email) throws MessagingException {
        mailService.SendMessageInMail(email, id);
        return ResponseEntity.ok().build();
    }
}
