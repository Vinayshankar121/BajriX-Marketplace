package com.bajrix.marketplace.controller;

import com.bajrix.marketplace.dto.SellerStatusUpdateRequest;
import com.bajrix.marketplace.dto.SellerSummaryResponse;
import com.bajrix.marketplace.service.MarketplaceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final MarketplaceService marketplaceService;

    public AdminController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    @GetMapping("/sellers")
    public List<SellerSummaryResponse> sellers() {
        return marketplaceService.getSellerSummaries();
    }

    @PatchMapping("/sellers/{sellerId}/status")
    public SellerSummaryResponse updateSellerStatus(
            @PathVariable Long sellerId,
            @Valid @RequestBody SellerStatusUpdateRequest request
    ) {
        return marketplaceService.updateSellerStatus(sellerId, request);
    }
}
