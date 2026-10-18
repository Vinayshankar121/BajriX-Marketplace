package com.bajrix.marketplace.config;

import com.bajrix.marketplace.entity.*;
import com.bajrix.marketplace.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedData(SellerRepository sellers, ProductRepository products, SellerListingRepository listings) {
        return args -> {
            if (sellers.count() > 0) {
                return;
            }

            Seller shree = sellers.save(new Seller("Shree Traders", SellerStatus.APPROVED));
            Seller buildMart = sellers.save(new Seller("Build Mart", SellerStatus.APPROVED));
            Seller future = sellers.save(new Seller("Future Supplies", SellerStatus.PENDING));
            Seller skyline = sellers.save(new Seller("Skyline Materials", SellerStatus.REJECTED));
            Seller urban = sellers.save(new Seller("Urban Build Co.", SellerStatus.APPROVED));

            Product cement = products.save(new Product("UltraTech PPC Cement 50 kg", "Cement", "bag", "Strong and durable PPC cement."));
            Product steel = products.save(new Product("TMT Steel Bar 12 mm", "Steel", "piece", "High strength reinforcement steel bar."));
            Product sand = products.save(new Product("River Sand", "Sand", "ton", "Clean river sand for construction."));
            Product bricks = products.save(new Product("Fly Ash Bricks", "Brick", "piece", "Lightweight and durable masonry blocks."));
            Product paint = products.save(new Product("Apex Exterior Paint", "Paint", "litre", "Weather-resistant wall finish."));

            save(listings, shree, cement, "390", 500, 10, true);
            save(listings, buildMart, cement, "385", 250, 20, true);
            save(listings, future, cement, "375", 900, 10, true);
            save(listings, shree, steel, "680", 100, 5, true);
            save(listings, buildMart, sand, "1250", 0, 1, true);
            save(listings, urban, sand, "1180", 40, 1, true);
            save(listings, shree, bricks, "12", 1200, 100, true);
            save(listings, urban, paint, "245", 320, 20, true);
            save(listings, skyline, paint, "260", 80, 15, false);
        };
    }

    private void save(SellerListingRepository repo, Seller seller, Product product, String price, int stock, int moq, boolean available) {
        SellerListing listing = new SellerListing();
        listing.setSeller(seller);
        listing.setProduct(product);
        listing.setPrice(new BigDecimal(price));
        listing.setStock(stock);
        listing.setMinimumOrderQuantity(moq);
        listing.setAvailable(available);
        repo.save(listing);
    }
}
