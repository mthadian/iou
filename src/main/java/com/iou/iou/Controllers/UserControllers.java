package com.iou.iou.Controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.iou.iou.Repositories.DebtRepository;
import com.iou.iou.Repositories.UserRepository;
import com.iou.iou.models.Debts;
import com.iou.iou.models.Users;

/**
*
* @author PMMuthama
*/

@RestController
public class UserControllers 
{
	private static final Logger logger = LoggerFactory.getLogger(UserControllers.class);
	String newline = System.lineSeparator();
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	DebtRepository debtRepository;
	
	ObjectMapper objectMapper = new ObjectMapper(); 
	 
	@GetMapping("/users")
	
	public ResponseEntity<?> getAllUsers() throws Exception
	{
		
		List<Users> list_Users=new ArrayList<Users>();
		ObjectNode outPutJson=objectMapper.createObjectNode();
		
		try 
		{
			//get list of all users ordered by name ascending
			list_Users=userRepository.findAllByOrderByNameAsc();
			List<ObjectNode> jsonAllUsers=new ArrayList<ObjectNode>();
			
			for(Users user:list_Users)
			{
				ObjectNode jsonUser = objectMapper.createObjectNode();
				List<Debts> list_BorrowedFrom=new ArrayList<Debts>();
				List<Debts> list_LendedTo=new ArrayList<Debts>();
			
				
				
			
				//get list of users that current user has lent money to
				list_LendedTo=debtRepository.findByLender_UserId(user.getUserId());
				BigDecimal totalLendedOut=new BigDecimal(0);
				List<JsonNode> list_Borrowers=new ArrayList<JsonNode>();				
				for(Debts a_borrower:list_LendedTo)
				{
					
					ObjectNode json_Aborrower = objectMapper.createObjectNode();
					json_Aborrower.put(a_borrower.getBorrower().getName(), a_borrower.getAmount());
					list_Borrowers.add(json_Aborrower);		
					
					totalLendedOut=totalLendedOut.add(a_borrower.getAmount());
				}
				
				//get the list of users that the current user has borrowed from
				list_BorrowedFrom=debtRepository.findByBorrower_UserId(user.getUserId());
				BigDecimal totalBorrowedIn=new BigDecimal(0);
				List<JsonNode> list_Lenders=new ArrayList<JsonNode>();	
				for(Debts a_Lender:list_BorrowedFrom)
				{
					ObjectNode json_ALender = objectMapper.createObjectNode();
					json_ALender.put(a_Lender.getLender().getName(), a_Lender.getAmount());
					list_Lenders.add(json_ALender);
					totalBorrowedIn=totalBorrowedIn.add(a_Lender.getAmount());
					
				
				}
				
				//calculate net balance
				BigDecimal balance=totalLendedOut.subtract(totalBorrowedIn);
			
				//add the details of name,owes,owedby and balance of the current user to a json object
				jsonUser.put("name", user.getName());
				jsonUser.putPOJO("owes", list_Lenders);
				jsonUser.putPOJO("owed_by", list_Borrowers);
				jsonUser.put("balance", balance);
				
				//add the final details of the current user to the final output
				jsonAllUsers.add(jsonUser);
				
				
			}
			outPutJson.putPOJO("users", jsonAllUsers);
			//jsonOutput.putPOJO(fieldName, pojo)
			return new ResponseEntity<>(outPutJson,HttpStatus.OK);
		} catch (Exception e) 
		{
			ObjectNode json_FinalOutPut=objectMapper.createObjectNode();
			
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
	

	
	@PostMapping("/add")
	public ResponseEntity<?> addUser(@RequestBody JsonNode json_User) throws Exception
	{
		Users user=new Users();
		String user_name="";
		ObjectNode json_FinalOutPut = objectMapper.createObjectNode();

		try 
		{
			if(json_User.hasNonNull("user")) 
			{
				user_name=json_User.get("user").asText();
				if(user_name.length()==0)
				{
					logger.error("Enter name of user"); 
					
					json_FinalOutPut.put("statusCode", 1);
					json_FinalOutPut.put("statusDesciption", "Enter name of user");
					return new ResponseEntity<>(json_FinalOutPut,HttpStatus.BAD_REQUEST);
				}
			}
			else
			{
				logger.error("Request structure has null name");
				json_FinalOutPut.put("statusCode", 1);
				json_FinalOutPut.put("statusDesciption", "Request structure has null name");
				return new ResponseEntity<>(json_FinalOutPut,HttpStatus.BAD_REQUEST);
			}
			
			if (userRepository.findByNameIgnoreCase(user_name).isPresent()) 
			{
				logger.info("User "+user_name+" Already Exists");
				json_FinalOutPut.put("statusCode", 1);
				json_FinalOutPut.put("statusDesciption", "User "+user_name+" Already Exists");
				return new ResponseEntity<>(json_FinalOutPut,HttpStatus.OK);
				
			}
			user.setName(user_name);
			userRepository.save(user);
			logger.info("User successfully saved");
			json_FinalOutPut.put("statusCode", 0);
			json_FinalOutPut.put("statusDesciption", "User Successfully Created");
			json_FinalOutPut.putPOJO("user", user);
			
			
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
	
	@DeleteMapping("/user/delete/{name}")
	public ResponseEntity<?> deleteUserById(@PathVariable("name") String name,HttpServletRequest request) throws Exception
	{
		ObjectNode json_FinalOutPut = objectMapper.createObjectNode();
		
		try 
		{
			RestTemplate restTemplate = new RestTemplate(); 
			String uri=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
			
			if(!userRepository.findByNameIgnoreCase(name).isPresent())
			{
				json_FinalOutPut.put("statusCode", 1);
				json_FinalOutPut.put("statusDesciption", "No user Exists with Name: "+name);
				return new ResponseEntity<>(json_FinalOutPut,HttpStatus.OK);
				
			}
			
			userRepository.deleteById(userRepository.findByNameIgnoreCase(name).get().getUserId());
			logger.info("User "+name+" deleted successfully");
			//json_FinalOutPut.put("statusCode", 0);
			//json_FinalOutPut.put("statusDesciption", "User "+name+" deleted successfully");
			
			json_FinalOutPut=restTemplate.getForObject(uri+"/users", ObjectNode.class);
			
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
