package org.baeldung.um.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
final class MetricsExporter {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MetricRepository metricRepository;

    //

    @Scheduled(fixedRate = 1000 * 30) // every 30 seconds
    public void exportMetrics() {
        metricRepository.findAll().forEach(this::log);
    }

    private void log(final Metric<?> m) {
         log.info("Reporting metric {}={}", m.getName(), m.getValue());
        metricRepository.reset(m.getName());
    }

}
