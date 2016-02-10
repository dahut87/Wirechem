package fr.evolving.UI;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fr.evolving.automata.Worlds;

public class IconValue extends ImageTextButton{
	
	public enum Icon{tech,cout,research,cycle,temp,rayon,nrj};
	Icon icon;
	boolean showmaximum;
	Worlds worlds;
	
	public IconValue(Icon icon,Worlds worlds, Skin skin) {
		super("", skin,icon.toString()+"2");
		this.icon=icon;
		this.worlds=worlds;
	}
	
	public void SetShowMaximum(boolean value)
	{
		showmaximum=value;
	}
	
	@Override
	public void act(float delta)
	{
		switch(this.icon) {
		case tech:
			this.setText(String.valueOf(worlds.getInformations().Tech));
			this.setVisible(worlds.getInformations().Tech>=1 || worlds.isDebug());
			break;
		case cout:
			worlds.getInformations().Cout=worlds.getInformations().Cout_orig-worlds.getInformations().Cout_copperfiber-worlds.getInformations().Cout_transmuter;
			this.setText(String.valueOf(worlds.getInformations().Cout));
			if (worlds.getInformations().Cout>0.25*worlds.getInformations().Cout_orig)
				this.setColor(1f, 1f, 1f, 1f);
			else if  (worlds.getInformations().Cout>0) 
				this.setColor(1f, 0.5f, 0.5f, 1f);
			else 
				this.setColor(1f, 0, 0, 1f);
			this.setVisible(worlds.getInformations().Cout_orig>0 || worlds.isDebug());
			break;
		case research:
			this.setText(String.valueOf(worlds.ReadResearch()));
			this.setVisible(worlds.ReadResearch()>0 || worlds.isDebug());
			break;
		case cycle:
			this.setVisible(worlds.getWorld()>=1 || worlds.isDebug());
			if (showmaximum)
				this.setText(String.valueOf(worlds.getInformations().Cycle)+"/"+String.valueOf(worlds.getInformations().Maxcycle));	
			else
				this.setText(String.valueOf(worlds.getInformations().Cycle));
			break;
		case temp:
			this.setVisible(worlds.getWorld()>=2 || worlds.isDebug());
			if (showmaximum)
				this.setText(String.valueOf(worlds.getInformations().Temp)+"/"+String.valueOf(worlds.getInformations().Maxtemp));	
			else
				this.setText(String.valueOf(worlds.getInformations().Temp));
			break;
		case rayon:
			this.setVisible(worlds.getWorld()>=3 || worlds.isDebug());
			if (showmaximum)
				this.setText(String.valueOf(worlds.getInformations().Rayon)+"/"+String.valueOf(worlds.getInformations().Maxrayon));	
			else
				this.setText(String.valueOf(worlds.getInformations().Rayon));
			break;
		case nrj:
			this.setVisible(worlds.getWorld()>=4 || worlds.isDebug());
			if (showmaximum)
				this.setText(String.valueOf(worlds.getInformations().Nrj)+"/"+String.valueOf(worlds.getInformations().Maxnrj));	
			else
				this.setText(String.valueOf(worlds.getInformations().Nrj));
		break;
		}
		
	}
}
