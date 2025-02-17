package com.github.zipcodewilmington.casino.games.cardgames.poker.threecardpoker;

import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.casino.games.cardgames.*;

import java.util.Collections;
import java.util.List;

public class ThreeCardPokerEngine {

    public ThreeCardPokerEngine() {
    }

    public boolean dealerHandQualifies(PokerHand hand) {
        return getHandPointValue(hand) > 111;
    }
    public PokerPlayer computeWinner(PokerPlayer player1, PokerPlayer player2) {
        int player1Points = getHandPointValue(player1.getHand());
        int player2Points = getHandPointValue(player2.getHand());

        if (player1Points == player2Points) {
            switch (player1.getHand().getRank()) {
                case FLUSH:
                    return flushTieBreaker(player1, player2);
                case PAIR:
                    return pairTieBreaker(player1, player2);
                case HIGH_CARD:
                    return highCardTieBreaker(player1, player2);
            }
        }
        if (player1Points > player2Points) {
            return player1;
        } else if (player1Points < player2Points) {
            return player2;
        }
        return null;
    }

    public int getHandPointValue(PokerHand hand) {
        ThreePokerHandRank rank = hand.getRank();
        int highestCardVal = hand.getHighestCard(hand, false).getRank().getValue();
        switch (rank) {
            case STRAIGHT_FLUSH:
                return ThreePokerHandRank.STRAIGHT_FLUSH.POINTS + highestCardVal;
            case THREE_OF_A_KIND:
                if (hand.getHighestCard(hand, false).getRank() == CardRank.ACE) {
                    highestCardVal = 14;
                }
                return ThreePokerHandRank.THREE_OF_A_KIND.POINTS + highestCardVal;
            case STRAIGHT:
                return ThreePokerHandRank.STRAIGHT.POINTS + highestCardVal;
            case FLUSH:
                return ThreePokerHandRank.FLUSH.POINTS + highestCardVal;
            case PAIR:
                return ThreePokerHandRank.PAIR.getPoints();
            case HIGH_CARD:
                return ThreePokerHandRank.HIGH_CARD.getPoints() + highestCardVal;
        }
        return 0;
    }

    public PokerPlayer flushTieBreaker(PokerPlayer player1, PokerPlayer player2) throws IllegalArgumentException {
        PokerHand player1Hand = player1.getHand();
        PokerHand player2Hand = player2.getHand();
        if (player1Hand.getRank() != ThreePokerHandRank.FLUSH || player2Hand.getRank() != ThreePokerHandRank.FLUSH) {
            throw new IllegalArgumentException("Both hands must be a flush");
        }

        while (player1Hand.getNumberOfCards() > 0 && player2Hand.getNumberOfCards() > 0) {
            PlayingCard card1 = player1Hand.getHighestCard(player1Hand, true);
            PlayingCard card2 = player2Hand.getHighestCard(player2Hand, true);

            if (card1.getRank().getValue() > card2.getRank().getValue()) {
                return player1;
            } else if (card2.getRank().getValue() > card1.getRank().getValue()) {
                return player2;
            }
            player1Hand.removeCard(card1);
            player2Hand.removeCard(card2);
        }
        return null;
    }

    public PokerPlayer pairTieBreaker(PokerPlayer player1, PokerPlayer player2) throws IllegalArgumentException {
        PokerHand hand1 = player1.getHand();
        PokerHand hand2 = player2.getHand();

        if (hand1.getRank() != ThreePokerHandRank.PAIR || hand2.getRank() != ThreePokerHandRank.PAIR) {
            throw new IllegalArgumentException("Both hands must be a pair");
        }

        int pairRank1 = hand1.getPairRanking(hand1);
        int pairRank2 = hand2.getPairRanking(hand2);

        if (pairRank1 > 0 && pairRank1 > pairRank2) {
            return player1;
        } else if (pairRank2 > 0 && pairRank2 > pairRank1) {
            return player2;
        }

        int oddRank1 = hand1.getPairOddRanking(hand1);
        int oddRank2 = hand2.getPairOddRanking(hand2);
        if(oddRank1 > 0 && oddRank1 > oddRank2) {
            return player1;
        } else if (oddRank2 > 0 && oddRank2 > oddRank1) {
            return player2;
        }

        return null;
    }

    public PokerPlayer highCardTieBreaker(PokerPlayer player1, PokerPlayer player2) throws IllegalArgumentException {
        PokerHand hand1 = player1.getHand();
        PokerHand hand2 = player2.getHand();
        if (hand1.getRank() != ThreePokerHandRank.HIGH_CARD || hand2.getRank() != ThreePokerHandRank.HIGH_CARD) {
            throw new IllegalArgumentException("Both hands must be high card");
        }

        while (hand1.getNumberOfCards() > 0 && hand2.getNumberOfCards() > 0) {
            PlayingCard card1 = hand1.getHighestCard(hand1, true);
            PlayingCard card2 = hand2.getHighestCard(hand2, true);

            if (card1.getRank().getValue() > card2.getRank().getValue()) {
                return player1;
            } else if (card2.getRank().getValue() > card1.getRank().getValue()) {
                return player2;
            }
            hand1.removeCard(card1);
            hand2.removeCard(card2);
        }
        return null;
    }

}
