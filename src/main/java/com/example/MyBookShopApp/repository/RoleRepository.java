package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findRoleByName(String name);

}