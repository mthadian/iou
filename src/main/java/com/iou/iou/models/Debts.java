package com.iou.iou.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
*
* @author PMMuthama
*/
@Entity
public class Debts
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "debt_id")
	private int debtId;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	@JoinColumn(name = "lender", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
	private Users lender;
	
	@JoinColumn(name = "borrower", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
	private Users borrower;

	public int getDebtId() {
		return debtId;
	}

	public void setDebtId(int debtId) {
		this.debtId = debtId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Users getLender() {
		return lender;
	}

	public void setLender(Users lender) {
		this.lender = lender;
	}

	public Users getBorrower() {
		return borrower;
	}

	public void setBorrower(Users borrower) {
		this.borrower = borrower;
	}

	@Override
	public String toString() {
		return "Debts [debtId=" + debtId + ", amount=" + amount + ", lender=" + lender + ", borrower=" + borrower
				+ ", getDebtId()=" + getDebtId() + ", getAmount()=" + getAmount() + ", getLender()=" + getLender()
				+ ", getBorrower()=" + getBorrower() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	
	
	
	
	
	

}
