package com.bajrix.marketplace.dto;

import com.bajrix.marketplace.entity.SellerStatus;

public class SellerStatusUpdateRequest {
    private SellerStatus status;

    public SellerStatus getStatus() {
        return status;
    }

    public void setStatus(SellerStatus status) {
        this.status = status;
    }
}
