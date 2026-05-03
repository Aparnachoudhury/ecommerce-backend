package com.aparna.ecommerce.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    public void sendEmail(String to, String subject, String body) {
        try {
            Email from = new Email("your_verified_email@domain.com"); // ⚠️ MUST be verified in SendGrid
            Email toEmail = new Email(to);

            Content content = new Content("text/html", body);

            Mail mail = new Mail(from, subject, toEmail, content);

            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("📧 Email status: " + response.getStatusCode());

        } catch (Exception e) {
            System.out.println("❌ Email failed: " + e.getMessage());
        }
    }
}