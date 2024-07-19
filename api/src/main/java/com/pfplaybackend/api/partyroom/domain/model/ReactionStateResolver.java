package com.pfplaybackend.api.partyroom.domain.model;

import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import com.pfplaybackend.api.partyroom.domain.enums.ReactionType;

import java.util.HashMap;
import java.util.Map;

// ReactionState →
public class ReactionStateResolver {

    static Map<ReactionState, ResolvedReaction> reactionStateTable = createReactionStateTable();
    static Map<ReactionState, ReactionState> likeCombinationTable = createLikeCombinationTable();
    static Map<ReactionState, ReactionState> dislikeCombinationTable = createDislikeCombinationTable();
    static Map<ReactionState, ReactionState> grabCombinationTable = createGrabCombinationTable();


    private static Map<ReactionState, ResolvedReaction> createReactionStateTable() {
        Map<ReactionState, ResolvedReaction> map = new HashMap<>();
        map.put(new ReactionState(false, false, false),
                new ResolvedReaction(MotionType.NONE, 0));
        map.put(new ReactionState(true, false, false),
                new ResolvedReaction(MotionType.DANCE_TYPE_1, 1));
        map.put(new ReactionState(true, false, true),
                new ResolvedReaction(MotionType.DANCE_TYPE_2, 3));
        map.put(new ReactionState(false, true, false),
                new ResolvedReaction(MotionType.NONE, 0));
        map.put(new ReactionState(false, true, true),
                new ResolvedReaction(MotionType.NONE, 2));
        return map;
    }

    private static Map<ReactionState, ReactionState> createLikeCombinationTable() {
        Map<ReactionState, ReactionState> map = new HashMap<>();
        map.put(new ReactionState(false, false, false),
                new ReactionState(true, false, false));
        map.put(new ReactionState(true, false, false),
                new ReactionState(true, false, false));
        map.put(new ReactionState(true, false, true),
                new ReactionState(true, false, true));
        map.put(new ReactionState(false, true, false),
                new ReactionState(true, false, false));
        map.put(new ReactionState(false, true, true),
                new ReactionState(true, false, true));
        return map;
    }

    private static Map<ReactionState, ReactionState> createDislikeCombinationTable() {
        Map<ReactionState, ReactionState> map = new HashMap<>();
        map.put(new ReactionState(false, false, false),
                new ReactionState(false, true, false));
        map.put(new ReactionState(true, false, false),
                new ReactionState(false, true, false));
        map.put(new ReactionState(true, false, true),
                new ReactionState(false, true, true));
        map.put(new ReactionState(false, true, false),
                new ReactionState(false, true, false));
        map.put(new ReactionState(false, true, true),
                new ReactionState(false, true, true));
        return map;
    }

    private static Map<ReactionState, ReactionState> createGrabCombinationTable() {
        Map<ReactionState, ReactionState> map = new HashMap<>();
        map.put(new ReactionState(false, false, false),
                new ReactionState(true, false, true));
        map.put(new ReactionState(true, false, false),
                new ReactionState(true, false, true));
        map.put(new ReactionState(true, false, true),
                new ReactionState(true, false, true));
        map.put(new ReactionState(false, true, false),
                new ReactionState(true, false, true));
        map.put(new ReactionState(false, true, true),
                new ReactionState(false, true, true));
        return map;
    }

    public static ReactionState getCombinedReactionState(ReactionState reactionState, ReactionType reactionType) {
        if(reactionType.equals(ReactionType.LIKE)) {
            return likeCombinationTable.get(reactionState);
        }
        if(reactionType.equals(ReactionType.DISLIKE)) {
            return dislikeCombinationTable.get(reactionState);
        }
        if(reactionType.equals(ReactionType.GRAB)) {
            return grabCombinationTable.get(reactionState);
        }
        // TODO Throw Exception;
        return null;
    }

    public static ResolvedReaction resolve(ReactionState reactionState) {
        return reactionStateTable.get(reactionState);
    }
}
