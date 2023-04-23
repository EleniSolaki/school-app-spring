package gr.aueb.cf.schoolapp.rest;


import gr.aueb.cf.schoolapp.dto.TeacherDTO;
import gr.aueb.cf.schoolapp.dto.UserDTO;
import gr.aueb.cf.schoolapp.model.User;
import gr.aueb.cf.schoolapp.service.IUserService;
import gr.aueb.cf.schoolapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.schoolapp.service.util.LoggerUtil;
import gr.aueb.cf.schoolapp.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/*swagger:
/http://localhost:8080/swagger-ui/index.html
*/
@RestController
@RequestMapping("/api")
public class UserRestController {

    private final IUserService userService;
    private final UserValidator userValidator;
    private final MessageSource messageSource;
    private MessageSourceAccessor accessor;

    @Autowired
    public UserRestController(IUserService userService, UserValidator userValidator, MessageSource messageSource) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.messageSource = messageSource;
    }

    @RequestMapping(path="/users/{username}", method = RequestMethod.GET)
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable("username") String username){
        User user;

        try{
            user = userService.getUserByUsername(username);
            UserDTO dto = new UserDTO(user.getId(),user.getUsername(),user.getPassword());

            return new ResponseEntity<>(dto, HttpStatus.OK);
        }catch (EntityNotFoundException e){
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> addUser (@RequestBody UserDTO dto, BindingResult bindingResult){
        userValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("empty"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = userService.registerUser(dto);
        UserDTO userDTO = map(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userDTO.getId())
                .toUri();
        return ResponseEntity.created(location).body(userDTO);
    }

    @RequestMapping(value = "users/{username}", method = RequestMethod.PUT)
    public ResponseEntity<UserDTO> updateUser (@PathVariable("username") String username, @RequestBody UserDTO dto, BindingResult bindingResult){
        userValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("empty"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try{
            dto.setUsername(username);
            User user = userService.updateUser(dto);
            UserDTO userDTO = map(user);
            return new ResponseEntity<>(userDTO,HttpStatus.OK);
        }catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/users{username}", method = RequestMethod.DELETE)
    public ResponseEntity<UserDTO> deleteUser (@PathVariable("username") String username){
        try{
            User user = userService.getUserByUsername(username);
            userService.deleteUser(username);
            UserDTO userDTO = map(user);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    private UserDTO map(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(userDTO.getPassword());
        return userDTO;
    }
}
