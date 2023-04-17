package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.dto.UserDTO;
import gr.aueb.cf.schoolapp.model.User;
import gr.aueb.cf.schoolapp.repository.UserRepository;
import gr.aueb.cf.schoolapp.service.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public User registerUser(UserDTO userToRegister) {

        return userRepository.save(convertToUser(userToRegister));
    }

    @Override
    public User updateUser(UserDTO userDTO) throws EntityNotFoundException {
        User user = userRepository.findByUsernameEquals(userDTO.getUsername());
        if(!userRepository.isUserValid(user.getUsername(),user.getPassword())) throw new EntityNotFoundException(User.class, userDTO.getId());
        return userRepository.save(convertToUser(userDTO));
    }

    @Override
    public void deleteUser(String username) throws EntityNotFoundException {
        userRepository.deleteByUsername(username);
    }

    @Override
    public User getUserByUsername(String username) throws EntityNotFoundException {
        User user = userRepository.findByUsernameEquals(username);
        if(!userRepository.usernameExists(username)) throw new EntityNotFoundException(User.class, 0L);
        return user;

    }

    @Override
    public User getUserById(Long id) throws EntityNotFoundException {
        Optional<User> user;
        user= userRepository.findById(id);
        if(user.isEmpty()) throw new EntityNotFoundException(User.class, 0L);
        return  user.get();
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.usernameExists(email);
    }

    private static User convertToUser(UserDTO dto){
        return new User(dto.getId(), dto.getUsername(), dto.getPassword());
    }
}
