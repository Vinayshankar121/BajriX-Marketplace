package com.bajrix.marketplace.dto;

import com.bajrix.marketplace.entity.SellerStatus;

public class SellerSummaryResponse {
    private Long id;
    private String name;
    private SellerStatus status;
    private long listingCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SellerStatus getStatus() {
        return status;
    }

    public void setStatus(SellerStatus status) {
        this.status = status;
    }

    public long getListingCount() {
        return listingCount;
    }

    public void setListingCount(long listingCount) {
        this.listingCount = listingCount;
    }
}
