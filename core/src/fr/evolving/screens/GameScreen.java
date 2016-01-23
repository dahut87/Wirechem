package fr.evolving.screens;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;

import fr.evolving.UI.ButtonLevel;
import fr.evolving.UI.HorizBarre;
import fr.evolving.UI.Menu;
import fr.evolving.UI.Objectives;
import fr.evolving.UI.TouchMaptiles;
import fr.evolving.UI.WarnDialog;
import fr.evolving.assets.AssetLoader;
import fr.evolving.assets.Preference;
import fr.evolving.automata.Level;
import fr.evolving.automata.Neutraliser_II;
import fr.evolving.automata.Transmuter;
import fr.evolving.automata.Transmuter.Angular;
import fr.evolving.automata.Transmuter.CaseType;
import fr.evolving.renderers.GameRenderer;

public class GameScreen implements Screen {
	private InputMultiplexer multiplexer;
	private Array<InputProcessor> processors;
	private WarnDialog dialog;
	private Stage stage, stage_map, stage_info, stage_tooltip;
	private VerticalGroup table2;
	private GameRenderer Renderer;
	private float runTime;
	public Level level;
	private Window winOptions, winSave;
	private CheckBox SetSound, SetVsynch, SetFullscreen, SetAnimation, Settuto,
			Setdebog, Setrefresh;
	private Slider SetEffectvolume, SetMusicvolume;
	private TextButton Setcancel, Setsave;
	private SelectBox<resolutions> selResolution;
	private SelectBox<quality> selTexturequal;
	private SelectBox<adaptation> selAdaptscreen;
	private List selSaved;
	private ImageButton Setflag, info_up_nrj, info_up_temp, info_up, info_up_rayon,
			info_up_cycle, info_up_nrjval, info_up_tempval, info_up_rayonval,
			info_up_cycleval, nextpage, previouspage;
	private ImageTextButton cycle, temp, nrj, rayon, cout, tech, research,
			info_cout, info_tech, info_research, info_activation;
	private ImageTextButton[] Barre2;
	String[] tocreate = { "run", "stop", "speed", "separator", "move#", "zoomp#",
			"zoomm#", "infos#", "separator", "raz", "save", "levels", "tree",
			"exits", "separator", "screen", "sound", "tuto", "settings",
			"separator", "stat" };
	private ButtonLevel buttonlevel;
	private Objectives objectives;
	private TouchMaptiles map;
	private Menu menu;
	private HorizBarre horizbar;
	private float oldx, oldy;
	private Label fpsLabel, info_nom;
	private TextArea info_desc, tooltip;
	public boolean unroll;

	public enum calling {
		mouseover, mouseclick, mousedrag, longpress, tap, taptap, zoom, fling, pan, pinch
	};

	public enum quality {
		Bas("Bas", TextureFilter.Nearest), Moyen("Moyen", TextureFilter.MipMap), Eleve(
				"Eleve", TextureFilter.Linear);
		private final String text;
		private final TextureFilter aquality;

		private quality(final String text, TextureFilter aquality) {
			this.text = text;
			this.aquality = aquality;
		}

		@Override
		public String toString() {
			return text;
		}

		public TextureFilter getQuality() {
			return this.aquality;
		}
	};

	public enum adaptation {
		fit("Aspect conserve"), fill("Remplissage");
		private final String text;

