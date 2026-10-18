package com.bajrix.marketplace.service;

import com.bajrix.marketplace.dto.ListingRequest;
import com.bajrix.marketplace.dto.ListingResponse;
import com.bajrix.marketplace.dto.SellerStatusUpdateRequest;
import com.bajrix.marketplace.dto.SellerSummaryResponse;
import com.bajrix.marketplace.entity.Product;
import org.springframework.data.domain.Page;
import java.util.List;

public interface MarketplaceService {
    Page<Product> searchProducts(String search, int page, int size);
    List<ListingResponse> getOffers(Long productId);
    List<ListingResponse> getMyListings(Long sellerId);
    ListingResponse addListing(Long sellerId, ListingRequest request);
    ListingResponse updateListing(Long sellerId, Long listingId, ListingRequest request);
    void stopSelling(Long sellerId, Long listingId);
    List<SellerSummaryResponse> getSellerSummaries();
    SellerSummaryResponse updateSellerStatus(Long sellerId, SellerStatusUpdateRequest request);
}

