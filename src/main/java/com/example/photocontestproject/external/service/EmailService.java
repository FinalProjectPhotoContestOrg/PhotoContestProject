package com.example.photocontestproject.external.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    public void sendEmail(String to, String username){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "Welcome to PhotoPulse – Start Exploring Photo Contests Today! \uD83D\uDCF8\n";
            String text = "Hello " + username + ",\n" +
                    "\n" +
                    "Thank you for registering at PhotoPulse! We’re excited to have you join our community of passionate photographers.\n\n" +
                    "### What Can You Do Next?\n" +
                    "Now that you’re a member, you can:\n" +
                    "- **Explore Contests**: Browse through a wide variety of photo contests, each with unique themes and prizes. Find the ones that inspire you the most and start participating.\n" +
                    "- **Submit Your Entries**: Ready to showcase your work? Enter your photos into contests with just a few clicks.\n" +
                    "- **Manage Your Profile**: Customize your profile. Keep track of your submissions, wins, and favorite contests.\n" +
                    "### Tips to Get Started\n" +
                    "- **Check Out the Featured Contests**: Don’t miss out on the latest and most popular contests on our platform.\n" +
                    "- **Invite Friends**: Know other photography enthusiasts? Invite them to join our vibrant community and participate in the contests.\n\n" +
                    "We’re thrilled to have you with us and can’t wait to see the incredible photos you’ll share.\n\n" +
                    "Happy photographing!\n\n" +
                    "Best regards,\n\n" +
                    "Stefan and Todor\n" +
                    "PhotoPulse Team\n";
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
