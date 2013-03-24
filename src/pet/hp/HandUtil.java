package pet.hp;

import java.util.*;

import pet.eq.Poker;

/**
 * Utilities for hands (no analysis - see HandInfo)
 */
public class HandUtil {
	
	/**
	 * Compare hands by id, this is equivalent to sorting by date
	 */
	public static final Comparator<Hand> idCmp = new Comparator<Hand>() {
		@Override
		public int compare(Hand h1, Hand h2) {
			int c = h1.id.compareTo(h2.id);
			return c;
		}
	};
	
	public static final Comparator<Seat> seatCmp = new Comparator<Seat>() {
		@Override
		public int compare(Seat s1, Seat s2) {
			return s1.num - s2.num;
		}
	};
	
	/**
	 * get board for street (index from 0).
	 */
	public static String[] getStreetBoard(Hand hand, int streetIndex) {
		switch (hand.game.type) {
			case Game.FCD_TYPE:
			case Game.DSTD_TYPE:
			case Game.DSSD_TYPE:
				return Poker.emptyBoard;
			case Game.HE_TYPE:
			case Game.OM_TYPE:
			case Game.OMHL_TYPE:
			case Game.OM5_TYPE:
			case Game.OM5HL_TYPE:
				return streetIndex > 0 ? Arrays.copyOf(hand.board, streetIndex + 2) : Poker.emptyBoard;
			case Game.OM51_TYPE:
			case Game.OM51HL_TYPE:
				return Arrays.copyOf(hand.board, streetIndex == 0 ? 1 : streetIndex + 2);
			case Game.STUD_TYPE:
			case Game.RAZZ_TYPE:
			case Game.STUDHL_TYPE:
				return streetIndex == 4 && hand.board.length > 0 ? hand.board : Poker.emptyBoard;
			default:
				throw new RuntimeException("unknown game type " + hand.game.type);
		}
	}

	/**
	 * get the final cards this seat had for display purposes returns null if no
	 * known cards, array may contain null if some are unknown
	 */
	public static String[] getFinalCards(int gametype, Seat seat) {
		switch (gametype) {
			case Game.STUD_TYPE:
			case Game.STUDHL_TYPE:
			case Game.RAZZ_TYPE:
				String[] holeCards = seat.finalHoleCards;
				String[] upCards = seat.finalUpCards;
				if (holeCards == null && upCards == null) {
					return null;
				}
				String[] cards = new String[7];
				if (holeCards != null) {
					cards[0] = holeCards.length > 0 ? holeCards[0] : null;
					cards[1] = holeCards.length > 1 ? holeCards[1] : null;
					cards[6] = holeCards.length > 2 ? holeCards[2] : null;
				}
				if (upCards != null) {
					cards[2] = upCards.length > 0 ? upCards[0] : null;
					cards[3] = upCards.length > 1 ? upCards[1] : null;
					cards[4] = upCards.length > 2 ? upCards[2] : null;
					cards[5] = upCards.length > 3 ? upCards[3] : null;
				}
				return cards;
			default:
				// TODO sort them?
				return seat.finalHoleCards;
		}
	}
	
	/**
	 * Get all final hole cards for hand and the blockers
	 */
	public static List<String[]> getFinalCards(Hand hand) {
		List<String[]> cardsList = new ArrayList<>();
		for (Seat seat : hand.seats) {
			String[] cards = getFinalCards(hand.game.type, seat);
			if (cards != null) {
				cardsList.add(cards);
			}
		}
		return cardsList;
	}
	
	private HandUtil() {
		//
	}
	
}
