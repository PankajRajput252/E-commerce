package com.mineCryptos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mineCryptos.model.FinalConstants;
import com.mineCryptos.model.Role;
import com.mineCryptos.repo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpringBootApplication
public class  MineCryptosApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
   private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository  roleRepository;

	public static void main(String[] args) {

		SpringApplication.run(MineCryptosApplication.class, args);
        Map<String, Object> testData = new HashMap<>();
        testData.put("payment_id", "5281942787");
        testData.put("payment_status", "finished");
        testData.put("payin_hash", "0x419c938c844ab6c0ac10211c9dd7e54e2a376d39f5e60aba173fe622532d345b");

        String signature = hmacSha512Sorted(testData, "your-ipn-secret");
        System.out.println("Use this signature: " + signature);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
		return springApplicationBuilder.sources(MineCryptosApplication.class);
	}


	@Override
	public void run(String... args) throws Exception {
		System.out.println(this.passwordEncoder.encode("xyz"));

		try{
			Role role=new Role();
			role.setRoleId(FinalConstants.ADMIN_USER);
			role.setName("ADMIN_USER");

			Role role1=new Role();
			role1.setRoleId(FinalConstants.NORMAL_USER);
			role1.setName("NORMAL_USER");

			List<Role> roles=new ArrayList<>();
			roles.add(role);
			roles.add(role1);
		    List<Role>result=this.roleRepository.saveAll(roles);
			result.forEach(r -> {
						System.out.println(r);
					}
			);
		}
		catch (Exception e){
          e.printStackTrace();
		}
	}

    private static String hmacSha512Sorted(Map<String, Object> data, String secret) {
        try {
            // Sort the map keys and create sorted JSON
            String sortedJson = createSortedJson(data);
//            logger.info("Sorted JSON for HMAC: {}", sortedJson);

            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(key);
            byte[] rawHmac = mac.doFinal(sortedJson.getBytes(StandardCharsets.UTF_8));

            // Convert to HEX (not Base64)
            return bytesToHex(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("HMAC error", e);
        }
    }

    private static String createSortedJson(Map<String, Object> data) {
        try {
            // Use Gson to create sorted JSON
            Gson gson = new GsonBuilder().create();
            Map<String, Object> sortedMap = new TreeMap<>(data);
            return gson.toJson(sortedMap);
        } catch (Exception e) {
            throw new RuntimeException("JSON sorting error", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
