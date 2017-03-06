package org.baeldung.um.service.impl;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.SortedMap;

@Component
final class MetricsExporterDropwizard {

     @Autowired
     private MetricRegistry metricRegistry;

     //

     @SuppressWarnings("unused")
     @Scheduled(fixedRate = 1000 * 30) // every 30 seconds
     public void exportMetrics() {
     final SortedMap<String, Counter> counters = metricRegistry.getCounters();
     final SortedMap<String, Gauge> gauges = metricRegistry.getGauges();
     System.out.println();
     }

}
