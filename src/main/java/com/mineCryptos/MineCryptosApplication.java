package com.mineCryptos;

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

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class  MineCryptosApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
   private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository  roleRepository;

	public static void main(String[] args) {

		SpringApplication.run(MineCryptosApplication.class, args);
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
}
