package com.iou.iou.Controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iou.iou.Repositories.DebtRepository;
import com.iou.iou.Repositories.UserRepository;
import com.iou.iou.models.Debts;
import com.iou.iou.models.Users;

import antlr.debug.NewLineEvent;

/**
*
* @author PMMuthama
*/

@RestController
public class DebtController 
{
	private static final Logger logger = LoggerFactory.getLogger(DebtController.class);
	String newline = System.lineSeparator();
	ObjectMapper objectMapper = new ObjectMapper(); 
	
	@Autowired
	DebtRepository debtRepository;
	
	@Autowired
	UserRepository userRepository;
	

	
	@PostMapping("/iou")
	public ResponseEntity<?> createIOU(@RequestBody JsonNode json_IOU,HttpServletRequest request) throws Exception
	{
		Debts debt= new Debts();
		Users lender= new Users();
		Users borrower= new Users();
		
		ObjectNode json_FinalOutPut = objectMapper.createObjectNode();

		
		try 
		{
			
			int lenderId;
			int borrowerId;
			
			String lenderName;
			String borrowerName;
			BigDecimal amount;
			RestTemplate restTemplate = new RestTemplate(); 
			String uri=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
			
			if(json_IOU.hasNonNull("lender")) 
			{
				lenderName=json_IOU.get("lender").asText();
			
				
				if(!userRepository.findByNameIgnoreCase(lenderName).isPresent())
				{
					logger.error("No user exists with Name "+lenderName); 
					
					json_FinalOutPut.put("statusCode", 1);
					json_FinalOutPut.put("statusDesciption", "No user exists with Name "+lenderName);
					return new ResponseEntity<>(json_FinalOutPut,HttpStatus.OK);
				}
				
				
			}
			else
			{
				logger.error("Request structure has no lender");
				json_FinalOutPut.put("statusCode", 1);
				json_FinalOutPut.put("statusDescription", "Request structure has no lender");
				return new ResponseEntity<>(json_FinalOutPut,HttpStatus.BAD_REQUEST);
			}
			
			if(json_IOU.hasNonNull("borrower")) 
			{
				borrowerName=json_IOU.get("borrower").asText();
				
				if(!userRepository.findByNameIgnoreCase(borrowerName).isPresent())
				{
					logger.error("No user exists with Name "+borrowerName); 
					
					json_FinalOutPut.put("statusCode", 1);
					json_FinalOutPut.put("statusDesciption", "No user exists with Name "+borrowerName);
					return new ResponseEntity<>(json_FinalOutPut,HttpStatus.OK);
					
				}
			}
			else
			{
				logger.error("Request structure has no Borrower");
				json_FinalOutPut.put("statusCode", 1);
				json_FinalOutPut.put("statusDescription", "Request structure has no Borrower");
				return new ResponseEntity<>(json_FinalOutPut,HttpStatus.BAD_REQUEST);
				
			}
			
			
			if(json_IOU.hasNonNull("amount")) 
			{
			
				amount=BigDecimal.valueOf(json_IOU.get("amount").asDouble());
				
			}
			else
			{
				logger.error("Request structure has no amount "); 
				
				json_FinalOutPut.put("statusCode", 1);
				json_FinalOutPut.put("statusDesciption", "Request structure has no amount ");
				return new ResponseEntity<>(json_FinalOutPut,HttpStatus.BAD_REQUEST);
			}
			
			if(lenderName==borrowerName)
			{
				logger.error("Lender and Borrower are the same "); 
				
				json_FinalOutPut.put("statusCode", 1);
				json_FinalOutPut.put("statusDesciption", "Lender and Borrower are the same");
				return new ResponseEntity<>(json_FinalOutPut,HttpStatus.OK);
			}
			
			
			lender=userRepository.findByNameIgnoreCase(lenderName).get();
			borrower=userRepository.findByNameIgnoreCase(borrowerName).get();
			
			debt.setLender(lender);		
			debt.setBorrower(borrower);
			debt.setAmount(amount);
			
			debtRepository.save(debt);
			
			
			
			json_FinalOutPut=restTemplate.getForObject(uri+"/users", ObjectNode.class);
			
			//json_FinalOutPut.putPOJO("users", userRepository.findAllByOrderByNameAsc());
			
			
			
			
			
			return new ResponseEntity<>(json_FinalOutPut,HttpStatus.OK);
		} catch (Exception e) 
		{
			
			e.printStackTrace();
			StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            
            
			json_FinalOutPut.put("statusDescription", "Internal Server Error");
			json_FinalOutPut.put("exception", exceptionAsString);
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
