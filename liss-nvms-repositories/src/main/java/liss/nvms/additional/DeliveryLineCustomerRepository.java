package liss.nvms.additional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import liss.nvms.Clivraison.DeliveryLineCustomerEntity;

@Repository
public interface DeliveryLineCustomerRepository extends JpaRepository<DeliveryLineCustomerEntity, String>{

	
}
