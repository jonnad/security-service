package com.ventyx.security.api;

import com.ventyx.security.api.model.User;
import org.springframework.stereotype.Service;

/**
 *
 * Service interface for interacting with user repositories
 *
 */
@Service
public interface UserService {

    public User validateUser(User user);
    public User getUser(String userId);


}
