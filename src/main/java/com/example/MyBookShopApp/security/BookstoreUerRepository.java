package com.example.MyBookShopApp.security;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookstoreUerRepository extends JpaRepository<BookstoreUser, Integer> {

    BookstoreUser findBookstoreUserByEmail(String email);

}