package com.mineCryptos.controller;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.JwtAuthRequest;
import com.mineCryptos.model.User;
import com.mineCryptos.model.Util;
import com.mineCryptos.repo.UserRepository;
import com.mineCryptos.security.JwtAuthResponse;
import com.mineCryptos.security.JwtTokenHelper;
import com.mineCryptos.service.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth/")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> createToken(@RequestBody JwtAuthRequest request)
    {
        User userData=userRepository.findByNodeIdAndActiveStateCodeFkId(request.getUsername(),"ACTIVE");
        if(Util.isDefined(userData)){
            request.setUsername(userData.getEmail());
        }
        else{
            JwtAuthResponse response = new JwtAuthResponse();
            response.setErrorMsg("Something went wrong");
            return new ResponseEntity<JwtAuthResponse>(response, HttpStatus.BAD_REQUEST);
        }
        this.authenticate(request.getUsername(), request.getPassword());

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getUsername());

        String token = this.jwtTokenHelper.generateToken(userDetails);
        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        response.setUser((User) userDetails);
        return new ResponseEntity<JwtAuthResponse>(response, HttpStatus.OK);

    }



    private void authenticate(String username,String password)
    {
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(username,password);
        try {
            this.authenticationManager.authenticate(authenticationToken);
        }
        catch (DisabledException e)
        {
            System.out.println("User is disabled");
        }

    }


    //
    @PostMapping("/register")
    public FinalResponse registerUser(@RequestBody User user)
    {
      return this.userService.createRegistration(user);
    }


}
