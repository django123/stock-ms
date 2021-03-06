package liss.nvms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import liss.nvms.model.PurchasePriceEntity;

public interface PurchasePriceRepository extends JpaRepository<PurchasePriceEntity, String> {

}
