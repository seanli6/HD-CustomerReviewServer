package ca.homedepot.customerreview.controller;

import ca.homedepot.customerreview.dao.*;
import ca.homedepot.customerreview.dao.UserDao;
import ca.homedepot.customerreview.domain.CustomerReview;
import ca.homedepot.customerreview.domain.Product;
import ca.homedepot.customerreview.domain.ProductSummary;
import ca.homedepot.customerreview.domain.User;
import ca.homedepot.customerreview.exception.ProductNotFoundException;
import ca.homedepot.customerreview.exception.UserNotFoundException;
import ca.homedepot.customerreview.forms.CustomerReviewForm;
import ca.homedepot.customerreview.model.CustomerReviewModel;
import ca.homedepot.customerreview.model.ProductModel;
import ca.homedepot.customerreview.model.*;
import ca.homedepot.customerreview.service.CustomerReviewService;
import ca.homedepot.customerreview.util.ServicesUtil;
import io.swagger.annotations.ApiOperation;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.nio.channels.FileChannel.MapMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import javax.websocket.server.PathParam;


@RestController
public class CustomerReviewController
{
	@Autowired
	private ProductDao productDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CustomerReviewDao customerReviewDao;

	@Autowired
	private CustomerReviewService customerReviewService;
	
	@ApiOperation(value = "Get all reviews under the product as well as summary info.", notes = "You can also filter the result by pass in the range of rating. Optional, the default range is 0-5"
			+ "\nProduct Id is mandatory")
	@GetMapping({ "products/summary/{productId}" })
	public ProductSummary getProductSummary(@PathVariable final Long productId,
			@RequestParam(required = false) final Double ratingFrom, @RequestParam(required = false) final Double ratingTo)
	{
		final ProductModel product = productDao.findById(productId).orElse(null);
		if (product == null)
		{
			throw new ProductNotFoundException(productId);
		}
		
		ModelMapper mapper = new ModelMapper();
		ProductSummary productSummary =  mapper.map(product, ProductSummary.class);
		List<CustomerReview> reviewsFiltered = ServicesUtil.filterReviews(ratingFrom, ratingTo, productSummary.getReviews());
		productSummary.setReviews(reviewsFiltered);
		
		if(reviewsFiltered != null) {
			//Count the number of reviews
			productSummary.setReviewNums(reviewsFiltered.size());
			
			//Average rating
			Double average = reviewsFiltered.stream().mapToDouble(r -> r.getRating()).average().orElse(0.0);
			productSummary.setAverageRating( (double)Math.round(average * 100) / 100);
			
			//Lowest rating
			productSummary.setLowestRating(reviewsFiltered.stream().mapToDouble(r -> r.getRating()).min().orElse(0.0));
			
			//Highest rating
			productSummary.setHighestRating(reviewsFiltered.stream().mapToDouble(r -> r.getRating()).max().orElse(0.0));
		}
		return productSummary;
	}
	
	@ApiOperation(value = "Get all reviews under the product", notes = "You can also filter the result by pass in the range of rating. Optional, the default range is 0-5"
			+ "\nProduct Id is mandatory")
	@GetMapping({ "products/{productId:\\d+}/reviews" })
	public List<CustomerReview> getReviews(@PathVariable final Long productId,
			@RequestParam(required = false) final Double ratingFrom, @RequestParam(required = false) final Double ratingTo)
	{
		final ProductModel product = productDao.findById(productId).orElse(null);
		if (product == null)
		{
			throw new ProductNotFoundException(productId);
		}
		
		List<CustomerReviewModel> reviews = customerReviewService.getReviewsForProduct(product);
		ModelMapper mapper = new ModelMapper();
		List<CustomerReview> reviewsDomain = Arrays.asList(mapper.map(reviews, CustomerReview[].class));
        
		return ServicesUtil.filterReviews(ratingFrom, ratingTo, reviewsDomain);
       
	}
	
