package com.charging.shelter.entity;

import com.charging.shelter.enums.InspectionType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inspections")
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InspectionType type;

    @Column(nullable = false)
    private Long officerId;

    private Long shelterId;

    private String location;

    @Column(nullable = false)
    private Boolean hasIssue;

    private String issueDescription;

    private String imageUrl;

    private String handleSuggestion;

    private Boolean isResolved;

    private LocalDateTime inspectionTime;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (inspectionTime == null) {
            inspectionTime = LocalDateTime.now();
        }
    }
}
