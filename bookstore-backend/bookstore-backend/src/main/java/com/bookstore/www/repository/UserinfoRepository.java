package com.bookstore.www.repository;

import com.bookstore.www.entity.Userinfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserinfoRepository extends JpaRepository<Userinfo, UUID> {

}
