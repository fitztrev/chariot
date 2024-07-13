package chariot.api;

import java.util.function.*;

import chariot.model.*;
import chariot.model.Enums.*;
import chariot.api.Builders.*;

public interface ChallengesAuthCommon {

    /**
     * Stream the events reaching the Lichess user in real time.<br>
     * When the stream opens, all current challenges and games are sent.
     */
    Many<Event> connect();

    /**
     * The challenge expires after 20s if not accepted.<br>
     */
    One<Challenge> challenge(String userId, Consumer<ChallengeBuilder> params);

    /**
     * The challenge is kept alive until the stream is closed.<br>
     */
    Many<Challenge> challengeKeepAlive(String userId, Consumer<ChallengeBuilder> params);

    /**
     * Show one challenge.<br>
     * Get details about a challenge, even if it has been recently accepted, canceled or declined.
     * @param challengeId the id of the challenge to show
     */
    One<ChallengeInfo> show(String challengeId);

    One<ChallengeAI> challengeAI(Consumer<ChallengeAIBuilder> params);
    One<Void>     cancelChallenge(String challengeId);
    One<Void>     cancelChallenge(String challengeId, Supplier<char[]> opponentToken);
    One<Void>     acceptChallenge(String challengeId);
    One<Void>     declineChallenge(String challengeId);
    One<Void>     declineChallenge(String challengeId, DeclineReason reason);

    default One<Void> declineChallenge(String challengeId, Function<DeclineReason.Provider, DeclineReason> reason) {
        return declineChallenge(challengeId, reason.apply(DeclineReason.provider()));
    }

    interface ChallengeBuilder extends Clock<ChallengeParams>, ClockCorrespondence<ChallengeParams> {}

    interface ChallengeParams {

        ChallengeParams rated(boolean rated);
        default ChallengeParams rated() { return rated(true); }

        /**
         * @param color Which color you get to play,
         * if the challenge is accepted.
         */
        ChallengeParams color(ColorPref color);

        /**
         * @param variant The variant to use in games
         */
        ChallengeParams variant(GameVariant variant);

        /**
         * @param fen Custom initial position (in FEN).
         * <p>Default: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"<br/>
         * Variant must be standard, fromPosition, or chess960 (if a valid 960 starting positiion), and the game cannot be rated.<br/>
         * Castling moves will use UCI_Chess960 notation, for example <code>e1h1</code> instead of <code>e1g1</code>.<br/>
         */
        ChallengeParams fen(String fen);

        ChallengeParams noAbort();
        ChallengeParams noRematch();
        ChallengeParams noGiveTime();
        ChallengeParams noClaimWin();
        ChallengeParams noEarlyDraw();

        default ChallengeParams color(Function<ColorPref.Provider, ColorPref> color) { return color(color.apply(ColorPref.provider())); }
        default ChallengeParams variant(Function<GameVariant.Provider, GameVariant> variant) {return variant(variant.apply(GameVariant.provider())); }
    }


    interface ChallengeAIBuilder extends Clock<ChallengeAIParams>, ClockCorrespondence<ChallengeAIParams> {}

    interface ChallengeAIParams {

        /**
         * @param level AI strength
         */
        ChallengeAIParams level(Level level);
        default ChallengeAIParams level(Function<Level.Provider, Level> level) { return level(level.apply(Level.provider())); }
        /**
         * @param level AI strength, from 1 to 8. Any other value will result in level 1 or 8.
         */
        default ChallengeAIParams level(int level) { return level(Level.valueOf("_"+Math.clamp(level, 1, 8))); }

        /**
         * @param color Which color you get to play,
         * if the challenge is accepted.
         */
        ChallengeAIParams color(ColorPref color);
        default ChallengeAIParams color(Function<ColorPref.Provider, ColorPref> color) { return color(color.apply(ColorPref.provider())); }


        ChallengeAIParams variant(VariantName variant);
        default ChallengeAIParams variant(Function<VariantName.Provider, VariantName> variant) { return variant(variant.apply(VariantName.provider())); };

        /**
         * @param fen Custom initial position (in FEN).
         * <p>Default: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"<br/>
         * Variant must be standard, fromPosition, or chess960 (if a valid 960 starting positiion), and the game cannot be rated.<br/>
         * Castling moves will use UCI_Chess960 notation, for example <code>e1h1</code> instead of <code>e1g1</code>.<br/>
         */
        ChallengeAIParams fen(String fen);
    }

}

