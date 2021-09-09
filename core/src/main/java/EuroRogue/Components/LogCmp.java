package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

import EuroRogue.IColoredString;
import squidpony.squidgrid.gui.gdx.SColor;


public class LogCmp implements Component
{
    public ArrayList<IColoredString.Impl<SColor>> logEntries = new ArrayList<>();

}
