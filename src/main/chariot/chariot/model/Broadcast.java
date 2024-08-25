package chariot.model;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

public record Broadcast(Tour tour, List<Round> rounds, Opt<Group> group)  {
    public String id() { return tour().id(); }

    public record Tour(String id, String name, String slug, ZonedDateTime createdAt, List<ZonedDateTime> dates, Info info, int tier, String description, URI url, Opt<URI> image, boolean teamTable) {
        public Opt<ZonedDateTime> startDate() { return dates().isEmpty() == false ? Opt.of(dates().getFirst()) : Opt.empty(); }
        public Opt<ZonedDateTime> endDate() { return dates().size() == 2  ? Opt.of(dates().getLast()) : Opt.empty(); }
    }
    public record Round(String id, String name, String slug, ZonedDateTime createdAt, boolean ongoing, boolean finished, Opt<ZonedDateTime> startsAt, Opt<RoundTour> tour, URI url) {}
    public record RoundTour(String id, String name, String slug, String description, ZonedDateTime createdAt, int tier) {}


    public record TourWithLastRound(Tour tour, RoundByUser round, Opt<String> group) {
        public String id() { return tour.id(); }
    }
    public record RoundByUser(String id, String name, String slug, ZonedDateTime createdAt, boolean ongoing, boolean finished, Opt<ZonedDateTime> startsAt, URI url) {}

    public record Group(String name, List<IdAndName> tours) {}

    public record Info(Opt<String> format, Opt<String> tc, Opt<String> players, Opt<FideTC> fideTc) {}
}