	@ApiOperation(value = "Create review", notes = "Product and user has to be created first. Use the sample request body below: \n\n\n"
			+ "{"
			+ "\n&nbsp;&nbsp;   \"headline\": \"Overall this is a pretty good dishwasher\","
			+ "\n&nbsp;&nbsp;   \"comment\": \"The reason I docked 1 star is mostly my fears that the motor has a bit of a ultra high pitched whir to it at times\","
			+ "\n&nbsp;&nbsp;   \"rating\": \"3.6\""
			+ "\n}")
	@PostMapping({ "products/{productId:\\d+}/users/{userId:\\d+}/reviews" })
	public CustomerReview createReview(@PathVariable final Long userId, @PathVariable final Long productId,
			@RequestBody final CustomerReviewForm customerReviewForm)
	{
		//Validate request body, if find any errors throw RequestValidationException with HttpStatus 422 UNPROCESSABLE_ENTITY
		ServicesUtil.validateRequest(customerReviewForm);
		
		final ProductModel product = productDao.findById(productId).orElse(null);
		if (product == null)
		{
			throw new ProductNotFoundException(productId);
		}

		final UserModel user = userDao.findById(userId).orElse(null);
		if (user == null)
		{
			throw new UserNotFoundException(userId);
		}

		CustomerReviewModel customerReviewModel = customerReviewService
				.createCustomerReview(customerReviewForm.getRating(), customerReviewForm.getHeadline(),
						customerReviewForm.getComment(), product, user);
		
		ModelMapper mapper = new ModelMapper();
		return mapper.map(customerReviewModel, CustomerReview.class);
	}

	/*
	 * Create new product. Sample request body:
	 * 
	 * {
		  "name": "Washer2"
	   }
	 * 
	 */
	@ApiOperation(value = "Create a new product", notes = "Cannot create review and user alone with product! Use the sample request body below: \n\n\n{\n&nbsp;&nbsp;   \"name\": \"washer\"\n}")
	@PostMapping({ "products" })
	public Product createProduct(@RequestBody ProductModel productModel)
	{
		ProductModel prod =  productDao.save(productModel);
		ModelMapper mapper = new ModelMapper();
		return mapper.map(prod, Product.class);
	}
	
	@ApiOperation(value = "Get product by id", notes = "Pass in product Id")
	@GetMapping({ "products/{productId:\\d+}" })
	public Product getProduct(@PathVariable final Long productId)
	{
		ProductModel prod =   productDao.findById(productId).orElse(null);
		
		if (prod == null)
		{
			throw new UserNotFoundException(productId);
		}
		ModelMapper mapper = new ModelMapper();
		return mapper.map(prod, Product.class);
	}
	
	@ApiOperation(value = "Get all products")
	@GetMapping({ "products" })
	public List<Product> getProducts()
	{
		List<ProductModel> prodList =  productDao.findAll();
		
		ModelMapper mapper = new ModelMapper();
		return Arrays.asList(mapper.map(prodList, Product[].class));
	}

	
	@ApiOperation(value = "Create a new new", notes = "Sample request body: \n\n\n{\n&nbsp;&nbsp;   \"name\": \"Tom\"\n}")
	@PostMapping({ "users" })
	public User createUser(@RequestBody UserModel user)
	{
		UserModel userModel = userDao.save(user);
		ModelMapper mapper = new ModelMapper();
		return mapper.map(userModel, User.class);
	}
	
	@ApiOperation(value = "Get user by id", notes = "Pass in user Id")
	@GetMapping({ "users/{id}" })
	public User getUser(@PathVariable("id") Long id)
	{
		UserModel userModel = userDao.findById(id).orElse(null);
		
		if (userModel == null)
		{
			throw new UserNotFoundException(id);
		}
		ModelMapper mapper = new ModelMapper();
		return mapper.map(userModel, User.class);
	}
	
	@ApiOperation(value = "Get all users")
	@GetMapping({ "users" })
	public List<User> getAllUser()
	{
		List<UserModel> userModelList =  userDao.findAll();
		ModelMapper mapper = new ModelMapper();
		return Arrays.asList(mapper.map(userModelList, User[].class));
	}
	

//	@DeleteMapping({ "reviews/{reviewId:\\d+}" })
//	public void deleteReview(@PathVariable final Long reviewId)
//	{
////		customerReviewService.deleteCustomerReview(reviewId);
//		
//		/*
//		 * Read: Delete Not Working with JpaRepository
//		 * 
//		 * https://stackoverflow.com/questions/22688402/delete-not-working-with-jparepository
//		 */
//		customerReviewDao.deleteById(reviewId);		
////		CustomerReviewModel customerReviewModel = customerReviewDao.findOne(reviewId);
////		customerReviewDao.delete(customerReviewModel);
//	}
//	
	
	
	
	
	
	
}
