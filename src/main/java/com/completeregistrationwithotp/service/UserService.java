package com.completeregistrationwithotp.service;

import com.completeregistrationwithotp.dto.LoginDto;
import com.completeregistrationwithotp.dto.RegisterDto;
import com.completeregistrationwithotp.entity.User;
import com.completeregistrationwithotp.repository.UserRepository;
import com.completeregistrationwithotp.util.EmailUtil;
import com.completeregistrationwithotp.util.OtpUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private OtpUtil otpUtil;

    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private UserRepository userRepository;

    public String register(RegisterDto registerDto) {
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(registerDto.getEmail(),otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send OTP , please try again");
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "User Registration Successful";
    }

    public String verifyAccount(String email, String otp) {
       User user =  userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found with this email: "+email));

       if(user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),LocalDateTime.now()).getSeconds() < (1 * 60)){
           user.setActive(true);
           userRepository.save(user);
           return "OTP verified you can login";
       }
       return "Please regenerate otp and try again";
    }

    public String regenrateOtp(String email) {

        User user =  userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found with this email: "+email));
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email,otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send OTP , please try again");
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent.... please verify account within 1 minute";
    }

    public String login(LoginDto loginDto) {
        User user =  userRepository.findByEmail(loginDto.getEmail()).
                orElseThrow(()->new RuntimeException("User not found with this email: "+loginDto.getEmail()));

        if (!loginDto.getPassword().equals(user.getPassword())){
            return "Password is incorrect";
        } else if (!user.isActive()) {
           return "your account is not verified";
        }

        return "Login Successful";
    }
}
