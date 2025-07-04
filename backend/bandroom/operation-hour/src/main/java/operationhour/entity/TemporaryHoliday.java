package operationhour.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "temporary_holidays")
public class TemporaryHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 OperationSchedule에 속하는 임시 휴무인지 양방향 연관관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_schedule_id", nullable = false)
    private OperationSchedule operationSchedule;

    // 임시 휴무 날짜
    @Column(nullable = false)
    private LocalDate holidayDate;

    // 임시 휴무 사유 (선택사항)
    private String reason;
}