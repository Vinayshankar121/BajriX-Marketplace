package com.bajrix.marketplace.controller;

import com.bajrix.marketplace.dto.*;
import com.bajrix.marketplace.service.MarketplaceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class SellerController {
    private final MarketplaceService marketplaceService;

    public SellerController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    @GetMapping("/listings")
    public List<ListingResponse> myListings(@RequestHeader("X-Seller-Id") Long sellerId) {
        return marketplaceService.getMyListings(sellerId);
    }

    @PostMapping("/listings")
    public ListingResponse add(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @Valid @RequestBody ListingRequest request
    ) {
        return marketplaceService.addListing(sellerId, request);
    }

    @PutMapping("/listings/{listingId}")
    public ListingResponse update(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @PathVariable Long listingId,
            @Valid @RequestBody ListingRequest request
    ) {
        return marketplaceService.updateListing(sellerId, listingId, request);
    }

    @DeleteMapping("/listings/{listingId}")
    public void stopSelling(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @PathVariable Long listingId
    ) {
        marketplaceService.stopSelling(sellerId, listingId);
    }
}
