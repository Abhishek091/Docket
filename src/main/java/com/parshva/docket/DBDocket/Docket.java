package com.parshva.docket.DBDocket;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "docket")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Docket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double hoursWorked;
    private double rate;
    private String supplier;
    private String purchaseOrder;

    private String description;

}

