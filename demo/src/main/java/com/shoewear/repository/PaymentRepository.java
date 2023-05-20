package com.shoewear.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoewear.model.PaymentHistory;

public interface PaymentRepository extends JpaRepository<PaymentHistory,Integer>{
	List<PaymentHistory> findAllByUser_Id(int id);
	List<PaymentHistory> findAllByUser_IdAndCategory_Id(int userid,int caregoryid);
	List<PaymentHistory> findAllByUser_IdAndPurchaseDateBetween(int userid, LocalDateTime from, LocalDateTime to);
	List<PaymentHistory> findAllByUser_IdAndCategory_IdAndPurchaseDateBetween(int userid, int caregoryid, LocalDateTime from, LocalDateTime to);
}
