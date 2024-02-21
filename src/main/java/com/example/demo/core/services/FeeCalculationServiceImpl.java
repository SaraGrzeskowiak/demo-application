package com.example.demo.core.services;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.example.demo.core.domain.FeeCalculationSummary;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class FeeCalculationServiceImpl implements FeeCalculationService {

    @Value("${free-time-start:7}")
    private int freeTimeStart;

    @Value("${free-time-end:12}")
    private int freeTimeEnd;

    private Set<Integer> freeHours;

    @PostConstruct
    private void setFreeHours() {
        freeHours = IntStream.rangeClosed(freeTimeStart, freeTimeEnd - 1)
                .boxed()
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public FeeCalculationSummary calculateFee(BigDecimal hourPrice, List<Pair<LocalDateTime, LocalDateTime>> attendances) {
        Map<LocalDate, Map<Integer, Boolean>> payedHours = calculatePayedHours(attendances);
        Duration timeAtSchool = getTimeAtSchool(attendances);
        BigDecimal fee = calculateFee(hourPrice, payedHours);

        return new FeeCalculationSummary(timeAtSchool, fee);
    }

    private Map<LocalDate, Map<Integer, Boolean>> calculatePayedHours(List<Pair<LocalDateTime, LocalDateTime>> attendances) {
        Map<LocalDate, Map<Integer, Boolean>> payedHours = new HashMap<>();
        for (Pair<LocalDateTime, LocalDateTime> attendance : attendances) {
            var entry = attendance.getFirst();
            var exit = attendance.getSecond();

            var currentDate = entry.toLocalDate();
            var currentTime = entry.toLocalTime();
            while (!currentDate.isAfter(exit.toLocalDate())) {
                var currentExitTime = currentDate.isEqual(exit.toLocalDate()) ? exit.toLocalTime() : LocalTime.of(23, 0, 0);
                for (int i = currentTime.getHour(); i <= currentExitTime.getHour(); i++) {
                    if (!freeHours.contains(i)) {
                        payedHours.computeIfAbsent(currentDate, k -> new HashMap<>()).put(i, true);
                    }
                }
                currentDate = currentDate.plusDays(1);
                currentTime = currentTime.withHour(0);
            }
        }
        return payedHours;
    }

    private Duration getTimeAtSchool(List<Pair<LocalDateTime, LocalDateTime>> attendances) {
        return attendances.stream()
                .map(attendance -> Duration.between(attendance.getFirst(), attendance.getSecond()))
                .reduce(Duration.ZERO, Duration::plus);
    }

    private BigDecimal calculateFee(BigDecimal hourPrice, Map<LocalDate, Map<Integer, Boolean>> paidHours) {
        var hoursCount = paidHours.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .mapToInt(bool -> bool ? 1 : 0)
                .sum();

        return hourPrice.multiply(BigDecimal.valueOf(hoursCount));
    }
}
