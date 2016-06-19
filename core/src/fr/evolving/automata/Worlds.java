package fr.evolving.automata;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;

import fr.evolving.assets.AssetLoader;
import fr.evolving.assets.InitWorlds;
import fr.evolving.assets.Preference;

public class Worlds extends Actor {
	private ChangeEvent event;
	private Array<Level> levels;
	private String name;
	private int usedworld;
	private Level usedlevel;
	private Array<Transmuter> Transmuters;
	private State state;
	private boolean Debug;
	private int research;
	private Level lastchange;
	
	public enum State {pause,simulating,notloaded,databasefailed};
	
	public Worlds(String campaign) {
		name=campaign;
		initialize();
	}
	
	public void initialize() {
		Debug=false;
		levels=null;
		usedworld=-1;
		research=-2;
		usedlevel=null;
		if (!AssetLoader.Datahandler.verifyall()) {
			Gdx.app.debug("wirechem-Worlds","Pilotes de bases de donnée défaillant.");
			state=State.databasefailed;
		}
		else
			state=State.notloaded;
		this.load(name);
		this.ReadTransmuters();
		if (state==State.notloaded)
			create(name);
		onchanged(null);
	}
	
	public void ModResearch(int addsub) {
		research+=addsub;
		SaveResearch();
	}
	
	public int ReadResearch() {
		if (research==-2)
			research=AssetLoader.Datahandler.user().getResearchpoint(0);
		return research;
	}
	
	public void SaveResearch() {
		AssetLoader.Datahandler.user().setResearchpoint(0,research);
	}
	
	public void ActivateDebug() {
		this.Debug=true;
	}
	
	public void DesactivateDebug() {
		this.Debug=false;
	}
	
	public boolean isDebug() {
		return this.Debug;
	}
	
	public void SaveTransmuters() {
		AssetLoader.Datahandler.user().setTransmuters(0,Transmuters);
	}
	
	public void ReadTransmuters() {
		Transmuters=AssetLoader.Datahandler.user().getTransmuters(0);
		if (Transmuters==null)
			state=State.notloaded;
		else
			state=State.pause;
	}
	
	public Array<Transmuter> getTransmuters() {
		return Transmuters;
	}
	
	public Array<String> ViewGrids() {
		if (usedlevel!=null)
			return AssetLoader.Datahandler.user().getGrids(0,usedlevel.id);
		else
			return null;
	}
	
	public void ReadGrid(int number) {
		if (usedlevel!=null)
			usedlevel.Grid = AssetLoader.Datahandler.user().getGrid(0,	usedlevel.id, number);
	}
	
	public void SaveGrid() {
		if (usedlevel!=null)
			AssetLoader.Datahandler.user().setGrid(0, usedlevel.id, usedlevel.Grid);
	}
	
	public void ReadLastGrid() {
		if (usedlevel!=null)
			usedlevel.Grid = AssetLoader.Datahandler.user().getGrid(0,	usedlevel.id, "LAST");
	}
	
	public void SaveLastGrid() {
		if (usedlevel!=null)
			AssetLoader.Datahandler.user().setGrid(0, usedlevel.id, "LAST",	usedlevel.Grid);
	}
	
	public void Forcereload() {
		onchanged(null);
	}
	
	public void onchanged(Level change) {
		ChangeEvent event=new ChangeEvent();
		event.setTarget(this);
		event.setListenerActor(this);	
		event.setStage(this.getStage());
		lastchange=change;
		if (event.getStage()!=null) 
			this.fire(event);
	}
	
	public Array<Level> getLevels(int world) {
		Array<Level> tempworld=new Array<Level>();
		if (state!=State.notloaded && this.levels!=null)
		{
			for(Level level:levels)
				if (level!=null && level.aWorld==world)
				{
					if (level.aLevel==0)
						level.Locked=false;
					tempworld.add(level);
				}
			return tempworld;
		}
		else
			return null;
	}
	
	public Array<Level> getLevels() {
			return getLevels(this.usedworld);
	}
	
	public Array<Level> getAllLevels() {
		return levels;
	}
	
	public void updateUnlockLevels() {
		if (levels!=null)
		for(Level level:levels)
			if (level!=null)
			level.Locked=AssetLoader.Datahandler.user().getLevellock(0, level.id);
	}
	
	public State getState() {
		return state;
	}
	
	public void prepareLevel(boolean force) {
		Gdx.app.debug("wirechem-Worlds","Récupération des conditions initiales.");
		usedlevel.Cout=usedlevel.Cout_orig;
		usedlevel.Cycle=usedlevel.Cycle_orig;
		usedlevel.Temp=usedlevel.Temp_orig;
		usedlevel.Rayon=usedlevel.Rayon_orig;
		usedlevel.Nrj=usedlevel.Nrj_orig;
		usedlevel.Victory=usedlevel.Victory_orig.clone();	
		Gdx.app.debug("wirechem-Worlds","Récupération des derniers niveaux.");
		ReadLastGrid();
		if (usedlevel.Grid == null || force) {
			Gdx.app.debug("wirechem-Worlds", "Copie monde original.");
			usedlevel.Grid = (Grid)usedlevel.Grid_orig.clone();

		} else {
			Gdx.app.debug("wirechem-Worlds","Récupération de la dernière grille.");
			ReadLastGrid();
		}
		usedlevel.Grid.tiling_copper();
		usedlevel.Grid.tiling_transmuter();
	}
	
	public void origLevel() {
		usedlevel.Grid_orig = (Grid)usedlevel.Grid.clone();
	}
	
	public Level findLevel(int levelid) {
		if (state!=State.notloaded) 
			if (usedworld>=0) {
				Array<Level> tempworld=getLevels();
				for(Level level:tempworld)
					if (level.aLevel==levelid)
						return level;
			}
		return null;
	}
	
