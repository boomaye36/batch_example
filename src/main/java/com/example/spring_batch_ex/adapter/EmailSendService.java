package com.example.spring_batch_ex.adapter;

import org.springframework.stereotype.Service;

@Service
public class EmailSendService implements SendService{
    @Override
    public void send(String email, String message) {
        System.out.println("email: " + email + "\nmessage: " + message);
    }
}
