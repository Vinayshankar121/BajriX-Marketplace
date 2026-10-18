package com.bajrix.marketplace.service.impl;

import com.bajrix.marketplace.dto.*;
import com.bajrix.marketplace.entity.*;
import com.bajrix.marketplace.exception.ConflictException;
import com.bajrix.marketplace.exception.NotFoundException;
import com.bajrix.marketplace.repository.*;
import com.bajrix.marketplace.service.MarketplaceService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarketplaceServiceImpl implements MarketplaceService {
    private static final int MAX_PAGE_SIZE = 100;

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final SellerListingRepository listingRepository;

    public MarketplaceServiceImpl(
            ProductRepository productRepository,
            SellerRepository sellerRepository,
            SellerListingRepository listingRepository
    ) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
        this.listingRepository = listingRepository;
    }

    @Override
    public Page<Product> searchProducts(String search, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        if (search == null || search.isBlank()) {
            return productRepository.findAll(pageable);
        }

        return productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(search.trim(), search.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getOffers(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));

        List<SellerListing> listings = listingRepository.findApprovedVisibleOffers(productId, SellerStatus.APPROVED, 0);

        List<ListingResponse> responses = new ArrayList<>();
        for (SellerListing listing : listings) {
            responses.add(toResponse(listing));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getMyListings(Long sellerId) {
        sellerRepository.findById(sellerId).orElseThrow(() -> new NotFoundException("Seller not found"));

        List<SellerListing> listings = listingRepository.findBySellerIdOrderByProductNameAsc(sellerId);
        List<ListingResponse> responses = new ArrayList<>();
        for (SellerListing listing : listings) {
            responses.add(toResponse(listing));
        }

        return responses;
    }

    @Override
    @Transactional
    public ListingResponse addListing(Long sellerId, ListingRequest request) {
        boolean listingAlreadyExists = listingRepository.existsBySellerIdAndProductId(sellerId, request.getProductId());

        if (listingAlreadyExists) {
            throw new ConflictException("You already have a listing for this product");
        }

        Seller seller = sellerRepository.findById(sellerId).orElseThrow(() -> new NotFoundException("Seller not found"));
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new NotFoundException("Product not found"));

        SellerListing listing = new SellerListing();
        listing.setSeller(seller);
        listing.setProduct(product);
        copyRequest(request, listing);

        SellerListing savedListing = listingRepository.save(listing);
        return toResponse(savedListing);
    }

    @Override
    @Transactional
    public ListingResponse updateListing(Long sellerId, Long listingId, ListingRequest request) {
        SellerListing listing = listingRepository.findByIdAndSellerId(listingId, sellerId)
                .orElseThrow(() -> new NotFoundException("Listing not found for this seller"));

        if (!listing.getProduct().getId().equals(request.getProductId())) {
            throw new IllegalArgumentException("Product cannot be changed. Create a new listing instead.");
        }

        copyRequest(request, listing);
        SellerListing savedListing = listingRepository.save(listing);
        return toResponse(savedListing);
    }

    @Override
    @Transactional
    public void stopSelling(Long sellerId, Long listingId) {
        SellerListing listing = listingRepository.findByIdAndSellerId(listingId, sellerId)
                .orElseThrow(() -> new NotFoundException("Listing not found for this seller"));
        listing.setAvailable(false);
        listingRepository.save(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SellerSummaryResponse> getSellerSummaries() {
        List<Seller> sellers = sellerRepository.findAllByOrderByIdAsc();
        List<SellerSummaryResponse> summaries = new ArrayList<>();

        for (Seller seller : sellers) {
            SellerSummaryResponse dto = new SellerSummaryResponse();
            dto.setId(seller.getId());
            dto.setName(seller.getName());
            dto.setStatus(seller.getStatus());
            dto.setListingCount(listingRepository.countBySellerId(seller.getId()));
            summaries.add(dto);
        }

        return summaries;
    }

    @Override
    @Transactional
    public SellerSummaryResponse updateSellerStatus(Long sellerId, SellerStatusUpdateRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller not found"));

        SellerStatus status = request.getStatus();
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        seller.setStatus(status);
        Seller savedSeller = sellerRepository.save(seller);

        SellerSummaryResponse dto = new SellerSummaryResponse();
        dto.setId(savedSeller.getId());
        dto.setName(savedSeller.getName());
        dto.setStatus(savedSeller.getStatus());
        dto.setListingCount(listingRepository.countBySellerId(savedSeller.getId()));
        return dto;
    }

    private void copyRequest(ListingRequest request, SellerListing listing) {
        listing.setPrice(request.getPrice());
        listing.setStock(request.getStock());
        listing.setMinimumOrderQuantity(request.getMinimumOrderQuantity());
        listing.setAvailable(request.isAvailable());
    }

    private ListingResponse toResponse(SellerListing listing) {
        ListingResponse response = new ListingResponse();
        response.setId(listing.getId());
        response.setProductId(listing.getProduct().getId());
        response.setProductName(listing.getProduct().getName());
        response.setSellerName(listing.getSeller().getName());
        response.setPrice(listing.getPrice());
        response.setStock(listing.getStock());
        response.setMinimumOrderQuantity(listing.getMinimumOrderQuantity());
        response.setAvailable(listing.isAvailable());
        return response;
    }
}
