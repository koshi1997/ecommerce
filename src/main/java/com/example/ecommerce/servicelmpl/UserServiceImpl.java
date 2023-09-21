package com.example.ecommerce.servicelmpl;

import com.example.ecommerce.JWT.CustomerUserDetailsService;
import com.example.ecommerce.JWT.JwtFilter;
import com.example.ecommerce.JWT.JwtUtil;
import com.example.ecommerce.POJO.User;
import com.example.ecommerce.contants.CafeContants;
import com.example.ecommerce.dao.UserDao;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.utils.CafeUtils;
import com.example.ecommerce.utils.EmailUtils;
import com.example.ecommerce.wrapper.UserWrapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signup(Map<String, String> requestMap) {

        log.info("Inside signup{}", requestMap);

        try {
            if (ValidateSignupMap(requestMap)) {

                User user = userDao.findAllByEmailId(requestMap.get("email"));

                if (Objects.isNull(user)) {

                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registered.", HttpStatus.OK);

                } else {
                    return CafeUtils.getResponseEntity("Email already exits", HttpStatus.BAD_REQUEST);

                }


            } else {

                return CafeUtils.getResponseEntity(CafeContants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeContants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }


    private boolean ValidateSignupMap(Map<String, String> requestMap) {

        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("password")) {
            return true;

        } else {
            return false;
        }

    }

    private User getUserFromMap(Map<String, String> requestMap) {

        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("inside login");
        try {

            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));

            if (auth.isAuthenticated()) {

                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);


                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "wait for admin approve" + "\"}", HttpStatus.BAD_REQUEST);
                }


            }


        } catch (Exception ex) {
            log.error("{}", ex);
        }

        return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials" + "\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {

           if(jwtFilter.isAdmin()){

                return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);

           }else {

               return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
           }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return new  ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{

            if(jwtFilter.isAdmin()){

            Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
            if(!optional.isEmpty()){

                    userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("User status updated successfully", HttpStatus.OK);

            }else {
                CafeUtils.getResponseEntity("User id doesn't not exist", HttpStatus.OK);
            }

            }else {

             return CafeUtils.getResponseEntity(CafeContants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();

        }

        return CafeUtils.getResponseEntity(CafeContants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status!=null && status.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER:- "+user+" \n is approved by \n ADMIN:-" +jwtFilter.getCurrentUser(),allAdmin);
        }else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disable", "USER:- "+user+" \n is disabled by \n ADMIN:-" +jwtFilter.getCurrentUser(),allAdmin);
        }
    }

    @Override
    public ResponseEntity<String> checkToken() {

        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {

             User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
             if(!userObj.equals(null)){
                 if(userObj.getPassword().equals(requestMap.get("oldPassword"))){
                     userObj.setPassword(requestMap.get("newPassword"));
                     userDao.save(userObj);
                     return CafeUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);

                 }
                 return CafeUtils.getResponseEntity("Incorrect old password", HttpStatus.BAD_REQUEST);

             }
             return CafeUtils.getResponseEntity(CafeContants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeContants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail()))
                emailUtils.forgotMail(user.getEmail(), "credential by Cafe Management System", user.getPassword());
                return CafeUtils.getResponseEntity("check your mail credential", HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeContants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}