		private adaptation(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public enum resolutions {
		r1024_768("XGA (1024x768) 4:3", 1024, 768), r1280_720(
				"720p (1280x720) 16:9", 1280, 720), r1280_768(
				"WXGA (1280x768) 5:3", 1280, 768), r1280_1024(
				"SXGA (1280x1024) 5:4", 1280, 1024), r1400_1050(
				"SXGA+ (1400x1050) 4:3", 1400, 1050), r1680_1050(
				"WSXGA (1680x1050) 16:10", 1680, 1050), r1600_1200(
				"UXGA (1600x1200) 4:3", 1600, 1200), r1920_1080(
				"1080p (1920x1080) 16:9", 1920, 1080), r1920_1200(
				"WUXGA (1920x1200) 16:10", 1920, 1200), rmax(
				"resolution Native", 0, 0);
		private final String text;
		private int resx, resy;
		boolean full;

		private resolutions(final String text, int resx, int resy) {
			this.text = text;
			this.resx = resx;
			this.resy = resy;
		}

		@Override
		public String toString() {
			if (full)
				return text + " Fullscreen";
			else
				return text;
		}

		public void SetFull(boolean fullscreen) {
			full = fullscreen;
		}

		public int getResolutionX() {
			return resx;
		}

		public int getResolutionY() {
			return resy;
		}

		public void setResolutionX(int x) {
			resx = x;
		}

		public void setResolutionY(int y) {
			resy = y;
		}
	}

	GestureDetector gesturedetector;

	// This is the constructor, not the class declaration
	public GameScreen(Level alevel) {
		Gdx.app.log("game", "Ok");
		this.level = alevel;
		Gdx.app.debug(getClass().getSimpleName(),
				"Récupération des derniers niveaux.");
		this.level.Grid = AssetLoader.Datahandler.user().getGrid(0,
				this.level.id, "LAST");
		if (this.level.Grid == null) {
			Gdx.app.debug(getClass().getSimpleName(), "Copie monde original.");
			this.level.Grid = this.level.Grid_orig;

		} else {
			Gdx.app.debug(getClass().getSimpleName(),
					"Récupération de la dernière grille.");
			this.level.Grid.tiling_copper();
			this.level.Grid.tiling_transmuter();
		}
		Gdx.app.debug(getClass().getSimpleName(),"Création des Barres verticales & horizontales.");
		horizbar=new HorizBarre(tocreate,"preparebarre");
		horizbar.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.debug("Barre", "Element changé");
				map.tempclear();
				map.fillempty(60);
				menu.unSelect();
				map.setSelected(getselected());
			}
		});
		table2 = new VerticalGroup();
		table2.setPosition(AssetLoader.width, AssetLoader.height - 375);
		table2.right();
		table2.space(10f);
		Gdx.app.debug(
				getClass().getSimpleName(),
				"Création des elements primordiaux du screen (stage, renderer, table, level, world)");
		fpsLabel = new Label("0 FPS", AssetLoader.Skin_level, "FPS");
		fpsLabel.setPosition(AssetLoader.width - 75, AssetLoader.height - 220);
		multiplexer = new InputMultiplexer();
		processors = new Array<InputProcessor>();
		stage = new Stage(AssetLoader.viewport);
		stage_map = new Stage(AssetLoader.viewport);
		stage_info = new Stage(AssetLoader.viewport);
		stage_tooltip = new Stage(AssetLoader.viewport);
		oldx = 0;
		oldy = 0;
		unroll = false;
		Renderer = new GameRenderer(this);
		Gdx.app.debug(getClass().getSimpleName(), "Création des barres");
		tooltip = new TextArea("tooltip:x\r\n tooltip:y",
				AssetLoader.Skin_level, "info_tooltip");
		tooltip.setBounds(541, 27, 100, 50);
		Barre2 = new ImageTextButton[Transmuter.Class.values().length];
		Gdx.app.debug(getClass().getSimpleName(), "Menu:" + Barre2.length
				+ " elements");
		for (int i = 0; i < Barre2.length; i++) {
			Barre2[i] = new ImageTextButton(
					AssetLoader.language.get(Transmuter.Class.values()[i]
							.toString()), AssetLoader.Skin_level);
			Barre2[i].setName(String.valueOf(i));
			Barre2[i].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					int caller = Integer.parseInt(event.getListenerActor()
							.getName());
					Gdx.app.debug("Barre2", "Selection dans la Barre droite:"
							+ caller);
					preparemenu(caller);
				}
			});
		}

		Gdx.app.debug(getClass().getSimpleName(),
				"Création de la barre de gestion du haut");
		cycle = new ImageTextButton(String.valueOf(level.Cycle),
				AssetLoader.Skin_level, "cycle2");
		cycle.setVisible(level.aWorld>=1);
		cycle.setPosition(10, AssetLoader.height - 74);
		temp = new ImageTextButton(String.valueOf(level.Temp),
				AssetLoader.Skin_level, "temp2");
		temp.setVisible(level.aWorld>=2);
		temp.setPosition(210, AssetLoader.height - 74);
		rayon = new ImageTextButton(String.valueOf(level.Rayon),
				AssetLoader.Skin_level, "rayon2");
		rayon.setVisible(level.aWorld>=3);
		rayon.setPosition(410, AssetLoader.height - 74);
		nrj = new ImageTextButton(String.valueOf(level.Nrj),
				AssetLoader.Skin_level, "nrj2");
		nrj.setVisible(level.aWorld>=4);
		nrj.setPosition(610, AssetLoader.height - 74);
		tech = new ImageTextButton(String.valueOf(level.Tech),
				AssetLoader.Skin_level, "tech2");
		tech.setPosition(1345, AssetLoader.height - 74);
		tech.setVisible(level.Tech==1);
		cout = new ImageTextButton(String.valueOf(level.Cout),
				AssetLoader.Skin_level, "cout2");
		cout.setVisible(level.Cout>0);
		cout.setPosition(1445, AssetLoader.height - 74);
		research = new ImageTextButton(String.valueOf(0),
				AssetLoader.Skin_level, "research2");
		research.setPosition(1545, AssetLoader.height - 74);
		research.setVisible(level.Special || level.aWorld>0);
		objectives = new Objectives();
		objectives.setVictory(level.Victory);
		objectives.setPosition(890, AssetLoader.height - 95);
		objectives.setVisible(level.Cout>0);
		buttonlevel = new ButtonLevel(level, true, 1.0f);
		buttonlevel.setPosition(1760, AssetLoader.height - 125);
		buttonlevel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.debug(getClass().getSimpleName(),
						"Remise à zéro du monde");
				GameScreen.this.level.Grid = GameScreen.this.level.Grid_orig;
				level.Grid.tiling_copper();
				level.Grid.tiling_transmuter();
				map.redraw(53);
				buttonlevel.setChecked(false);
			}
		});
		Gdx.app.debug(getClass().getSimpleName(),
				"Création de la barre d'information");
		info_tech = new ImageTextButton("0", AssetLoader.Skin_level,
				"info_tech");
		info_tech.setSize(48, 48);
		info_tech.setPosition(1200, AssetLoader.height - 775);
		info_cout = new ImageTextButton("0", AssetLoader.Skin_level,
				"info_cout");
		info_cout.setSize(48, 48);
		info_cout.setPosition(1280, AssetLoader.height - 775);
		info_research = new ImageTextButton("0", AssetLoader.Skin_level,
				"info_research");
		info_research.setSize(48, 48);
		info_research.setPosition(1360, AssetLoader.height - 775);
		info_activation = new ImageTextButton("0", AssetLoader.Skin_level,
				"info_activation");
		info_activation.setSize(48, 48);
		info_activation.setPosition(1440, AssetLoader.height - 775);
		info_up_cycleval = new ImageButton(AssetLoader.Skin_level,
				"info_cycleval");
		info_up_cycleval.setPosition(1819, AssetLoader.height - 765);
		info_up_tempval = new ImageButton(AssetLoader.Skin_level,
				"info_tempval");
		info_up_tempval.setPosition(1819, AssetLoader.height - 832);
		info_up_rayonval = new ImageButton(AssetLoader.Skin_level,
				"info_rayonval");
		info_up_rayonval.setPosition(1819, AssetLoader.height - 900);
		info_up_nrjval = new ImageButton(AssetLoader.Skin_level, "info_nrjval");
		info_up_nrjval.setPosition(1819, AssetLoader.height - 967);
		info_up_cycle = new ImageButton(AssetLoader.Skin_level, "info_cycle");
		info_up_cycle.setSize(32, 32);
		info_up_cycle.setPosition(1835, AssetLoader.height - 747);
		info_up_temp = new ImageButton(AssetLoader.Skin_level, "info_temp");
		info_up_temp.setSize(32, 32);
		info_up_temp.setPosition(1837, AssetLoader.height - 816);
		info_up_rayon = new ImageButton(AssetLoader.Skin_level, "info_rayon");
		info_up_rayon.setSize(32, 32);
		info_up_rayon.setPosition(1835, AssetLoader.height - 884);
		info_up_nrj = new ImageButton(AssetLoader.Skin_level, "info_nrj");
		info_up_nrj.setSize(32, 32);
		info_up_nrj.setPosition(1835, AssetLoader.height - 950);
		info_nom = new Label("Unknow", AssetLoader.Skin_level, "info_nom");
		info_nom.setPosition(1230, AssetLoader.height - 710);
		info_desc = new TextArea(
				"Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description Description",
				AssetLoader.Skin_level, "info_desc");
		info_desc.setBounds(1220, AssetLoader.height - 965, 575, 150);
		info_up=new ImageButton(AssetLoader.Skin_level,"evolution");
		info_up.setPosition(1375, AssetLoader.height - 720);
		info_up.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (menu.getTransmuter()!=null && menu.getTransmuter().isUpgradable())
					menu.getTransmuter().Upgrade();
			}
		});
		dialog = new WarnDialog(AssetLoader.Skin_ui);
		Gdx.app.debug(getClass().getSimpleName(), "Création d'une tilemap");
		map = new TouchMaptiles(level, 128, 128);
		map.setBounds(0, 0, AssetLoader.width, AssetLoader.height);
		map.redraw(53);
		
		Gdx.app.debug(getClass().getSimpleName(), "Création du menu");
		nextpage=new ImageButton(AssetLoader.Skin_level,"extend");
		nextpage.setPosition(1850, AssetLoader.height - 370);
		nextpage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!nextpage.isDisabled()) {
				menu.NextPage();
				Gdx.app.debug("menu", "Page suivante:"+menu.getPage());
				map.tempclear();
				map.fillempty(53);
				hideInfo();
				nextpage.setDisabled(menu.isNextEmpty());
				previouspage.setDisabled(menu.isPreviousEmpty());
				}
			}
		});
		previouspage=new ImageButton(AssetLoader.Skin_level,"extend2");
		previouspage.setPosition(1800, AssetLoader.height - 370);
		previouspage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!previouspage.isDisabled()) {
				menu.PreviousPage();
				Gdx.app.debug("menu", "Page précédente:"+menu.getPage());
				map.tempclear();
				map.fillempty(53);
				hideInfo();
				nextpage.setDisabled(menu.isNextEmpty());
				previouspage.setDisabled(menu.isPreviousEmpty());
				}
			}
		});
		menu = new Menu(level);
		menu.setBounds(1531f, AssetLoader.height-780, 264, 480);
		menu.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.debug("Menu", "Element changé");
				map.tempclear();
				map.fillempty(60);
				if (menu.getTransmuter() != null) 
					showInfo(menu.getTransmuter());
				else
					hideInfo();
				horizbar.unSelect();
				map.setSelected(getselected());
			}
		});
	}
	
	public String getselected() {
		if (menu.getSelection()==null)
			if (horizbar.getSelection()==null)
					return null;
				else
					return horizbar.getSelection();
		else
			return menu.getSelection();
	}


	public void map_transmuter(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		if (call == calling.taptap && button == 0
				|| (call == calling.mouseclick && button == 1)) {
			Angular angle = menu.getTransmuter().getRotation();
			if (angle == Angular.A00)
				 menu.getTransmuter().setRotation(Angular.A90);
			else if (angle == Angular.A90)
				 menu.getTransmuter().setRotation(Angular.A180);
			else if (angle == Angular.A180)
				 menu.getTransmuter().setRotation(Angular.A270);
			else if (angle == Angular.A270)
				 menu.getTransmuter().setRotation(Angular.A00);
		}
		map.tempclear();
		boolean positionisgood = true;
		int color = 0;
		OrderedMap<Vector2, Integer> tiles =  menu.getTransmuter()
				.getTilesidrotated();
		Entries<Vector2, Integer> iterator = tiles.iterator();
		while (iterator.hasNext()) {
			Entry<Vector2, Integer> all = iterator.next();
			color = 63;
			int index = tiles.keys().toArray().indexOf(all.key, false);
			if ((( menu.getTransmuter().getTilestype(index) == CaseType.Nimporte)
					|| (!level.Grid.getFiber(x + all.key.x, y + all.key.y)
							&& !level.Grid.getCopper(x + all.key.x, y
									+ all.key.y) &&  menu.getTransmuter()
							.getTilestype(index) == CaseType.Rien)
					|| (level.Grid.getFiber(x + all.key.x, y + all.key.y)
							&& level.Grid.getCopper(x + all.key.x, y
									+ all.key.y) &&  menu.getTransmuter()
							.getTilestype(index) == CaseType.Tout)
					|| (level.Grid.getCopper(x + all.key.x, y + all.key.y) &&  menu.getTransmuter()
							.getTilestype(index) == CaseType.Cuivre)
					|| (level.Grid.getFiber(x + all.key.x, y + all.key.y) &&  menu.getTransmuter()
							.getTilestype(index) == CaseType.Fibre)
					|| (level.Grid.getFiber(x + all.key.x, y + all.key.y)
							&& !level.Grid.getCopper(x + all.key.x, y
									+ all.key.y) &&  menu.getTransmuter()
							.getTilestype(index) == CaseType.Fibre_seul) || (level.Grid
					.getCopper(x + all.key.x, y + all.key.y)
					&& !level.Grid.getFiber(x + all.key.x, y + all.key.y) &&  menu.getTransmuter()
					.getTilestype(index) == CaseType.Cuivre_seul))
					&& (level.Grid.getTransmutercalc(x + all.key.x, y
							+ all.key.y) == 0))
				color = 0;
			else
				positionisgood = false;
			map.tempdraw(x + all.key.x, y + all.key.y, all.value,
					 menu.getTransmuter().getRotation().ordinal(), color);
		}
		if ((call == calling.longpress && button == 0) && positionisgood) {
			level.Grid.GetXY(x, y).Transmuter = (Transmuter)  menu.getTransmuter()
					.clone();
			if (alone)
				level.Grid.tiling_transmuter();
			map.redraw(60);
			Gdx.input.vibrate(new long[] { 0, 400, 500, 400 }, -1);
		}
	}

	public void map_infos(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		if (level.Grid.GetXY(x, y) != null) {
			Gdx.app.debug("map", "Etat extension:" + unroll);
			if (level.Grid.GetXY(x, y).Copper)
				Gdx.app.debug("map", "*** Présence de cuivre");
			if (level.Grid.GetXY(x, y).Fiber > 0)
				Gdx.app.debug("map", "*** Présence de fibre");
			if (level.Grid.GetXY(x, y).Transmuter_calc > 0) {
				Gdx.app.debug("map", "transmuter deplacement vers origine:"
						+ level.Grid.GetXY(x, y).Transmuter_movex + ","
						+ level.Grid.GetXY(x, y).Transmuter_movey + " coords:"
						+ (x + level.Grid.GetXY(x, y).Transmuter_movex) + "x"
						+ (y + level.Grid.GetXY(x, y).Transmuter_movey));
				Gdx.app.debug(
						"map",
						level.Grid.getTransmuter(
								x + level.Grid.GetXY(x, y).Transmuter_movex,
								y + level.Grid.GetXY(x, y).Transmuter_movey)
								.getInformations());
				showInfo(level.Grid.getTransmuter(x
						+ level.Grid.GetXY(x, y).Transmuter_movex, y
						+ level.Grid.GetXY(x, y).Transmuter_movey));
				map.tempclear();
				map.tempdraw(x, y, 1069, 0, 0);
			}
		}

	}

	public void map_zoomp(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		map.setZoom(0.9f);
		map.setDec((AssetLoader.width / 2 - realx) / 2,
				(AssetLoader.height / 2 - realy) / 2);
	}

	public void map_zoomm(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		map.setZoom(1.1f);
		map.setDec((AssetLoader.width / 2 - realx) / 2,
				(AssetLoader.height / 2 - realy) / 2);
	}

	public void map_move(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		if (oldx != 0 && oldy != 0) {
			map.setDec(realx - oldx, realy - oldy);
			Gdx.app.debug("map", "Decalage absolue en pixel:" + (realx - oldx)
					+ "x" + (realy - oldy));
		}
		oldx = realx;
		oldy = realy;
	}

	public void map_blank(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		map_fiber_eraser(0, 0, x, y, false, button, call);
		map_copper_eraser(0, 0, x, y, alone, button, call);
	}

	public void map_cleaner(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		for (x = 0; x < level.Grid.sizeX; x++)
			for (y = 0; y < level.Grid.sizeY; y++)
				map_transmuter_eraser(0, 0, x, y, false, button, call);
		level.Grid.tiling_transmuter();
		for (x = 0; x < level.Grid.sizeX; x++)
			for (y = 0; y < level.Grid.sizeY; y++) {
				map_fiber_eraser(0, 0, x, y, false, button, call);
				map_copper_eraser(0, 0, x, y, false, button, call);
			}
		level.Grid.tiling_copper();
		map.redraw(60);
	}

	public void map_all_eraser(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		map_transmuter_eraser(0, 0, x, y, alone, button, call);
		map_fiber_eraser(0, 0, x, y, false, button, call);
		map_copper_eraser(0, 0, x, y, alone, button, call);
	}

	public void map_transmuter_eraser(float realx, float realy, int x, int y,
			boolean alone, int button, calling call) {
		if (level.Grid.GetXY(x, y).Transmuter_calc != 0) {
			level.Grid.GetXY(x + level.Grid.GetXY(x, y).Transmuter_movex, y
					+ level.Grid.GetXY(x, y).Transmuter_movey).Transmuter = null;
			Gdx.app.debug("map", "transmuter deplacement vers origine:"
					+ level.Grid.GetXY(x, y).Transmuter_movex + ","
					+ level.Grid.GetXY(x, y).Transmuter_movey + " coords:"
					+ (x + level.Grid.GetXY(x, y).Transmuter_movex) + "x"
					+ (y + level.Grid.GetXY(x, y).Transmuter_movey));
		}
		if (alone) {
			level.Grid.tiling_transmuter();
			map.redraw(60);
		}
	}

	public void map_fiber_eraser(float realx, float realy, int x, int y,
			boolean alone, int button, calling call) {
		if (level.Grid.GetXY(x, y).Transmuter_calc == 0) {
			level.Grid.GetXY(x, y).Fiber = 0;
			if (alone) {
				level.Grid.tiling_copper();
				map.redraw(60);
			}
		}
	}

	public void map_fiber_pen(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		if (level.Grid.GetXY(x, y).Transmuter_calc == 0)
			level.Grid.GetXY(x, y).Fiber = -1 * level.Grid.GetXY(x, y).Fiber
					+ 1;
		if (alone) {
			level.Grid.tiling_copper();
			map.redraw(60);
		}
	}

	public void map_fiber_brush(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		if (level.Grid.GetXY(x, y).Transmuter_calc == 0)
			level.Grid.GetXY(x, y).Fiber = 1;
		if (alone) {
			level.Grid.tiling_copper();
			map.redraw(60);
		}
	}

	public void map_copper_eraser(float realx, float realy, int x, int y,
			boolean alone, int button, calling call) {
		if (level.Grid.GetXY(x, y).Transmuter_calc == 0) {
			level.Grid.GetXY(x, y).Copper = false;
			if (alone) {
				level.Grid.tiling_copper();
				map.redraw(60);
			}
		}
	}

	public void map_copper_pen(float realx, float realy, int x, int y, boolean alone,
			int button, calling call) {
		if (level.Grid.GetXY(x, y).Transmuter_calc == 0)
			level.Grid.GetXY(x, y).Copper = !level.Grid.GetXY(x, y).Copper;
		if (alone) {
			level.Grid.tiling_copper();
			map.redraw(60);
		}
	}

	public void map_copper_brush(float realx, float realy, int x, int y,
			boolean alone, int button, calling call) {
		if (level.Grid.GetXY(x, y).Transmuter_calc == 0)
			level.Grid.GetXY(x, y).Copper = true;
		if (alone) {
			level.Grid.tiling_copper();
			map.redraw(60);
		}
	}

	@Override
	public void render(float delta) {
		stage.act();
		runTime += delta;
		if (Preference.prefs.getBoolean("Refresh"))
			fpsLabel.setText(Gdx.graphics.getFramesPerSecond() + "FPS");
		Renderer.render(delta, runTime, 0);
		stage_map.draw();
		Renderer.render(delta, runTime, 1);
		stage.draw();
		if (unroll)
			stage_info.draw();
		Renderer.render(delta, runTime, 2);
		stage_tooltip.draw();
	}

	@Override
	public void resize(int width, int height) {
		AssetLoader.viewport.update(width, height);
	}

	@Override
	public void show() {
		Gdx.app.debug(getClass().getSimpleName(),
				"Création de la fenêtre d'option");
		Table Optiontable = Createoption();
		stage.addActor(winOptions);
		Table Savetable = Createsaving();
		stage.addActor(winSave);
		Gdx.app.log("*****", "Affichage du niveau.");
		for (int i = 0; i < Barre2.length; i++)
			table2.addActor(Barre2[i]);
		stage_info.addActor(info_tech);
		stage_info.addActor(info_research);
		stage_info.addActor(info_activation);
		stage_info.addActor(info_up_nrj);
		stage_info.addActor(info_up_temp);
		stage_info.addActor(info_up_rayon);
		stage_info.addActor(info_up_cycle);
		stage_info.addActor(info_up_nrjval);
		stage_info.addActor(info_up_tempval);
		stage_info.addActor(info_up_rayonval);
		stage_info.addActor(info_up_cycleval);
		stage_info.addActor(info_nom);
		stage_info.addActor(info_cout);
		stage_info.addActor(info_up);
		stage_info.addActor(info_desc);
		stage_map.addActor(map);
		//stage_tooltip.addActor(tooltip);
		stage.addActor(horizbar);
		stage.addActor(nextpage);
		stage.addActor(previouspage);		
		stage.addActor(objectives);
		stage.addActor(buttonlevel);
		stage.addActor(rayon);
		stage.addActor(nrj);
		if (Preference.prefs.getBoolean("Refresh"))
			stage.addActor(fpsLabel);
		stage.addActor(temp);
		stage.addActor(cycle);
		stage.addActor(table2);
		stage.addActor(tech);
		stage.addActor(cout);
		stage.addActor(research);
		stage.addActor(menu);
		//gesturedetector=new GestureDetector(null);
		//processors.add(gesturedetector);
		//processors.add(stage_tooltip);
		processors.add(stage_info);
		processors.add(stage);
		processors.add(stage_map);
		multiplexer.setProcessors(processors);
		Gdx.input.setInputProcessor(multiplexer);
		preparemenu(0);
	}

	public void preparebarre(String caller, int count) {
		map.fillempty(53);
		map.tempclear();
		menu.EraseSurtile();
		hideInfo();
		if (caller == "run") {
		} else if (caller == "stop") {
		} else if (caller == "speed") {
		} else if (caller == "move") {
			if (count >= 2)
				map.initzoom();
		} else if (caller == "zoomp") {
			if (count >= 2)
				map.initzoom();
		} else if (caller == "zoomm") {
			if (count >= 2)
				map.initzoom();
		} else if (caller == "infos") {
			if (count >= 2)
				map.initzoom();
		} else if (caller == "raz") {
			winOptions.setVisible(false);
			winSave.setVisible(!winSave.isVisible());
			if (winSave.isVisible())
				readsaved();
		} else if (caller == "save") {
			Gdx.app.log("save", "Ok");
			Gdx.app.debug("Barre", "Sauvegarde de la grille.");
			Gdx.app.debug("Barre", AssetLoader.Datahandler.user().toString());			
			AssetLoader.Datahandler.user().setGrid(0, level.id, level.Grid);
			readsaved();
		} else if (caller == "levels") {
			Gdx.app.debug("Barre", "Affichage des niveaux.");
			AssetLoader.Datahandler.user().setGrid(0, level.id, "LAST",
					this.level.Grid);
			((Game) Gdx.app.getApplicationListener())
					.setScreen(new LevelScreen(level.aWorld));
		} else if (caller == "tree") {
		} else if (caller == "exits") {
			AssetLoader.Datahandler.user().setGrid(0, level.id, "LAST",
					this.level.Grid);
			Gdx.app.exit();
		} else if (caller == "screen") {
			DisplayMode currentMode = Gdx.graphics.getDesktopDisplayMode();
			if (Gdx.graphics.isFullscreen()) {
				Gdx.app.debug("Barre", "vers fenetre.");
				Gdx.graphics.setDisplayMode(currentMode.width,
						currentMode.height, false);
			} else {
				Gdx.app.debug("Barre", "vers plein ecran.");
				Gdx.graphics.setDisplayMode(currentMode.width,
						currentMode.height, true);
			}
		} else if (caller == "sound") {
			if (AssetLoader.intro.getVolume() > 0) {
				Gdx.app.debug("Barre", "arret son.");
				AssetLoader.intro.setVolume(0f);
			} else {
				Gdx.app.debug("Barre", "marche son.");
				AssetLoader.intro.setVolume(1f);
			}
		} else if (caller == "tuto") {
			if (AssetLoader.Tooltipmanager.enabled) {
				Gdx.app.debug("Barre", "arret tuto.");
				AssetLoader.Tooltipmanager.enabled = false;
			} else {
				Gdx.app.debug("Barre", "marche tuto.");
				AssetLoader.Tooltipmanager.enabled = true;
			}
		} else if (caller == "settings") {
			winOptions.setVisible(!winOptions.isVisible());
			winSave.setVisible(false);
			if (winOptions.isVisible())
				readpref();
		} else if (caller == "flag") {
			if (AssetLoader.language.getLocale().getDisplayName()
					.contains("français")) {
				Gdx.app.debug("Barre", "Langue USA");
				AssetLoader.language = AssetLoader.usa;
			} else {
				Gdx.app.debug("Barre", "Langue FR");
				AssetLoader.language = AssetLoader.french;
			}
		} else if (caller == "stat") {
		}
	}

	public void preparemenu(int menuitem) {
		checkMenu(menuitem, true);
		map.tempclear();
		map.fillempty(53);
		horizbar.unSelect();
		menu.setPageType(0,menuitem);		
		nextpage.setDisabled(menu.isNextEmpty());
		previouspage.setDisabled(true);
		hideInfo();
	}

	public void showInfo(Transmuter transmuter) {
		if (transmuter == null)
			return;
		unroll = true;
		info_nom.setText(transmuter.getName());
		info_desc.setText(transmuter.getDesc());
		info_tech.setVisible(transmuter.getTechnology() > 0);
		info_tech.setText(String.valueOf(transmuter.getTechnology()));
		info_cout.setVisible(transmuter.getPrice() > 0);
		info_cout.setText(String.valueOf(transmuter.getPrice()));
		info_research.setVisible(transmuter.getResearch() > 0);
		info_research.setText(String.valueOf(transmuter.getResearch()));
		info_activation.setVisible(transmuter.isActivable());
		info_activation.setText(String.valueOf(transmuter
				.getMaxActivationLevel()));
		info_up_cycle.setVisible(transmuter.isUpgradableCycle());
		info_up_nrj.setVisible(transmuter.isUpgradableNrj());
		info_up_temp.setVisible(transmuter.isUpgradableTemp());
		info_up_rayon.setVisible(transmuter.isUpgradableRayon());
		info_up_cycleval.setVisible(transmuter.isUpgradableCycle());
		info_up_nrjval.setVisible(transmuter.isUpgradableNrj());
		info_up_tempval.setVisible(transmuter.isUpgradableTemp());
		info_up_rayonval.setVisible(transmuter.isUpgradableRayon());
		info_up_cycleval.getStyle().up = new TextureRegionDrawable(
				AssetLoader.Atlas_level.findRegion("jauge"
						+ transmuter.getUpgradeCycle()));
		info_up_cycleval.setColor(AssetLoader.Levelcolors[0]);
		info_up_nrjval.getStyle().up = new TextureRegionDrawable(
				AssetLoader.Atlas_level.findRegion("jauge"
						+ transmuter.getUpgradeNrj()));
		info_up_nrjval.setColor(AssetLoader.Levelcolors[4]);
		info_up_tempval.getStyle().up = new TextureRegionDrawable(
				AssetLoader.Atlas_level.findRegion("jauge"
						+ transmuter.getUpgradeTemp()));
		info_up_tempval.setColor(AssetLoader.Levelcolors[1]);
		info_up_rayonval.getStyle().up = new TextureRegionDrawable(
				AssetLoader.Atlas_level.findRegion("jauge"
						+ transmuter.getUpgradeRayon()));
		info_up_rayonval.setColor(AssetLoader.Levelcolors[2]);
	}

	public void hideInfo() {
		unroll = false;
	}

	public void checkMenu(int menu, boolean check) {
		for (int i = 0; i < Barre2.length; i++)
			Barre2[i].setChecked(false);
		Barre2[menu].setChecked(true);
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	public Table Createsaving() {
		winSave = new Window("Saved grids", AssetLoader.Skin_ui);
		winSave.add(savingPanel()).row();
		winSave.setColor(1, 1, 1, 0.8f);
		winSave.setVisible(false);
		winSave.pack();
		winSave.setBounds(50, 100, 250, 450);
		return winSave;
	}

	private Table savingPanel() {
		Table table = new Table();
		table.pad(10, 10, 0, 10);
		selSaved = new List(AssetLoader.Skin_ui);
		selSaved.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (this.getTapCount() > 1)
					level.Grid = AssetLoader.Datahandler.user().getGrid(0,
							level.id, selSaved.getSelectedIndex());
				level.Grid.tiling_copper();
				level.Grid.tiling_transmuter();
				map.redraw(53);
			}
		});
		ScrollPane scroll = new ScrollPane(selSaved);
		table.add(scroll).width(250).height(440).row();
		return table;
	}

	public void readsaved() {
		Array<String> items = AssetLoader.Datahandler.user().getGrids(0,
				level.id);
		if (items != null)
			selSaved.setItems(items);
	}

	public Table Createoption() {
		winOptions = new Window("Options", AssetLoader.Skin_ui);
		winOptions.add(SettingsVideo()).row();
		winOptions.add(SettingsAudio()).row();
		winOptions.add(SettingsOther()).row();
		winOptions.add(SettingsButtons()).pad(10, 0, 10, 0);
		winOptions.setColor(1, 1, 1, 0.8f);
		winOptions.setVisible(false);
		winOptions.pack();
		winOptions.setPosition(100, 250);
		return winOptions;
	}

	public void readpref() {
		SetFullscreen.setChecked(Preference.prefs.getBoolean("Fullscreen"));
		SetSound.setChecked(Preference.prefs.getBoolean("Sound"));
		Settuto.setChecked(Preference.prefs.getBoolean("Tutorial"));
		SetVsynch.setChecked(Preference.prefs.getBoolean("VSync"));
		Setrefresh.setChecked(Preference.prefs.getBoolean("Refresh"));
		SetAnimation.setChecked(Preference.prefs.getBoolean("Animation"));
		Setflag.setChecked(Preference.prefs.getBoolean("Language"));
		SetEffectvolume.setValue(Preference.prefs.getFloat("Effect"));
		SetMusicvolume.setValue(Preference.prefs.getFloat("Music"));
		selResolution.setSelectedIndex(Preference.prefs
				.getInteger("Resolution"));
		selAdaptscreen.setSelectedIndex(Preference.prefs
				.getInteger("Adaptation"));
		selTexturequal.setSelectedIndex(Preference.prefs.getInteger("Quality"));
		Setdebog.setChecked(Preference.prefs.getInteger("log") == Gdx.app.LOG_DEBUG);
	}

	public void writepref() {
		Preference.prefs.putInteger("ResolutionX", selResolution.getSelected()
				.getResolutionX());
		Preference.prefs.putInteger("ResolutionY", selResolution.getSelected()
				.getResolutionY());
		Preference.prefs.putInteger("Resolution",
				selResolution.getSelectedIndex());
		Preference.prefs.putBoolean("Fullscreen", SetFullscreen.isChecked());
		Preference.prefs.putBoolean("Sound", SetSound.isChecked());
		Preference.prefs.putBoolean("Tutorial", Settuto.isChecked());
		Preference.prefs.putBoolean("VSync", SetVsynch.isChecked());
		Preference.prefs.putBoolean("Refresh", Setrefresh.isChecked());
		Preference.prefs.putBoolean("Animation", SetAnimation.isChecked());
		Preference.prefs.putBoolean("Language", Setflag.isChecked());
		Preference.prefs.putFloat("Effect", SetEffectvolume.getValue());
		Preference.prefs.putFloat("Music", SetMusicvolume.getValue());
		Preference.prefs.putInteger("Adaptation",
				selAdaptscreen.getSelectedIndex());
		Preference.prefs.putInteger("Quality",
				selTexturequal.getSelectedIndex());
		if (Setdebog.isChecked())
			Preference.prefs.putInteger("log", Gdx.app.LOG_DEBUG);
		else
			Preference.prefs.putInteger("log", Gdx.app.LOG_INFO);
		Preference.prefs.flush();
	}

	private Table SettingsOther() {
		Table table = new Table();
		table.pad(10, 10, 0, 10);
		table.add(
				new Label("Divers", AssetLoader.Skin_level, "Fluoxetine-25",
						Color.ORANGE)).colspan(3);
		table.row();
		table.columnDefaults(0).padRight(10);
		table.columnDefaults(1).padRight(10);
		Settuto = new CheckBox("Activation du tutoriel", AssetLoader.Skin_ui);
		table.add(Settuto).left();
		table.row();
		Setdebog = new CheckBox("Mode debugage", AssetLoader.Skin_ui);
		table.add(Setdebog).left();
		table.row();
		Setrefresh = new CheckBox("Afficher le rafraichissement",
				AssetLoader.Skin_ui);
		table.add(Setrefresh).left();
		table.row();
		table.add(
				new Label("Choix de la langue", AssetLoader.Skin_ui,
						"default-font", Color.WHITE)).left();
		Setflag = new ImageButton(AssetLoader.Skin_level, "Setflag");
		table.add(Setflag);
		table.row();
		return table;
	}

	private Table SettingsVideo() {
		Table table = new Table();
		table.pad(10, 10, 0, 10);
		table.add(
				new Label("Video", AssetLoader.Skin_level, "Fluoxetine-25",
						Color.ORANGE)).colspan(3);
		table.row();
		table.columnDefaults(0).padRight(10);
		table.columnDefaults(1).padRight(10);

		SetVsynch = new CheckBox("Synchronisation verticale",
				AssetLoader.Skin_ui);
		table.add(SetVsynch).left();
		Table tablev1 = new Table();
		tablev1.add(
				new Label("Resolution:", AssetLoader.Skin_ui, "default-font",
						Color.WHITE)).left().row();
		selResolution = new SelectBox<resolutions>(AssetLoader.Skin_ui);
		selResolution.setItems(resolutions.values());
		tablev1.add(selResolution).left().row();
		table.add(tablev1).left();
		table.row();

		SetFullscreen = new CheckBox("Plein ecran", AssetLoader.Skin_ui);
		table.add(SetFullscreen).left();
		Table tablev2 = new Table();
		tablev2.add(
				new Label("Remplissage de l'ecran:", AssetLoader.Skin_ui,
						"default-font", Color.WHITE)).left().row();
		selAdaptscreen = new SelectBox<adaptation>(AssetLoader.Skin_ui);
		selAdaptscreen.setItems(adaptation.values());
		tablev2.add(selAdaptscreen).left().row();
		table.add(tablev2).left();
		table.row();

		Table tablev3 = new Table();
		tablev3.add(
				new Label("Qualite des textures:", AssetLoader.Skin_ui,
						"default-font", Color.WHITE)).left().row();
		SetAnimation = new CheckBox("Activer les animations",
				AssetLoader.Skin_ui);
		table.add(SetAnimation).left();
		selTexturequal = new SelectBox<quality>(AssetLoader.Skin_ui);
		selTexturequal.setItems(quality.values());
		tablev3.add(selTexturequal).left().row();
		table.add(tablev3).left();
		table.row();
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			Graphics.DisplayMode[] modes = Gdx.graphics.getDisplayModes();
			for (resolutions res : resolutions.values()) {
				res.SetFull(false);
				for (DisplayMode mode : modes) {
					if (res.resx == mode.width && res.resy == mode.height)
						res.SetFull(true);
				}
			}
			Vector2 maxres = Preference.getmaxresolution();
			resolutions.rmax.SetFull(true);
			resolutions.rmax.setResolutionX((int) maxres.x);
			resolutions.rmax.setResolutionY((int) maxres.y);
		} else
			selResolution.setDisabled(true);
		return table;
	}

	private Table SettingsAudio() {
		Table table = new Table();
		table.pad(10, 10, 0, 10);
		table.add(
				new Label("Audio", AssetLoader.Skin_level, "Fluoxetine-25",
						Color.ORANGE)).colspan(3);
		table.row();
		table.columnDefaults(0).padRight(10);
		table.columnDefaults(1).padRight(10);
		SetSound = new CheckBox("Activation du son", AssetLoader.Skin_ui);
		table.add(SetSound).left();
		table.row();
		table.add(new Label("Volume des effets", AssetLoader.Skin_ui));
		SetEffectvolume = new Slider(0.0f, 1.0f, 0.1f, false,
				AssetLoader.Skin_ui);
		table.add(SetEffectvolume).left();
		table.row();
		table.add(new Label("Volume de la musiques", AssetLoader.Skin_ui));
		SetMusicvolume = new Slider(0.0f, 1.0f, 0.1f, false,
				AssetLoader.Skin_ui);
		table.add(SetMusicvolume).left();
		table.row();
		return table;
	}

	private void onSaveClicked() {
		winOptions.setVisible(false);
		writepref();
		dialog.Show(
				"Veuillez redémmarrer pour que les préférences soient appliquées.",
				stage);
	}

	private void onCancelClicked() {
		winOptions.setVisible(false);
	}

	private Table SettingsButtons() {
		Table table = new Table();
		table.pad(10, 10, 0, 10);
		Setsave = new TextButton("Save", AssetLoader.Skin_ui);
		table.add(Setsave).padRight(30);
		Setsave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onSaveClicked();
			}
		});
		Setcancel = new TextButton("Cancel", AssetLoader.Skin_ui);
		table.add(Setcancel);
		Setcancel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCancelClicked();
			}
		});
		return table;
	}
}
