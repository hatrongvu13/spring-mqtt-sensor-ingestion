package com.htv.lab.mqttingestion.aggregation;

import com.htv.lab.mqttingestion.domain.Entities.SensorAggregate;
import com.htv.lab.mqttingestion.repo.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
public class AggregationJob {
    private final SensorReadingRepository readings;
    private final SensorAggregateRepository aggregates;

    public AggregationJob(SensorReadingRepository r, SensorAggregateRepository a) {
        readings = r;
        aggregates = a;
    }

    @Scheduled(cron = "${app.aggregation.cron:0 * * * * *}")
    @Transactional
    public void aggregatePreviousMinute() {
        Instant end = Instant.now().truncatedTo(ChronoUnit.MINUTES), start = end.minus(1, ChronoUnit.MINUTES);
        var groups = readings.findByRecordedAtGreaterThanEqualAndRecordedAtLessThan(start, end).stream().collect(Collectors.groupingBy(x -> x.getTenantId() + "|" + x.getDeviceId() + "|" + x.getSensorType()));
        groups.values().forEach(g -> {
            var stats = g.stream().mapToDouble(x -> x.getValue()).summaryStatistics();
            var x = g.get(0);
            aggregates.save(new SensorAggregate(x.getTenantId(), x.getDeviceId(), x.getSensorType(), start, end, stats.getCount(), stats.getMin(), stats.getMax(), stats.getAverage()));
        });
    }
}
