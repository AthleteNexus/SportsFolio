//package com.tech;
//
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import java.util.Properties;
//
//public class SMTPConnectionTest {
//    public static void main(String[] args) {
//        String host = System.getenv("MAIL_HOST") != null ? System.getenv("MAIL_HOST") : "smtp.gmail.com";
//        int port = System.getenv("MAIL_PORT") != null ? Integer.parseInt(System.getenv("MAIL_PORT")) : 587;
//        String username = System.getenv("MAIL_USERNAME");
//        String password = System.getenv("MAIL_PASSWORD");
//
//        if (username == null || password == null) {
//            System.out.println("❌ MAIL_USERNAME or MAIL_PASSWORD not set");
//            System.exit(1);
//        }
//
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost(host);
//        mailSender.setPort(port);
//        mailSender.setUsername(username);
//        mailSender.setPassword(password);
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.starttls.required", "true");
//        props.put("mail.smtp.connectiontimeout", "5000");
//        props.put("mail.smtp.timeout", "5000");
//
//        try {
//            mailSender.testConnection();
//            System.out.println("✅ SMTP Connection Successful!");
//            System.out.println("   Host: " + host);
//            System.out.println("   Port: " + port);
//            System.out.println("   Username: " + username);
//        } catch (Exception e) {
//            System.out.println("❌ SMTP Connection Failed!");
//            System.out.println("   Error: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }
//}
