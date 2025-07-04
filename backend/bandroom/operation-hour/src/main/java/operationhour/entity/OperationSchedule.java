package operationhour.entity;

import eurm.MonthlyWeekPattern;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "operation_schedules")
public class OperationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 운영 대상 공간 등 연결 (예시)
    private Long roomId;
    private Long studioId;

    // 요일별 운영시간 설정
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    // 운영 시작 시간
    @Column(nullable = false)
    private LocalTime openTime;

    private long price;

    // 운영 종료 시간
    @Column(nullable = false)
    private LocalTime closeTime;

    // 매달 주차별 휴무 패턴
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MonthlyWeekPattern monthlyWeekPattern = MonthlyWeekPattern.NONE;

    // 운영시간 설정 활성화 여부
    @Column(nullable = false)
    private boolean active = true;

    // 임시 휴무일 목록
    @OneToMany(mappedBy = "operationSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemporaryHoliday> temporaryHolidays;
}
