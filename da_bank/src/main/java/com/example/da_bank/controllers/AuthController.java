package com.example.da_bank.controllers;

import com.example.da_bank.auth.AuthRequest;
import com.example.da_bank.auth.AuthResponse;
import com.example.da_bank.auth.JwtTokenUtil;
import com.example.da_bank.models.AccountUser;
import com.example.da_bank.repositories.AccountUserRepository;
import com.example.da_bank.service.AccountUserService.AccountUserService;
import lombok.Data;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AccountUserService accountUserService;
    @Autowired
    AccountUserRepository accountUserRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            AccountUser accountUser = (AccountUser) authentication.getPrincipal();
            String accessToken = jwtTokenUtil.generateAccessToken(accountUser);
            AuthResponse response = new AuthResponse(accountUser.getUsername(), accessToken);
            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            AccountUser accountUser = (AccountUser) authentication.getPrincipal();
            String accessToken = jwtTokenUtil.generateAccessToken(accountUser);
            AuthResponse response = new AuthResponse(accountUser.getUsername(), accessToken);
            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/createUserAccount")
    protected @ResponseBody String createUserAccount(
            @RequestParam ("customer_number") int customerNumber,
            @RequestParam ("dob") String dob,
            @RequestParam ("first_name") String firstName,
            @RequestParam ("last_name") String lastName,
            @RequestParam ("password") String password,
            @RequestParam ("username") String username,
            @RequestParam ("email") String email,
            @RequestParam ("phone_number") String phone_number
    ){
        return accountUserService.createUserAccount(customerNumber, dob, firstName, lastName, password, username, email, phone_number);
    }


    @GetMapping("/getAllAccounts")
    @RolesAllowed({"ROLE_CUSTOMER", "ROLE_EDITOR"})
    public @ResponseBody String getAll(){
        JSONArray jsonArray = new JSONArray();
        accountUserRepository.findAll().forEach((user) -> jsonArray.add(user.getFirstName() + " " + user.getLastName()));
        return jsonArray.toJSONString();
    }


    @DeleteMapping("/deleteUserAccount/{id}")
    protected ResponseEntity<?> deleteUserAccount(@PathVariable Long id){
        accountUserRepository.deleteById(id);
        return ResponseEntity.ok().body("Account deleted");
    }

    @PostMapping("/addRoleToUser")
    public ResponseEntity<?> addUserRole(@RequestBody RoleToUserForm form){
        accountUserService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

}

@Data
class RoleToUserForm {
    private String username;
    private String roleName;
}

