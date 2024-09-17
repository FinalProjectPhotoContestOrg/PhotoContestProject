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

    public void sendEmailForRegister(String to, String username) {
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

    public void sendEmailForEnteringInContest(String to, String username, String contestName, String entryName) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "🎉 Congratulations, " + username + "! You've Entered the " + contestName + " Contest!";

            String text = "Dear " + username + ",\n\n" +
                    "We are thrilled to inform you that you have successfully entered the \"" + contestName + "\" contest with your stunning entry titled \"" + entryName + "\".\n\n" +
                    "Your passion for photography is truly inspiring, and we can’t wait to see how your entry captivates the judges and the community!\n\n" +
                    "### What’s Next?\n" +
                    "- **Stay Tuned**: Keep an eye on your email for contest updates and announcements. You never know, your entry might just win!\n" +
                    "- **Explore More**: Why stop here? Check out other exciting contests on PhotoPulse and keep submitting your amazing work.\n\n" +
                    "### Need Assistance?\n" +
                    "If you have any questions or need help with your submissions, feel free to reach out to our support team.\n\n" +
                    "Thank you for being part of the PhotoPulse community. We wish you the best of luck in the contest!\n\n" +
                    "Happy Clicking!\n\n" +
                    "Warm regards,\n\n" +
                    "Stefan and Todor\n" +
                    "The PhotoPulse Team\n";

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPasswordResetEmail(String to, String username, String resetLink) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String subject = "Password Reset Request for PhotoPulse";

            String htmlText = "<div style=\"color: black; font-family: Arial, sans-serif;\">" +
                    "<p style=\"color: black\">Hello " + username + ",</p>" +
                    "<p style=\"color: black\">We received a request to reset your password for your PhotoPulse account. If you made this request, please click on the link below to reset your password:</p>" +
                    "<p style=\"color: black\"><a href=\"" + resetLink + "\" style=\"color: black; text-decoration: none; font-weight: bold;\">Reset Password</a></p>" +
                    "<p style=\"color: black\">If you did not request a password reset, you can safely ignore this email. Your password will not be changed.</p>" +
                    "<p style=\"color: black\">Best regards,</p>" +
                    "<p style=\"color: black\">Todor and Stefan<br/>PhotoPulse Team</p>" +
                    "</div>";


            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlText, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

}
