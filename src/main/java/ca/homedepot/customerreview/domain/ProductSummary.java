package ca.homedepot.customerreview.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummary 
{
	private Long id;
	private String name;
	
	//Extra fields for product summary
	private Integer reviewNums;
	private Double averageRating;
	private Double lowestRating;
	private Double highestRating;
	
	//Reviews
	private List<CustomerReview> reviews;
	


}
