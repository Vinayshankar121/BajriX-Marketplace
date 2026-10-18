package com.bajrix.marketplace.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import java.math.BigDecimal;

@Entity
@Table(name = "seller_listings", uniqueConstraints = @UniqueConstraint(columnNames = {"seller_id", "product_id"}))
@Check(constraints = "NOT available OR stock = 0 OR minimum_order_quantity <= stock")
public class SellerListing {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "seller_id")
    private Seller seller;
    @ManyToOne(optional = false) @JoinColumn(name = "product_id")
    private Product product;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private Integer stock;
    @Column(nullable = false)
    private Integer minimumOrderQuantity;
    @Column(nullable = false)
    private boolean available = true;
    @Version
    private Long version;

    public SellerListing() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getMinimumOrderQuantity() { return minimumOrderQuantity; }
    public void setMinimumOrderQuantity(Integer minimumOrderQuantity) { this.minimumOrderQuantity = minimumOrderQuantity; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
