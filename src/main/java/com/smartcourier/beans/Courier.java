package com.smartcourier.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Entity implementation class for Entity: Courier
 *
 */
@Entity

public class Courier implements Serializable {

	   
	@Id
	@GeneratedValue
	private Long id;
	private String email;
	private String phone;
	private String preferredArea;

	
	private String po;
	@OneToOne(mappedBy = "courier", fetch = FetchType.LAZY)
	//@JsonManagedReference
	private User user;
	
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="courier")
    //@JsonManagedReference
	private List<Delivery> delivery;
	
	
	/*@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
	private List<Salary> salary;*/
	
    /*@OneToMany(
            mappedBy = "courier", 
            cascade = CascadeType.ALL, 
            orphanRemoval = true
        )
	private List<Delivery> delivery;
	
	@ManyToMany(mappedBy = "courier")
	private Set<Region> region = new HashSet<>();
    */
	@ManyToMany(fetch=FetchType.LAZY, 	mappedBy = "courier")
    @JsonIgnore
	private Set<Region> region = new HashSet<>();
	
	// The 'mappedBy = "courier"' attribute specifies that
	// the 'private Courier courier;' field in delivery owns the
	// relationship (i.e. contains the foreign key for the query to
	// find all deliveries for a courier.
	//@OneToMany(mappedBy = "region")
	//private List<Delivery> delivery;
	
	private static final long serialVersionUID = 1L;
	
	
	/*public List<Delivery> getDelivery() {
		return delivery;
	}

	public void setDelivery(List<Delivery> delivery) {
		this.delivery = delivery;
	}

	public Set<Region> getRegion() {
		return region;
	}

	public void setRegion(Set<Region> region) {
		this.region = region;
	}*/

	 
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	

	public Set<Region> getRegion() {
		return region;
	}

	public void setRegion(Set<Region> region) {
		this.region = region;
	}


	
	public Courier() {
		super();
	}  
	 
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}   
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}   
	
	public String getPreferredArea() {
	    return preferredArea;
	}
	public void setPreferredArea(String preferredArea) {
	    this.preferredArea = preferredArea;
	}
	
	public String getPo() {
		return this.po;
	}

	public void setPo(String po) {
		this.po = po;
	}

	public List<Delivery> getDelivery() {
		return delivery;
	}

	public void setDelivery(List<Delivery> delivery) {
		this.delivery = delivery;
	}   
	
	
	
	/*public List<Salary> getSalary() {
		return salary;
	}
	
	public void setSalary(List<Salary> salary) {
		this.salary = salary;
	}*/
}
