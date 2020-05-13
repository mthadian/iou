package com.iou.iou.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iou.iou.models.Debts;

public interface DebtRepository extends JpaRepository<Debts, Integer>
{

	List<Debts> findByLender_UserId(Integer userId);
	List<Debts> findByBorrower_UserId(Integer userId);
	
	List<Debts> findByBorrower_Name(String name);
	List<Debts> findByLender_Name(String name);
	
}
