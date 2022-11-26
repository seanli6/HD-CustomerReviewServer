package ca.homedepot.customerreview.dao;

import ca.homedepot.customerreview.model.MyProductModel;
import ca.homedepot.customerreview.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyProductDao extends JpaRepository<MyProductModel, Long> {
}
