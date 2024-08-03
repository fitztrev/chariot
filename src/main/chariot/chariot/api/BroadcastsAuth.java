package chariot.api;

import java.net.URI;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

import chariot.model.*;
import chariot.model.Broadcast.Round;

public interface BroadcastsAuth extends Broadcasts {

    One<Broadcast> create(Consumer<BroadcastBuilder> params);
    One<Void>      update(String tourId, Consumer<BroadcastBuilder> params);

    One<MyRound>   createRound(String tourId, Consumer<RoundBuilder> params);
    One<Round>     updateRound(String roundId, Consumer<RoundBuilder> params);

    /**
     * Deletes all games in a round
     */
    One<Void>      resetRound(String roundId);

    /**
     * Update your broadcast with new PGN. Only for broadcast without a source URL.<br>
     *
     * @param roundId The broadcast round ID (8 characters).
     * @param pgn The PGN. It can contain up to 64 games, separated by a double new line.
     * @return The PGN tags and number of moves which were updated.
     */
    Many<PushResult>   pushPgnByRoundId(String roundId, String pgn);

    /**
     * Stream all broadcast rounds you are a member of.<br>
     *
     * Also includes broadcasts rounds you did not create, but were invited to. Also
     * includes broadcasts rounds where you're a non-writing member. See the
     * {@code writeable} flag in the response. Rounds are ordered by rank, which is roughly
     * chronological, most recent first, slightly pondered with popularity.
     */
    Many<MyRound> myRounds(Consumer<RoundsParameters> params);

    /**
     * See {@link #myRounds(Consumer)}
     */
    default Many<MyRound> myRounds() { return myRounds(__ -> {}); }

    interface BroadcastBuilder {

        /**
         * @param name Name of the broadcast tournament.<br/>
         *             Length must be between 3 and 80 characters.<br/>
         *        Example: Sinquefield Cup
         */
        BroadcastBuilder name(String name);

        /**
         * @param description Optional long description of the broadcast. Markdown is supported.<br/>
         *        Length must be less than 20,000 characters.
         */
        BroadcastBuilder description(String description);

        BroadcastBuilder infoTimeControl(String timeControl);
        BroadcastBuilder infoTournamentFormat(String format);
        BroadcastBuilder infoFeaturedPlayers(String players);
        default BroadcastBuilder infoFeaturedPlayers(String player1, String player2) {
            return infoFeaturedPlayers(String.join(",", player1, player2));
        };
        default BroadcastBuilder infoFeaturedPlayers(String player1, String player2, String player3) {
            return infoFeaturedPlayers(String.join(",", player1, player2, player3));
        }
        default BroadcastBuilder infoFeaturedPlayers(String player1, String player2, String player3, String player4) {
            return infoFeaturedPlayers(String.join(",", player1, player2, player3, player4));
        }

        /**
         * Only for admins.
         * @param tier Broadcast tier. [3 4 5]. 3 normal, 4 high, 5 best.
         */
        BroadcastBuilder tier(int tier);

        /**
         * Compute and display a simple leaderboard based on game results
         */
        BroadcastBuilder autoLeaderboard(boolean autoLeaderboard);

        /**
         * Compute and display a simple leaderboard based on game results
         */
        default BroadcastBuilder autoLeaderboard() { return autoLeaderboard(true); }

        /**
         * Show a team leaderboard.
         */
        BroadcastBuilder teamTable(boolean teamTable);

        /**
         * Show a team leaderboard.
         */
        default BroadcastBuilder teamTable() { return teamTable(true); }

