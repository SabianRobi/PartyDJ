package partydj.backend.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import partydj.backend.rest.domain.User;
import partydj.backend.rest.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findById(int userId) {
        return userRepository.findById(userId);
    }
}
