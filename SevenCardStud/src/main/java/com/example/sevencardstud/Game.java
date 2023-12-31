package com.example.sevencardstud;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {
    public Hand hands;

    public boolean show;

    public boolean winner;

    public List<List<Card>> foldedHands;

    public List<String> foldedNames;

    public List<String> finalResults;
    //public List<Integer> currentBets;
    public String result;
    //public int winner;
    public int currentPot;
    public int maxBet;
    public final int ante;
    public int numPlayers;
    public boolean completeCalled;
    public boolean bringInCalled;

    private Set<String> playerIds;
    public int smallest;

    public Game() {
        hands = new Hand();
        foldedNames = new ArrayList<>();

        foldedHands = new ArrayList<>();
        finalResults = new ArrayList<>();
        //currentBets = new ArrayList<>();
        result = "";
        currentPot = 6;
        maxBet = 0;
        ante = 1;
        numPlayers = 0;
        completeCalled = false;
        bringInCalled = false;
        show = false;
        winner = false;
        playerIds = new HashSet<>(); // Initialize the set
        smallest = 0;


        //bringIn(hands);


    }

    public int botBrain(List<List<Card>> cardHands, List<Card> currentHand, String name) {
        GamePlay bot = new GamePlay(cardHands, currentHand);
        bot.botInGame(hands.round);
        if (bot.bot.isFolded) {
            foldedHands.add(currentHand);
            foldedNames.add(name);
        }
        return bot.bot.bettingAction;
    }

    public void botCall(List<Integer> bets, int myIndex) {
        if (hands.round == 1) {
            if (bringInCalled) {
                if (maxBet == 0) {
                    maxBet = 1;
                }
                int amount = maxBet - bets.get(myIndex);
                int currentAmount = bets.get(myIndex);
                bets.set(myIndex,currentAmount + amount);
                updateCurrentPot(amount);
            }
            else if (completeCalled) {
                if (maxBet == 0) {
                    maxBet = 2;
                }
                int amount = maxBet - bets.get(myIndex);
                int currentAmount = bets.get(myIndex);
                bets.set(myIndex,currentAmount + amount);
                updateCurrentPot(amount);
            }
        }
        else if (hands.round == 2) {
            if (maxBet == 0) {
                maxBet = 2;
            }
            int amount = maxBet - bets.get(myIndex);
            int currentAmount = bets.get(myIndex);
            bets.set(myIndex,currentAmount + amount);
            updateCurrentPot(amount);
        }
        else if (hands.round > 2) {
            if (maxBet == 0) {
                maxBet = 4;
            }
            int amount = maxBet - bets.get(myIndex);
            int currentAmount = bets.get(myIndex);
            bets.set(myIndex,currentAmount + amount);
            updateCurrentPot(amount);
        }
        hands.newTurn(this, smallest);
    }

    public void botRaise(List<Integer> bets, int myIndex){
        if (hands.round == 1) {
            if (bringInCalled) {
                if (maxBet == 1) {
                    maxBet = 0;
                }
                int amount = maxBet + 2;
                int currentAmount = bets.get(myIndex);
                bets.set(myIndex,currentAmount + amount);
                maxBet = amount;
                updateCurrentPot(amount);
            }
            else if (completeCalled) {
                int amount = maxBet + 2;
                int currentAmount = bets.get(myIndex);
                bets.set(myIndex,currentAmount + amount);
                maxBet = amount;
                updateCurrentPot(amount);
            }
        }
        else if (hands.round == 2) {
            if (maxBet == 0) {
                maxBet = 2;
            }
            int amount = maxBet + 2;
            int currentAmount = bets.get(myIndex);
            bets.set(myIndex,currentAmount + amount);
            maxBet = amount;
            updateCurrentPot(amount);
        }
        else if (hands.round > 2) {
            if (maxBet == 0) {
                maxBet = 4;
            }
            int amount = maxBet + 4;
            int currentAmount = bets.get(myIndex);
            bets.set(myIndex,currentAmount + amount);
            maxBet = amount;
            updateCurrentPot(amount);
        }
        hands.newTurn(this, smallest);
    }

    public void play() {
        if (hands.turn != 7) {
            hands.turn++;
        } else {
            hands.turn = 0;
            //IF ()
            // If 6 rounds haven't passed.
            if (hands.round != 6) {
                // All players went; increment round and start new round.
                hands.round++;
                hands.newRound();
            }
            else {
                // Reset round; game over.
                hands.round = 0;
            }
        }
    }

    public Hand getGameHand() {
        return hands;
    }

    public void playRound() {
        hands.newRound();
    }

    public void playTurn() {
        hands.newTurn(this, smallest);
    }

    public int getCurrentPot() {
        return currentPot;
    }

    public void setCurrentPot(int currentPot) {
        this.currentPot = currentPot;
    }

    public void updateCurrentPot(int update) {
        this.currentPot += update;
    }

    // Game Logic
    public int findPlayerWithSmallestCard(List<List<Card>> cardHands) {
        int smallestCardValue = Integer.MAX_VALUE;
        int smallestCardIndex = -1;

        for (int i = 0; i < cardHands.size(); i++) {
            List<Card> hand = cardHands.get(i);

            // Check if the hand has at least three cards
            if (hand.size() >= 3) {
                Card thirdCard = hand.get(2);
                int thirdCardValue = getCardValue(thirdCard);

                // Update smallest card information
                if (thirdCardValue < smallestCardValue) {
                    smallestCardValue = thirdCardValue;
                    smallestCardIndex = i;
                }
            }
        }
        return smallestCardIndex;
    }

    private int getCardValue(Card card) {
        if ("A".equals(card.getNumber())) {
            return 14;
        } else if ("K".equals(card.getNumber())) {
            return 13;
        } else if ("Q".equals(card.getNumber())) {
            return 12;
        } else if ("J".equals(card.getNumber())) {
            return 11;
        } else {
            return Integer.parseInt(card.getNumber());
        }
    }

    public String gameResult(List<Card> hand) {
        Hand handAnalyzer = new Hand();
        int handValue = handAnalyzer.handValue(hand);
        switch (handValue) {
            case 10:
                finalResults.add("Royal Flush");
                break;
            case 9:
                finalResults.add("Straight Flush");
                break;
            case 8:
                finalResults.add("Four of a Kind");
                break;
            case 7:
                finalResults.add("Full House");
                break;
            case 6:
                finalResults.add("Flush");
                break;
            case 5:
                finalResults.add("Straight");
                break;
            case 4:
                finalResults.add("Three of a Kind");
                break;
            case 3:
                finalResults.add("Two Pair");
                break;
            case 2:
                finalResults.add("One Pair");
                break;
            default:
                finalResults.add("High Card");
                break;
        }
        return result;
    }

    public int bringIn(List<List<Card>> cardHands) {
        int lowestCard = -1;
        for (int i = 0; i < cardHands.size(); i++) {

            List<Card> currentHand = cardHands.get(i);
            List<Card> nextHand = cardHands.get(i+1);
            if (currentHand.get(2).compare(nextHand.get(2)) < 0) {
                lowestCard = i;
            }
            else if (currentHand.get(2).compare(nextHand.get(2)) > 0) {
                lowestCard = i+1;
            }
            else { //Tie
                lowestCard = i;
            }
        }

        return lowestCard;
    }

    public List<Integer> determineWinner(List<List<Card>> cardHands) {
        List<Integer> toReturn = new ArrayList<>();
        int tied = -1;
        List<Card> biggest = cardHands.get(0);
        for (int i = 0; i < cardHands.size(); i++) {

            List<Card> currentHand = cardHands.get(i);
            if (!foldedHands.contains(currentHand) ) {
                for (int j = i + 1; j < cardHands.size(); j++) {
                    List<Card> otherHand = cardHands.get(j);

                    int result = hands.compareHands(biggest, otherHand);

                    if (result == 1) { //biggest is bigger
                    } else if (result == 2) { // other is bigger
                        biggest = otherHand;
                        tied = -1;
                    } else if (result == 0) { //tie
                        tied = cardHands.indexOf(otherHand);
                        //other and biggest is tied
                        // Handle tie-breaking logic
                        //handleTie(currentHand, otherHand);
                    }
                }
            }
        }
        toReturn.add(cardHands.indexOf(biggest));

        if (tied != -1) {
            toReturn.add(cardHands.indexOf(tied));
        }

        return toReturn;
    }

    private int handleTie(List<List<Card>> currentHand, List<List<Card>> otherHand) {
        return -1;
    }

    public boolean isNewPlayer(String playerId) {
        return !playerIds.contains(playerId);
    }

    // Method to add a new player
    public void addPlayer(String playerId) {
        if (isNewPlayer(playerId)) {
            playerIds.add(playerId);
            numPlayers++;
        }
    }

    public void fold(List<Card> hand, String name) {
        foldedHands.add(hand);
        foldedNames.add(name);
        hands.newTurn(this, smallest);
    }
}

