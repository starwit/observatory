package de.starwit.service.jobs;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public class AreaOccupancyJob implements Job {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    
    private AreaOccupancyObservationListener observationListener;
    
    private ObservationJobEntity configEntity;
    private Area polygon;

    private TrajectoryStore trajectoryStore;
    
    private static Duration ANALYZING_WINDOW_LENGTH = Duration.ofSeconds(10);
    private static Duration ANALYZING_INTERVAL = Duration.ofSeconds(10);
    private static double STDDEV_THRESHOLD = 0.001;
    
    public AreaOccupancyJob(ObservationJobEntity configEntity, AreaOccupancyObservationListener observationListener) {
        this.configEntity = configEntity;
        this.polygon = GeometryConverter.areaFrom(configEntity);
        this.observationListener = observationListener;
        this.trajectoryStore = new TrajectoryStore(ANALYZING_WINDOW_LENGTH);

        // Add some random offset to job schedule to spread the load
        int initialDelay = (int) (Math.random() * ANALYZING_INTERVAL.toMillis() / 2);
        this.scheduledExecutor.scheduleAtFixedRate(this::process, initialDelay, ANALYZING_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void pushNewDetection(SaeDetectionDto dto) {
        this.trajectoryStore.addDetection(dto);
    }

    @Override
    public ObservationJobEntity getConfigEntity() {
        return this.configEntity;
    }

    @Override
    public void stop() {
        scheduledExecutor.shutdown();
        try {
            boolean cleanExit = scheduledExecutor.awaitTermination(1, TimeUnit.SECONDS);
            if (!cleanExit) {
                log.warn("Executor tasks did not finish after waiting for 1 second.");
            }
        } catch (InterruptedException e) {
            log.warn("Interruption while waiting for executor shutdown", e);
        }
        
    }

    protected void process() {
        long objectCount = 0;
        List<List<SaeDetectionDto>> trajectories = this.trajectoryStore.getAllValidTrajectories();
        
        for (List<SaeDetectionDto> trajectory : trajectories) {
            List<Point2D> pointTrajectory = GeometryConverter.toCenterPoints(trajectory, this.configEntity.getGeoReferenced());
            Point2D avgPos = getAveragePosition(pointTrajectory);
            if (isStationary(pointTrajectory) && polygon.contains(avgPos)) {
                objectCount++;
                log.info("Stationary " + trajectory.get(0).getObjectId().substring(0, 4));
            }
        }
        
        this.trajectoryStore.purge();

        log.info("Count: " + objectCount);

        // TODO what about non-geo?

        observationListener.onObservation(this.configEntity, this.trajectoryStore.getMostRecentTimestamp().atZone(ZoneOffset.UTC), objectCount);
    }

    /**
     * Determines if the passed trajectory meets our stationary position requirements.
     * Right now that means the average of standard deviation in x and y coordinates is below some threshold.
     * @param pointTrajectory
     * @return
     */
    private boolean isStationary(List<Point2D> pointTrajectory) {
        Point2D avgPos = getAveragePosition(pointTrajectory);
        double squareSumX = 0;
        double squareSumY = 0;
        for (Point2D point : pointTrajectory) {
            squareSumX += Math.pow(avgPos.getX() - point.getX(), 2);
            squareSumY += Math.pow(avgPos.getY() - point.getY(), 2);
        }
        double stdDevX = Math.sqrt(squareSumX / pointTrajectory.size());
        double stdDevY = Math.sqrt(squareSumY / pointTrajectory.size());

        double avgStdDev = (stdDevX + stdDevY) / 2;

        log.info("len " + String.format("%04d", pointTrajectory.size()) + ", avgStdDev: " + String.format("%10.8f", avgStdDev));
    
        return avgStdDev < STDDEV_THRESHOLD;
    }

    private Point2D getAveragePosition(List<Point2D> pointTrajectory) {
        double avgX = pointTrajectory.stream().mapToDouble(p -> p.getX()).summaryStatistics().getAverage();
        double avgY = pointTrajectory.stream().mapToDouble(p -> p.getY()).summaryStatistics().getAverage();

        return new Point2D.Double(avgX, avgY);
    }

}
