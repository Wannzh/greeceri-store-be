package com.greeceri.store.services;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    public void sendEmail(String toEmail, String subject, String body);  
}