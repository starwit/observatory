package de.starwit.service.jobs;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public class AreaOccupancyJob {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ObservationJobEntity configEntity;
    private Area polygon;
    private Instant lastUpdate;
    private Duration analyzingInterval;
    private TrajectoryStore trajectoryStore;
    private double GEO_DISTANCE_P95_THRESHOLD;
    private double PX_DISTANCE_P95_THRESHOLD_SCALE;
    private Consumer<AreaOccupancyObservation> observationConsumer;
    
    public AreaOccupancyJob(ObservationJobEntity configEntity, Duration analyzingInterval, double geoDistanceP95Threshold, double pxDistanceP95ThresholdScale, Consumer<AreaOccupancyObservation> observationConsumer) {
        this.configEntity = configEntity;
        this.polygon = GeometryConverter.areaFrom(configEntity);
        this.lastUpdate = Instant.ofEpochMilli(0);
        this.analyzingInterval = analyzingInterval;
        this.trajectoryStore = new TrajectoryStore(this.analyzingInterval);
        this.GEO_DISTANCE_P95_THRESHOLD = geoDistanceP95Threshold;
        this.PX_DISTANCE_P95_THRESHOLD_SCALE = pxDistanceP95ThresholdScale;
        this.observationConsumer = observationConsumer;
    }
    
    public ObservationJobEntity getConfigEntity() {
        return this.configEntity;
    }
    
    public Instant getLastUpdate() {
        return lastUpdate;
    }
    
    public Duration getAnalyzingInterval() {
        return analyzingInterval;
    }

    public void addDetection(SaeDetectionDto dto, Instant currentTime) {
        trajectoryStore.addDetection(dto);
        this.lastUpdate = currentTime;
    }

    public void run() {
        long objectCount = 0;
        List<List<SaeDetectionDto>> trajectories = trajectoryStore.getAllHealthyTrajectories();
        
        for (List<SaeDetectionDto> trajectory : trajectories) {
            List<Point2D> pointTrajectory = GeometryConverter.toCenterPoints(trajectory, configEntity.getGeoReferenced());
            Point2D avgPos = getAveragePosition(pointTrajectory);

            if (!polygon.contains(avgPos)) {
                continue;
            }

            // Use bounding box size as stationarity constraint if not geo-referenced (to compensate for perspective)
            boolean stationary = false;
            if (configEntity.getGeoReferenced()) {
                stationary = isStationary(pointTrajectory, GEO_DISTANCE_P95_THRESHOLD);
            } else {
                stationary = isStationary(pointTrajectory, getAverageBoundingBoxDiagonal(trajectory) * PX_DISTANCE_P95_THRESHOLD_SCALE);
            }

            if (stationary) {
                objectCount++;
                log.debug("Stationary " + trajectory.get(0).getObjectId().substring(0, 4));
            }
        }
        
        trajectoryStore.purge();

        observationConsumer.accept(new AreaOccupancyObservation(configEntity, trajectoryStore.getMostRecentTimestamp().atZone(ZoneOffset.UTC), objectCount));
    }

    /**
     * Determines if the passed trajectory meets our stationary position requirements.
     * Right now that means the 95 percentile of distances to the average position (within the recorded track) is smaller than a certain threshold.
     * @param pointTrajectory
     * @return
     */
    private boolean isStationary(List<Point2D> pointTrajectory, double threshold) {
        Point2D avgPos = getAveragePosition(pointTrajectory);

        double[] distances = pointTrajectory.stream().map(p -> p.distance(avgPos)).mapToDouble(d -> d).toArray();
        double distanceP95 = new Percentile().evaluate(distances, 0.95);

        boolean stationary = distanceP95 < threshold;

        log.debug("trajLen " + String.format("%04d", pointTrajectory.size()) + ", distance P95: " + String.format("%10.8f", distanceP95) + ", threshold: " + String.format("%10.8f", threshold));
    
        return stationary;
    }

    private Point2D getAveragePosition(List<Point2D> pointTrajectory) {
        double avgX = pointTrajectory.stream().mapToDouble(p -> p.getX()).summaryStatistics().getAverage();
        double avgY = pointTrajectory.stream().mapToDouble(p -> p.getY()).summaryStatistics().getAverage();

        return new Point2D.Double(avgX, avgY);
    }

    private double getAverageBoundingBoxDiagonal(List<SaeDetectionDto> dtos) {
        double avgDeltaX = dtos.stream().mapToDouble(d -> d.getMaxX() - d.getMinX()).summaryStatistics().getAverage();
        double avgDeltaY = dtos.stream().mapToDouble(d -> d.getMaxY() - d.getMinY()).summaryStatistics().getAverage();

        return Math.sqrt(Math.pow(avgDeltaX, 2) + Math.pow(avgDeltaY, 2));
    }
}
