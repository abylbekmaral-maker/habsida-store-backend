package com.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "store_breaks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreBreak extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_hour_id", nullable = false)
    private StoreHour storeHour;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}