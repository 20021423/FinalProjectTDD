package org.quyetnt.finalprojecttdd.security;

import org.quyetnt.finalprojecttdd.model.User;
import org.quyetnt.finalprojecttdd.repository.UserRepository;
import org.quyetnt.finalprojecttdd.security.service.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor inject UserRepository
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Tìm kiếm người dùng theo tên đăng nhập và trả về đối tượng UserDetailsImpl
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm người dùng trong cơ sở dữ liệu
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Trả về UserDetailsImpl
        return UserDetailsImpl.build(user);
    }
}

