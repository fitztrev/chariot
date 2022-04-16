package chariot.model;

import java.util.List;
import java.util.Optional;

import chariot.model.Enums.Color;

public sealed interface ExploreResult extends Model {

    @SuppressWarnings("unused") // needs to explicitly implements Model
    record OpeningDB (
            int white,
            int draws,
            int black,
            List<DBMove> moves,
            List<DBGame> topGames,
            List<DBGame> recentGames,
            Optional<EROpening> opening) implements ExploreResult, Model {}

    @SuppressWarnings("unused") // needs to explicitly implements Model
    record OpeningPlayer(
            int white,
            int draws,
            int black,
            List<PlayerMove> moves,
            List<PlayerGame> topGames,
            List<PlayerGame> recentGames,
            Optional<EROpening> opening) implements ExploreResult, Model {}

    public record DBMove(
            String uci,
            String san,
            int white,
            int draws,
            int black,
            int averageRating,
            Optional<DBGame> game) {}

    public record DBGame(
            String uci,
            String id,
            Optional<Color> winner,
            ERPlayer white,
            ERPlayer black,
            int year,
            String month) {}

    public record PlayerMove(
            String uci,
            String san,
            int white,
            int draws,
            int black,
            int averageOpponentRating,
            int performance,
            Optional<PlayerGame> game) {}

    public record PlayerGame(
            String uci,
            String id,
            Optional<Color> winner,
            String speed,
            String mode,
            ERPlayer white,
            ERPlayer black,
            int year,
            String month) {}

    public record ERPlayer(String name, Integer rating) {}
    public record EROpening(String eco, String name) {}
}
