package ca.homedepot.customerreview.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import ca.homedepot.customerreview.domain.CustomerReview;
import ca.homedepot.customerreview.exception.RequestValidationException;
import ca.homedepot.customerreview.forms.CustomerReviewForm;
import ca.homedepot.customerreview.model.CustomerReviewModel;


public class ServicesUtil
{
	private static void validateParameterNotNull(Object parameter, String nullMessage)
	{
		Preconditions.checkArgument(parameter != null, nullMessage);
	}

	public static void validateParameterNotNullStandardMessage(String param, Object value)
	{
		validateParameterNotNull(value, "Parameter " + param + " cannot be null");
	}
	
	
	/*
	 * Return list of reviews whose ratings are within a given range (inclusive)
	 * 
	 * Assumptions: "From" is the lower rating and "To" is the higher rating, and they are all positive number. 
	 * 
	 * 				 The lowest rating is 0;
	 * 
	 */
	public static List<CustomerReview> filterReviews(Double ratingFrom,	Double ratingTo, List<CustomerReview> reviews) {

		Double ratingFromModified = ratingFrom;
		Double ratingToModified = ratingTo;
		
		// Rating range can be optional: 
		// If ratingFrom is null, consider search from the lowest rating: 0
		// If ratingTo is null, consider search up to the max rating
		if(ratingFrom == null) {
			ratingFromModified = 0.0;
		}
		if(ratingTo == null) {
			ratingToModified = Double.MAX_VALUE;
		}
		
		//If incoming request accidentally exchange the from and to value (ratingFrom > ratingTo), switch the value between ratingFrom and ratingTo
		if(ratingFrom != null && ratingTo != null) {
			if(ratingFrom.compareTo(ratingTo) >0) {
				ratingFromModified = ratingTo;
				ratingToModified = ratingFrom;
			}
		}
		
		// Filter based on modified rating range
		Double fromValue = ratingFromModified;
		Double toValue = ratingToModified;
		return reviews.stream()
		.filter(r -> r.getRating() >= fromValue  && r.getRating() <= toValue)
		.collect(Collectors.toList());
	}
	
	
	/*
	 * Validate the request, if anything happen below throws RequestValidationException
	 * 
	 * 1. Rating is negative number
	 * 2. Comment/headline contains curse words: "Ship","Miss","Duck","Punt","Rooster","Mother","Bits"; Case-insensitive
	 */
	
	public static void validateRequest(CustomerReviewForm customerReviewForm) {
		
		//Create empty error messages
		StringBuffer sb = new StringBuffer();
		
		boolean foundError = false;
		
		if(customerReviewForm != null) {
			
			//Checking rating: cannot be negative number
			Double rating = customerReviewForm.getRating();
			if(rating != null && rating < 0) {
				sb.append("Rating can not be nagtive : " + rating + " ; ");
				foundError = true;
			}
			
			//Check comment or headline has curse words
			String comment = customerReviewForm.getComment();
			String headline = customerReviewForm.getHeadline();
			List<String> matchedCurseWords = Constants.CURSEWORDS.stream()
			.filter(w -> {
				if(comment != null && comment.toLowerCase().contains(w.toLowerCase()) ||
						headline != null && headline.toLowerCase().contains(w.toLowerCase()) ) {
					return true;
				}
				return false;
				})
			.collect(Collectors.toList());
			
			//If find any matching curse words, prepare the error messages:
			if(matchedCurseWords != null && matchedCurseWords.size() > 0) {
				sb.append("Find curse words in the comment / headline : " + matchedCurseWords);
				foundError = true;
			}
			
			//If find any errors, throw RequestValidationException with error messages: 
			if(foundError == true) {
				throw new RequestValidationException(sb.toString());
			}
		}
	}
}