	public void setLevel(int levelid) {
		Level level=findLevel(levelid);
		if (level!=null)
			usedlevel=level;
		return;
	}
	
	public Level getLevelData() {
		return usedlevel;
	}
	
	public int getLevel() {
		if (usedlevel!=null)
			return usedlevel.aLevel;
		else
			return 0;
	}
	
	public void delLevel() {
		delLevel(this.usedlevel.aLevel);
	}
	
	public void delLevel(int levelid) {
		Level level=findLevel(levelid);
		if (level!=null) {
			levels.removeValue(level, false);
			if (this.usedlevel!=null && this.usedlevel.aLevel==levelid)
				this.usedlevel=null;
			onchanged(level);
			this.showlevels();
		}	
	}
	
	public Level getChange() {
		return lastchange;
	}
	
	public void addLevel(Level level) {
		levels.add(level);
		this.showlevels();
		onchanged(level);
	}
	
	public void showlevels() {
		Gdx.app.debug("wirechem-worlds","Affichage des niveaux:");
		for(Level level: levels)
			Gdx.app.debug("wirechem-GameScreen","Monde:"+level.aWorld+" Niveau:"+level.aLevel+" Nom:"+level.Name+" Debloque:"+level.Locked+" Special:"+level.Special+" id:"+level.id);
	}
	
	public void setWorld(int world) {
		if (state!=State.notloaded)
		if (world<getMaxWorlds()) {
			usedlevel=null;
			usedworld=world;
			onchanged(null);
		}
	}
	
	public void setMaxWorldLevel() {
		usedworld=getMaxUnlockWorlds();
		usedlevel=getMaxUnlockLevel();
		onchanged(null);
	}
	
	public boolean isFirstWorld() {
		return (usedworld==0);
	}
	
	public boolean isLastWorld() {
		return (usedworld==getMaxUnlockWorlds());
	}
	
	public boolean isRealLastWorld() {
		return (usedworld==getMaxWorlds());
	}
	
	
	public void NextWorld() {
		if (state!=State.notloaded)
		if (usedworld<getMaxWorlds()) {
			usedlevel=null;
			usedworld++;
			onchanged(null);
		}
	}
	
	public void PreviousWorld() {
		if (state!=State.notloaded)
		if (usedworld>0) {
			usedlevel=null;
			usedworld--;
			onchanged(null);
		}
	}
	
	public int getWorld() {
		if (state!=State.notloaded)
			return usedworld;
		else
			return -1;
	}
	
	public void unLockLevel() {
		AssetLoader.Datahandler.user().setLevelunlock(0, usedlevel.id);
	}
	
	public void unLockLevel(int levelid) {
		AssetLoader.Datahandler.user().setLevelunlock(0, levelid);
	}
	
	public void set(String campaign) {
		Gdx.app.log("wirechem-Worlds", "***** Définition de la compagne "+campaign);
		Preference.prefs.putString("world", campaign);
		Preference.prefs.flush();
		load(campaign);
	}
	
	public void load(String campaign) {
		Gdx.app.log("wirechem-Worlds", "***** Chargement de la compagne "+campaign);
		levels=AssetLoader.Datahandler.game().getCampaign(campaign);
		updateUnlockLevels();
		name=campaign;
		if (levels==null)
			state=State.notloaded;
		else
			state=State.pause;
	}
	
	public void create(String campaign) {
		Gdx.app.log("wirechem-Worlds", "***** initialisation de la compagne "+campaign);
		try {
			levels=InitWorlds.go();
			Preference.prefs.putString("world",campaign);
			Preference.prefs.flush();
			name=campaign;
			AssetLoader.Datahandler.game().setCampaign(levels,name);
			state=State.pause;
			research=0;
			Transmuters=AssetLoader.allTransmuter;
			SaveTransmuters();
			SaveResearch();
		}
		catch (Exception e) {
			state=State.notloaded;
		}
	}
	
	public void save(String campaign) {
		Gdx.app.log("wirechem-Worlds", "***** enregistrement de la compagne "+campaign);
		AssetLoader.Datahandler.game().setCampaign(levels,campaign);
	}
	
	public int getMaxWorlds() {
		int max = 0;
		for (Level level : levels)
			if (level != null && level.aWorld > max)
				max = level.aWorld;
		return max;
	}
	
	public int getFreeLevel() {
		return getFreeLevel(usedworld);
	}
	
	public int getFreeLevel(int world) {
		int max = getMaxLevel(world);
		if (max==0)
			return 0;
		int free = 0;
		for (;free<=max;free++)
			if (findLevel(free)==null)
				return free;
		return free;
	}
	
	public int getMaxLevel() {
		return getMaxLevel(usedworld);
	}
		
	public int getMaxLevel(int world) {
		int max = 0;
		for (Level level : levels)
			if (level != null && level.aWorld == world && level.aLevel>max)
				max = level.aLevel;
		return max;
	}
	
	public int getMaxUnlockWorlds() {
		int maxworld=0;
		if (levels!=null)
		for (Level level : levels)
			if (!level.Locked && level.aWorld>maxworld)
				maxworld=level.aWorld;
		return maxworld;
	}
	
	public Level getMaxUnlockLevel() {
		Array<Level> tempworld=getLevels();
		int maxlevel=0;
		Level themaxlevel=null;
		if (tempworld!=null)
		for (Level level : tempworld)
			if (!level.Locked && level.aLevel>maxlevel) {
				maxlevel=level.aLevel;
				themaxlevel=level;
			}
		return themaxlevel;
	}
	
	public String getName() {
		return this.name;
	}

}
