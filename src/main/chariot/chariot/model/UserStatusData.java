package chariot.model;

public record UserStatusData(
        UserCommon common,
        boolean online,
        boolean playing,
        Opt<String> playingGameId
        ) implements UserStatus {}
