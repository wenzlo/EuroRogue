package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

public class FactionCmp implements Component {

     public Faction faction;
     public FactionCmp(){}
     public FactionCmp(Faction faction)
     {
         this.faction=faction;
     }
     public enum Faction {PLAYER, MONSTER, RAT}
}
