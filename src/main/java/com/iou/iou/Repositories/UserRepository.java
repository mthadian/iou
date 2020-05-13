package com.iou.iou.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iou.iou.models.Users;

public interface UserRepository extends JpaRepository<Users, Integer>
{

	List<Users> findAllByOrderByNameAsc();
	Optional<Users> findByUserId(int userId);
	Optional<Users> findByNameIgnoreCase(String name);
}
