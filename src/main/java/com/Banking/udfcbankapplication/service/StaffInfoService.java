package com.Banking.udfcbankapplication.service;
import com.Banking.udfcbankapplication.entity.StaffInfo;
import com.Banking.udfcbankapplication.repository.StaffInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class StaffInfoService {
    @Autowired
    private StaffInfoRepo userInfoRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    public String addUser(StaffInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userInfoRepo.save(userInfo);
        return "User registered successfully!";
    }
}