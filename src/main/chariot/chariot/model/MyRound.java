package chariot.model;

import java.net.URI;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

public record MyRound(Tour tour, Round round, Study study) {
    public record Tour(String id, String slug, String name, String description, ZonedDateTime createdAt, int tier) {}
    public record Round(String id, String slug, String name, ZonedDateTime createdAt, Optional<ZonedDateTime> startsAt, boolean ongoing, boolean finished, URI url, Duration delay) {}
    public record Study(boolean writeable) {}
}
