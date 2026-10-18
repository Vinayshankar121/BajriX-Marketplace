package com.bajrix.marketplace.repository;

import com.bajrix.marketplace.entity.SellerListing;
import com.bajrix.marketplace.entity.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerListingRepository extends JpaRepository<SellerListing, Long> {
    List<SellerListing> findBySellerIdOrderByProductNameAsc(Long sellerId);
    Optional<SellerListing> findByIdAndSellerId(Long id, Long sellerId);
    boolean existsBySellerIdAndProductId(Long sellerId, Long productId);
    long countBySellerId(Long sellerId);

    @Query("SELECT sl FROM SellerListing sl JOIN sl.seller s WHERE sl.product.id = :productId AND s.status = :status AND sl.available = true AND sl.stock > :stock ORDER BY sl.price ASC")
    List<SellerListing> findApprovedVisibleOffers(@Param("productId") Long productId, @Param("status") SellerStatus status, @Param("stock") Integer stock);
}