        /**
         * Replace player names, ratings and titles.<br>
         *
         * When broadcast rounds receive PGN data (pushed or polled),
         * it is possible to apply player information replacement of the tags in the PGN,
         * based on the player name tags.<br>
         * The replacements are specified in the broadcast with one line per player.<br>
         * It is possible to augment the PGN tags with information from their FIDE profile by matching a player name to a FIDE ID,<br>
         * or by manually specifying values of PGN tags.<br>
         *
         * <b>FIDE ID format</b><br>
         * {@code <player name> = <FIDE ID>}<br>
         * {@code <player name> = <FIDE ID> / <title>}<br>
         * <b>Manual format</b><br>
         * {@code <player name> / <rating> / <title> / <new name>}<br>
         * <br>
         * The {@code <rating>}, {@code <title>} and {@code <new name>} are optional and can be left blank.<br>
         * Player names ignore case and punctuation, and match all possible combinations of 2 words:<br>
         * "Jorge Rick Vito" will match "Jorge Rick", "jorge vito", "Rick, Vito", etc.<br>
         *
         * Example:
         * {@snippet :
         *   var broadcast = client.broadcasts().create(params -> params
         *      .name("Broadcast Name")
         *      .description("Broadcast Description")
         *      .players("""
         *          Anna = 14111330
         *          Art = 7818424 / NM
         *          Some One / 2700 / WGM / Numero Uno
         *          Someone Else / / / Deuce
         *          Other Player / 1300 / /
         *          """));
         * }
         */
        BroadcastBuilder players(String players);

        /**
         * Assign players to teams.<br>
         * By default the PGN tags WhiteTeam and BlackTeam are used.<br>
         *
         * One line per player, formatted as such:<br>
         * {@code <Team name>;<FIDE ID or Player name>}<br>
         *
         * {@snippet :
         *   var broadcast = client.broadcasts().create(params -> params
         *      .name("Broadcast Name")
         *      .description("Broadcast Description")
         *      .teamTable()
         *      .teams("""
         *          Team Cats;3408230
         *          Team Dogs;Scooby Doo
         *          """));
         * }
         */
        BroadcastBuilder teams(String teams);
    }

    interface RoundBuilder {

        /**
         * @param name Name of the broadcast round. Length must be between 3 and 80 characters.<br/>
         *        Example: Round 1
         */
        public RoundBuilder name(String name);

        /**
         * @param syncUrl URL that Lichess will poll to get updates about the games.<br/>
         *                It must be publicly accessible from the Internet.<br/>
         *                If the syncUrl is missing, then the broadcast needs to be fed by pushing PGN to it.<br/>
         *        Example: https://myserver.org/myevent/round-10/games.pgn
         */
        RoundBuilder syncUrl(URI syncUrl);

        /**
         * @param syncUrl URL that Lichess will poll to get updates about the games.<br/>
         *                It must be publicly accessible from the Internet.<br/>
         *                If the syncUrl is missing, then the broadcast needs to be fed by pushing PGN to it.<br/>
         *        Example: https://myserver.org/myevent/round-10/games.pgn
         */
        default RoundBuilder syncUrl(String syncUrl) { return syncUrl(URI.create(syncUrl)); }

        /**
         * @param syncUrlRound Required if syncUrl contains a livechesscloud link.
         */
        RoundBuilder syncUrlRound(int syncUrlRound);

        /**
         * @param delay The delay of the broadcast, in seconds
         */
        RoundBuilder delay(long delay);

        /**
         * @param delay The delay duration of the broadcast
         */
        default RoundBuilder delay(Duration delay) { return delay(delay.toSeconds()); }

        /**
         * Only for Admins. Waiting time for each poll, in seconds. Between 2 and 60 seconds.
         * @param period seconds wait between polls
         */
        RoundBuilder period(int period);

        /**
         * Only for Admins. Waiting time for each poll. Between 2 and 60 seconds.
         * @param period duration wait time between polls
         */
         default RoundBuilder period(Duration period) { return period((int) period.toSeconds()); }

        /**
         * @param startsAt Timestamp in milliseconds of broadcast round start.<br/>
         *                 Leave empty to manually start the broadcast round.
         */
        RoundBuilder startsAt(long startsAt);

        /**
         * @param startsAt Broadcast round start.<br/>
         *                 Leave empty to manually start the broadcast round.
         */
        default RoundBuilder startsAt(ZonedDateTime startsAt) { return startsAt(startsAt.toInstant().toEpochMilli()); }

        /**
         * @param now Broadcast round start, from a given {@code ZonedDateTime.now()} instance.
         */
        default RoundBuilder startsAt(Function<ZonedDateTime, ZonedDateTime> now) { return startsAt(now.apply(ZonedDateTime.now())); }


    }

    interface RoundsParameters {
        /**
         * @param nb How many rounds to get.<br>
         *           {@code >= 1}
         *           Example: {@code nb=20}
         */
        RoundsParameters nb(int nb);
    }

}
