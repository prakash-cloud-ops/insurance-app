package com.insurance.config;

import com.insurance.model.Policy;
import com.insurance.model.PolicyStatus;
import com.insurance.model.PolicyType;
import com.insurance.model.User;
import com.insurance.repository.PolicyRepository;
import com.insurance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUsers();
        }

        if (policyRepository.count() == 0) {
            seedPolicies();
        }
    }

    private void seedUsers() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        userRepository.save(admin);
    }

    private void seedPolicies() {
        Random random = new Random();
        List<Policy> policies = new ArrayList<>();

        String[] holderNames = {
                "John Smith", "Emma Johnson", "Michael Brown", "Sarah Davis",
                "David Wilson", "Lisa Anderson", "Robert Taylor", "Jennifer Martinez",
                "William Garcia", "Mary Rodriguez", "James Lee", "Patricia White",
                "Christopher Harris", "Linda Clark", "Daniel Lewis", "Barbara Walker",
                "Matthew Hall", "Susan Allen", "Joseph Young", "Jessica King"
        };

        PolicyType[] types = PolicyType.values();
        PolicyStatus[] statuses = PolicyStatus.values();

        for (int i = 0; i < 20; i++) {
            Policy policy = new Policy();
            policy.setPolicyNumber(String.format("POL-2026-%04d", i + 1));
            policy.setHolderName(holderNames[i]);
            policy.setType(types[random.nextInt(types.length)]);
            policy.setStatus(statuses[random.nextInt(statuses.length)]);
            policy.setPremiumAmount(BigDecimal.valueOf(500 + random.nextInt(4500)));
            policy.setStartDate(LocalDate.now().minusDays(random.nextInt(365)));
            policy.setEndDate(policy.getStartDate().plusYears(1).plusDays(random.nextInt(30)));

            policies.add(policy);
        }

        policyRepository.saveAll(policies);
    }
}
