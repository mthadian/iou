package com.iou.iou.models;

import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
*
* @author PMMuthama
*/
@Entity
public class Users 
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "user_id")
	private int userId;
	@Column(name = "name")
	private String name;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "lender")
    @JsonIgnore
    private List<Debts> list_Lenders;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "borrower")
    @JsonIgnore
    private List<Debts> list_Borrowers;
	
	/*
	 
    @OneToMany(mappedBy = "organizationId")
    @JsonIgnore
    private Collection<MstOrganizationAttachment> mstOrganizationAttachmentCollection;
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")
    @ManyToOne
    private MstOrganizationCategory categoryId;
	 */

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Debts> getList_Lenders() {
		return list_Lenders;
	}

	public void setList_Lenders(List<Debts> list_Lenders) {
		this.list_Lenders = list_Lenders;
	}

	public List<Debts> getList_Borrowers() {
		return list_Borrowers;
	}

	public void setList_Borrowers(List<Debts> list_Borrowers) {
		this.list_Borrowers = list_Borrowers;
	}

	@Override
	public String toString() {
		return "Users [userId=" + userId + ", name=" + name + ", list_Lenders=" + list_Lenders + ", list_Borrowers="
				+ list_Borrowers + "]";
	}

	
	
	
	
	
	
	
	

}
