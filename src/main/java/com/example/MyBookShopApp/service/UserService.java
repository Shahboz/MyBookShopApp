package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.UserDto;
import com.example.MyBookShopApp.entity.Role;
import com.example.MyBookShopApp.repository.RoleRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
@NoArgsConstructor
public class UserService {

    @Value("${user.refresh.offset}")
    private Integer refreshOffset;

    @Value("${user.refresh.limit}")
    private Integer refreshLimit;

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public Integer getRefreshOffset() {
        return refreshOffset;
    }

    public Integer getRefreshLimit() {
        return refreshLimit;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User getUserByPhone(String phone) {
        return userRepository.findUserByPhone(phone);
    }

    public User getUserByHash(String userHash) {
        return userRepository.findUserByHash(userHash);
    }

    public Role getUserRoleByName(String roleName) {
        return roleRepository.findRoleByName(roleName);
    }

    public Integer getCountRegisteredUsers() {
        return Math.toIntExact(userRepository.countRegisteredUsers());
    }

    public Integer getCountUsers() {
        return Math.toIntExact(userRepository.count());
    }

    public UserDto getUserDtoByHash(String userHash) {
        User user = userRepository.findUserByHash(userHash);
        return user == null ? new UserDto() : new UserDto(user);
    }

    public List<UserDto> getPageOfUsers(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return userRepository.findByOrderByRegTimeDesc(nextPage).getContent().stream().map(UserDto::new).collect(Collectors.toList());
    }

}