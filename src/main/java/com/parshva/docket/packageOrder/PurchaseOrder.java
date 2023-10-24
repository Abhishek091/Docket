package com.parshva.docket.packageOrder;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PurchaseOrder {
    private String poNumber;
    private String supplier;
}
