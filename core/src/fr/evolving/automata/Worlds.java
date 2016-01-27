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
	
	public enum State {pause,simulating,notloaded,databasefailed};
	
	public Worlds(String campaign) {
		name=campaign;
		init();
	}
	
	public void init() {
		levels=null;
		usedworld=-1;
		usedlevel=null;
		if (!AssetLoader.Datahandler.verifyall()) {
			Gdx.app.debug(getClass().getSimpleName(),"Pilotes de bases de donnée défaillant.");
			state=State.databasefailed;
		}
		else
			state=State.notloaded;
		this.load(name);
		if (state==State.notloaded)
			this.init(name);
		onchanged();
	}	
	
	public void SaveGrid() {
		if (usedlevel!=null)
			AssetLoader.Datahandler.user().setGrid(0, usedlevel.id, usedlevel.Grid);
	}
	
	public void SaveLastGrid() {
		if (usedlevel!=null)
			AssetLoader.Datahandler.user().setGrid(0, usedlevel.id, "LAST",	usedlevel.Grid);
	}
	
	public void Forcereload() {
		onchanged();
	}
	
	public void onchanged() {
		ChangeEvent event=new ChangeEvent();
		event.setTarget(this);
		event.setListenerActor(this);	
		event.setStage(this.getStage());
		if (event.getStage()!=null) 
			this.fire(event);
	}
	
	public Array<Level> getLevels() {
		Array<Level> tempworld=new Array<Level>();
		if (state!=State.notloaded && this.levels!=null)
		{
			for(Level level:levels)
				if (level!=null && level.aWorld==usedworld)
					tempworld.add(level);
			return tempworld;
		}
		else
			return null;
	}
	
	public State getState() {
		return state;
	}
	
	public void setLevel(int alevel) {
		if (state!=State.notloaded)
		if (usedworld>=0) {
			Array<Level> tempworld=getLevels();
			for(Level level:tempworld)
				if (level.aLevel==alevel)
				{
					usedlevel=level;
					return;
				}
		}
	}
	
	public Level getInformations() {
		return usedlevel;
	}
	
	public int getLevel() {
		return usedlevel.aLevel;
	}
	
	public void delLevel() {
		usedlevel=null;
	}
	
	public void setWorld(int world) {
		if (state!=State.notloaded)
		if (world<getMaxWorlds()) {
			usedworld=world;
			onchanged();
		}
	}
	
	public void NextWorld() {
		if (state!=State.notloaded)
		if (usedworld<getMaxWorlds()-1) {
			usedworld++;
			onchanged();
		}
	}
	
	public void PreviousWorld() {
		if (state!=State.notloaded)
		if (usedworld>0) {
			usedworld--;
			onchanged();
		}
	}
	
	public int getWorld() {
		if (state!=State.notloaded)
			return usedworld;
		else
			return -1;
	}
	
	public void set(String campaign) {
		Gdx.app.log("*****", "Définition de la compagne "+campaign);
		Preference.prefs.putString("world", campaign);
		Preference.prefs.flush();
		load(campaign);
	}
	
	public void load(String campaign) {
		Gdx.app.log("*****", "Chargement de la compagne "+campaign);
		levels=AssetLoader.Datahandler.game().getCampaign(campaign);
		name=campaign;
		if (levels==null)

		{
			state=State.notloaded;
		}
		else
			state=State.pause;
	}
	
	public void init(String campaign) {
		Gdx.app.log("*****", "initialisation de la compagne "+campaign);
		try {
			levels=InitWorlds.go();
			Preference.prefs.putString("world",campaign);
			Preference.prefs.flush();
			name=campaign;
			AssetLoader.Datahandler.game().setCampaign(levels,name);
			state=State.pause;
		}
		catch (Exception e) {
			state=State.notloaded;
		}
	}
	
	public void save(String campaign) {
		Gdx.app.log("*****", "enregistrement de la compagne "+campaign);
		AssetLoader.Datahandler.game().setCampaign(levels,campaign);
	}
	
	public int getMaxWorlds() {
		int max = 0;
		for (Level level : levels)
			if (level != null && level.aWorld > max)
				max = level.aWorld;
		return max;
	}
	
	public String getName() {
		return this.name;
	}

}