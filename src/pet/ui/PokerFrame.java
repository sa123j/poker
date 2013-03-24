package pet.ui;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.*;

import pet.hp.*;
import pet.hp.impl.PSParser;
import pet.hp.info.*;
import pet.ui.eq.*;
import pet.ui.graph.GraphData;
import pet.ui.hp.*;
import pet.ui.hud.HUDManager;
import pet.ui.replay.ReplayPanel;

/**
 * Poker equity GUI tool.
 */
public class PokerFrame extends JFrame {
	
	// left triangle 25c0, right triangle 25b6 
	public static final String LEFT_TRI = "\u25c0";
	public static final String RIGHT_TRI = "\u25b6";
	public static final Font boldfont = new Font("SansSerif", Font.BOLD, 12);
	public static final Font bigfont = new Font("SansSerif", Font.BOLD, 24);
	
	/** all the parsed data */
	private final History history = new History();
	/** thread that feeds the parser */
	private final FollowThread followThread = new FollowThread(new PSParser(history));
	/** data analysis */
	private final Info info = new Info();
	private final JTabbedPane tabs = new JTabbedPane();
	private final JTabbedPane eqTabs = new JTabbedPane();
	private final JTabbedPane hisTabs = new JTabbedPane();
	private final ReplayPanel replayPanel = new ReplayPanel();
	private final BankrollPanel bankrollPanel = new BankrollPanel();
	private final LastHandPanel lastHandPanel = new LastHandPanel();
	private final HistoryPanel historyPanel = new HistoryPanel();
	private final HandsPanel handsPanel = new HandsPanel();
	private final HoldemCalcPanel holdemPanel = new HoldemCalcPanel("Hold'em", 1, 2);
	private final HoldemCalcPanel omahaPanel = new HoldemCalcPanel("Omaha", 2, 4);
	private final HoldemCalcPanel omaha5Panel = new HoldemCalcPanel("5 Card Omaha", 2, 5);
	private final DrawCalcPanel drawPanel = new DrawCalcPanel();
	private final GamesPanel gamesPanel = new GamesPanel();
	private final PlayerPanel playerPanel = new PlayerPanel();
	private final HUDManager hudManager = new HUDManager();
	private final AboutPanel aboutPanel = new AboutPanel();
	private final StudCalcPanel studPanel = new StudCalcPanel();
	
	public PokerFrame() {
		super("Poker Equity Tool");
		try (InputStream iconIs = getClass().getResourceAsStream("/pet32.png")) {
			BufferedImage icon = ImageIO.read(iconIs);
		    setIconImage(icon);
		    //com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
		    //app.setDockIconImage (icon);
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tabs.addTab("Equity", eqTabs);
		tabs.addTab("History", hisTabs);
		tabs.addTab("About", aboutPanel);
		
		eqTabs.addTab("Hold'em", holdemPanel);
		eqTabs.addTab("Omaha", omahaPanel);
		eqTabs.addTab("5 Card Omaha", omaha5Panel);
		eqTabs.addTab("Draw", drawPanel);
		eqTabs.addTab("Stud", studPanel);
		
		hisTabs.addTab("Files", historyPanel);
		hisTabs.addTab("Players", playerPanel);
		hisTabs.addTab("Games", gamesPanel);
		hisTabs.addTab("Tournaments", new TournPanel());
		hisTabs.addTab("Graph", bankrollPanel);
		hisTabs.addTab("Hands", handsPanel);
		hisTabs.addTab("Replay", replayPanel);
		hisTabs.addTab("Last Hand", lastHandPanel);
		
		history.addListener(lastHandPanel);
		history.addListener(gamesPanel);
		history.addListener(info);
		history.addListener(hudManager);
		
		followThread.addListener(historyPanel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(tabs);
		// XXX can cause infinite loop in jeditorpane layout
		pack();
	}
	
	public void start() {
		followThread.start();
	}
	
	public Info getInfo() {
		return info;
	}
	
	public History getHistory() {
		return history;
	}
	
	/** display hand in replayer */
	public void replayHand(Hand hand) {
		replayPanel.setHand(hand);
		hisTabs.setSelectedComponent(replayPanel);
		tabs.setSelectedComponent(hisTabs);
	}
	
	/** display hand in hand panel */
	public void displayHand(Hand hand) {
		lastHandPanel.showHand(hand);
		hisTabs.setSelectedComponent(lastHandPanel);
		tabs.setSelectedComponent(hisTabs);
	}
	
	public void displayBankRoll(GraphData bankRoll) {
		bankrollPanel.setData(bankRoll);
		hisTabs.setSelectedComponent(bankrollPanel);
		tabs.setSelectedComponent(hisTabs);
	}
	
	public FollowThread getFollow() {
		return followThread;
	}
	
	/** display hands in hands tab */
	public void displayHands(String name, String gameid) {
		handsPanel.displayHands(name, gameid);
		hisTabs.setSelectedComponent(handsPanel);
		tabs.setSelectedComponent(hisTabs);
	}

	public void displayHands(long tournid) {
		handsPanel.displayHands(tournid);
		hisTabs.setSelectedComponent(handsPanel);
		tabs.setSelectedComponent(hisTabs);
	}
	
	/**
	 * display the calc panel for the game type and return it so you can set the
	 * hand
	 */
	public CalcPanel displayCalcPanel(int gameType) {
		CalcPanel p;
		switch (gameType) {
			case Game.HE_TYPE: 
				p = holdemPanel;
				break;
			case Game.OM_TYPE:
			case Game.OMHL_TYPE:
				p = omahaPanel;
				break;
			case Game.DSTD_TYPE:
			case Game.FCD_TYPE:
			case Game.DSSD_TYPE: 
				p = drawPanel;
				break;
			case Game.STUD_TYPE:
			case Game.STUDHL_TYPE:
			case Game.RAZZ_TYPE:
				p = studPanel;
				break;
			default:
				throw new RuntimeException("no panel for game " + gameType);
		}
		eqTabs.setSelectedComponent(p);
		tabs.setSelectedComponent(eqTabs);
		return p;
	}

	public void displayPlayer(String player) {
		playerPanel.displayPlayer(player);
		hisTabs.setSelectedComponent(playerPanel);
		tabs.setSelectedComponent(hisTabs);
	}

	public HUDManager getHudManager() {
		return hudManager;
	}
	
}
