package com.bajrix.marketplace.controller;

import com.bajrix.marketplace.dto.ListingResponse;
import com.bajrix.marketplace.dto.ProductPageResponse;
import com.bajrix.marketplace.entity.Product;
import com.bajrix.marketplace.service.MarketplaceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buyer")
public class BuyerController {
    private final MarketplaceService marketplaceService;

    public BuyerController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    @GetMapping("/products")
    public ProductPageResponse<Product> products(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ProductPageResponse.from(marketplaceService.searchProducts(search, page, size));
    }

    @GetMapping("/products/{productId}/offers")
    public List<ListingResponse> offers(@PathVariable Long productId) {
        return marketplaceService.getOffers(productId);
    }
}
