package ez.codebits.journalApp.controller;

import ez.codebits.journalApp.api.response.WeatherResponse;
import ez.codebits.journalApp.entity.User;
import ez.codebits.journalApp.repository.UserRepository;
import ez.codebits.journalApp.service.UserService;
import ez.codebits.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

     @Autowired
     private UserService userService;
     @Autowired
     private UserRepository userRepository;
     @Autowired
     private WeatherService weatherService;

     @PutMapping
     public ResponseEntity<?> updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User userInDB = userService.findByUsername(username);
        userInDB.setUsername(user.getUsername());
        userInDB.setPassword(user.getPassword());
        userService.saveNewUser(userInDB);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
     }

     @DeleteMapping
     public ResponseEntity<?> deleteUserByName() {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         userRepository.deleteByUsername(authentication.getName());
         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
     }

    @GetMapping
    public ResponseEntity<?> greeting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponse weatherResponse = weatherService.getWeather("Mumbai");
        String greeting = "";
        if(weatherResponse != null)
            greeting = ", Weather feels like "+weatherResponse.getCurrent().getFeelslike();
        return new ResponseEntity<>("hi " + authentication.getName() + greeting, HttpStatus.OK);
    }
}
