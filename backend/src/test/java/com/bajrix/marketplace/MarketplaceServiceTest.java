package com.bajrix.marketplace;

import com.bajrix.marketplace.entity.Product;
import com.bajrix.marketplace.entity.Seller;
import com.bajrix.marketplace.entity.SellerListing;
import com.bajrix.marketplace.entity.SellerStatus;
import com.bajrix.marketplace.repository.ProductRepository;
import com.bajrix.marketplace.repository.SellerListingRepository;
import com.bajrix.marketplace.repository.SellerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MarketplaceServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerListingRepository listingRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Seller approvedSeller;
    private Seller secondApprovedSeller;
    private Seller thirdApprovedSeller;
    private Seller pendingSeller;
    private Seller rejectedSeller;
    private Product cement;
    private Product steel;
    private SellerListing approvedOffer;

    @BeforeEach
    void setUp() {
        listingRepository.deleteAll();
        productRepository.deleteAll();
        sellerRepository.deleteAll();

        approvedSeller = sellerRepository.save(new Seller("Approved Supply", SellerStatus.APPROVED));
        secondApprovedSeller = sellerRepository.save(new Seller("Second Supply", SellerStatus.APPROVED));
        thirdApprovedSeller = sellerRepository.save(new Seller("Inactive Supply", SellerStatus.APPROVED));
        pendingSeller = sellerRepository.save(new Seller("Pending Supply", SellerStatus.PENDING));
        rejectedSeller = sellerRepository.save(new Seller("Rejected Supply", SellerStatus.REJECTED));

        cement = productRepository.save(new Product("PPC Cement", "Cement", "bag", "Portland pozzolana cement"));
        steel = productRepository.save(new Product("TMT Steel", "Steel", "piece", "Reinforcement steel"));

        approvedOffer = saveListing(approvedSeller, cement, "385.00", 50, 10, true);
        saveListing(secondApprovedSeller, cement, "390.00", 20, 5, true);
        saveListing(pendingSeller, cement, "350.00", 100, 1, true);
        saveListing(rejectedSeller, cement, "360.00", 100, 1, true);
        saveListing(secondApprovedSeller, steel, "700.00", 0, 1, true);
        saveListing(thirdApprovedSeller, steel, "680.00", 10, 1, false);
    }

    @Test
    void buyerCanSearchProductsAndOnlySeesApprovedAvailableOffersInPriceOrder() throws Exception {
        mockMvc.perform(get("/api/buyer/products").param("search", "cement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("PPC Cement"));

        mockMvc.perform(get("/api/buyer/products").param("page", "0").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/api/buyer/products").param("size", "101"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/buyer/products/{productId}/offers", cement.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].sellerName", contains("Approved Supply", "Second Supply")))
                .andExpect(jsonPath("$[0].price").value(385.0));
    }

    @Test
    void sellersCanCreateUpdateAndStopOnlyTheirOwnListings() throws Exception {
        String request = listingRequest(steel.getId(), "725.50", 15, 3, true);
        String created = mockMvc.perform(post("/api/seller/listings")
                        .header("X-Seller-Id", approvedSeller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("TMT Steel"))
                .andExpect(jsonPath("$.price").value(725.5))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long listingId = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(put("/api/seller/listings/{listingId}", listingId)
                        .header("X-Seller-Id", approvedSeller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listingRequest(steel.getId(), "730.00", 12, 4, true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(730.0))
                .andExpect(jsonPath("$.stock").value(12));

        mockMvc.perform(put("/api/seller/listings/{listingId}", listingId)
                        .header("X-Seller-Id", secondApprovedSeller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listingRequest(steel.getId(), "1.00", 1, 1, true)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/seller/listings/{listingId}", listingId)
                        .header("X-Seller-Id", approvedSeller.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/seller/listings").header("X-Seller-Id", approvedSeller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].available").value(false));

        mockMvc.perform(get("/api/buyer/products/{productId}/offers", steel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void duplicateAndInvalidListingsAreRejected() throws Exception {
        mockMvc.perform(post("/api/seller/listings")
                        .header("X-Seller-Id", approvedSeller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listingRequest(cement.getId(), "400.00", 10, 1, true)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());

        mockMvc.perform(post("/api/seller/listings")
                        .header("X-Seller-Id", approvedSeller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listingRequest(steel.getId(), "0", -1, 0, true)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/seller/listings")
                        .header("X-Seller-Id", approvedSeller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listingRequest(steel.getId(), "12.345", 5, 3, true)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/seller/listings")
                        .header("X-Seller-Id", approvedSeller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listingRequest(steel.getId(), "12.34", 5, 10, true)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminStatusChangesImmediatelyAffectBuyerVisibility() throws Exception {
        mockMvc.perform(patch("/api/admin/sellers/{sellerId}/status", pendingSeller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPROVED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(get("/api/buyer/products/{productId}/offers", cement.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].sellerName").value(org.hamcrest.Matchers.hasItem("Pending Supply")));

        mockMvc.perform(patch("/api/admin/sellers/{sellerId}/status", approvedSeller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"REJECTED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        mockMvc.perform(get("/api/buyer/products/{productId}/offers", cement.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].sellerName").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.hasItem("Approved Supply"))));
    }

    @Test
    void unknownProductsAndSellersReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/buyer/products/{productId}/offers", Long.MAX_VALUE))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/seller/listings").header("X-Seller-Id", Long.MAX_VALUE))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/seller/listings"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sellerOnlyReceivesTheirOwnListings() throws Exception {
        mockMvc.perform(get("/api/seller/listings").header("X-Seller-Id", approvedSeller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sellerName").value("Approved Supply"));
    }

    @Test
    void staleConcurrentListingUpdatesAreRejected() {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        SellerListing staleCopy = transaction.execute(status ->
                listingRepository.findById(approvedOffer.getId()).orElseThrow());

        transaction.executeWithoutResult(status -> {
            SellerListing currentCopy = listingRepository.findById(approvedOffer.getId()).orElseThrow();
            currentCopy.setPrice(new BigDecimal("386.00"));
            listingRepository.saveAndFlush(currentCopy);
        });

        assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
                    staleCopy.setPrice(new BigDecimal("387.00"));
                    listingRepository.saveAndFlush(staleCopy);
                }))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    private SellerListing saveListing(
            Seller seller,
            Product product,
            String price,
            int stock,
            int minimumOrderQuantity,
            boolean available
    ) {
        SellerListing listing = new SellerListing();
        listing.setSeller(seller);
        listing.setProduct(product);
        listing.setPrice(new BigDecimal(price));
        listing.setStock(stock);
        listing.setMinimumOrderQuantity(minimumOrderQuantity);
        listing.setAvailable(available);
        return listingRepository.save(listing);
    }

    private String listingRequest(Long productId, String price, int stock, int minimumOrderQuantity, boolean available) {
        return """
                {
                  "productId": %d,
                  "price": %s,
                  "stock": %d,
                  "minimumOrderQuantity": %d,
                  "available": %s
                }
                """.formatted(productId, price, stock, minimumOrderQuantity, available);
    }
}
