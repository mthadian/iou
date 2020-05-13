package com.iou.iou.Controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.iou.iou.Repositories.DebtRepository;
import com.iou.iou.Repositories.UserRepository;
import com.iou.iou.models.Debts;
import com.iou.iou.models.Users;

import antlr.debug.NewLineEvent;

@RestController
public class DebtController 
{
	private static final Logger logger = LoggerFactory.getLogger(DebtController.class);
	String newline = System.lineSeparator();
	
	@Autowired
	DebtRepository debtRepository;
	
	@Autowired
	UserRepository userRepository;
	
	
	@PostMapping("/iou")
	public ResponseEntity<?> createIOU(@RequestBody JsonNode json_IOU) throws Exception
	{
		Debts debt= new Debts();
		Users lender= new Users();
		Users borrower= new Users();
		try 
		{
			int lenderId;
			int borrowerId;
			
			String lenderName;
			String borrowerName;
			BigDecimal amount;
			if(json_IOU.hasNonNull("lender")) 
			{
				//lenderId=json_IOU.get("lender").asInt();
				lenderName=json_IOU.get("lender").asText();
				/*
				if(!userRepository.findByUserId(lenderId).isPresent())
				{
					return new ResponseEntity<>("No user exists with UserId "+lenderId,HttpStatus.BAD_REQUEST);
				}
				*/
				
				if(!userRepository.findByNameIgnoreCase(lenderName).isPresent())
				{
					return new ResponseEntity<>("No user exists with Name "+lenderName,HttpStatus.BAD_REQUEST);
				}
				
				
			}
			else
			{
				return new ResponseEntity<>("lender  can not be null",HttpStatus.BAD_REQUEST);
			}
			
			if(json_IOU.hasNonNull("borrower")) 
			{
				borrowerName=json_IOU.get("borrower").asText();
				/*
				borrowerId=json_IOU.get("borrower").asInt();
				if(!userRepository.findByUserId(borrowerId).isPresent())
				{
					return new ResponseEntity<>("No user exists with UserId "+borrowerId,HttpStatus.BAD_REQUEST);
				}
				*/
				if(!userRepository.findByNameIgnoreCase(borrowerName).isPresent())
				{
					return new ResponseEntity<>("No user exists with Name "+borrowerName,HttpStatus.BAD_REQUEST);
				}
			}
			else
			{
				return new ResponseEntity<>("borrower can not be null",HttpStatus.BAD_REQUEST);
			}
			
			
			if(json_IOU.hasNonNull("amount")) 
			{
				//amount=json_IOU.get("amount").asDouble();
				amount=BigDecimal.valueOf(json_IOU.get("amount").asDouble());
				System.out.println("amount "+amount);
			}
			else
			{
				return new ResponseEntity<>("amount can not be null",HttpStatus.BAD_REQUEST);
			}
			
			if(lenderName==borrowerName)
			{
				return new ResponseEntity<>("Lender can not be a borrower on the same request",HttpStatus.BAD_REQUEST);
			}
			
			/*
			lender=userRepository.findByUserId(lenderId).get();
			borrower=userRepository.findByUserId(borrowerId).get();
			*/
			
			lender=userRepository.findByNameIgnoreCase(lenderName).get();
			borrower=userRepository.findByNameIgnoreCase(borrowerName).get();
			
			debt.setLender(lender);		
			debt.setBorrower(borrower);
			debt.setAmount(amount);
			
			debtRepository.save(debt);
			
			
			
			
			return new ResponseEntity<>(debt,HttpStatus.OK);
		} catch (Exception e) 
		{
			e.printStackTrace();
			StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            
            logger.error(exceptionAsString,newline);
			return new ResponseEntity<>(exceptionAsString,HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	@GetMapping("/api/debts")
	public ResponseEntity<?> getAllDebts() throws Exception
	{
		List<Debts> list_Debts=new ArrayList<Debts>();
		try
		{
			list_Debts=debtRepository.findAll();
			return new ResponseEntity<>(list_Debts,HttpStatus.OK);
			
		} catch (Exception e)
		{
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
