package com.gunwala.controller;

import com.gunwala.model.FinalResponse;
import com.gunwala.model.JwtAuthRequest;
import com.gunwala.model.User;
import com.gunwala.model.Util;
import com.gunwala.repo.UserRepository;
import com.gunwala.security.JwtAuthResponse;
import com.gunwala.security.JwtTokenHelper;
import com.gunwala.service.Service.UserService;
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
