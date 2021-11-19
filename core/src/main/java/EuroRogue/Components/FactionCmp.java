package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FactionCmp implements Component {

     public Faction faction;
     public List<Faction> allied = new ArrayList<>();
     public List<Faction> neutral = new ArrayList<>();;
     public List<Faction> enemy = new ArrayList<>();;
     public FactionCmp(){}
     public FactionCmp(Faction faction, Collection<Faction> allied, Collection<Faction> neutral, Collection<Faction> enemy)
     {
         this.faction = faction;
         if(allied!=null)
            this.allied = (List<Faction>) allied;
         if(neutral!=null)
            this.neutral = (List<Faction>) neutral;
         if(enemy!=null)
            this.enemy  = (List<Faction>) enemy;
     }

    public FactionCmp(Faction faction)
    {
        this.faction = faction;
        switch (faction)
        {

            case PLAYER:
                this.enemy  = Arrays.asList(Faction.RAT, Faction.SNAKE, Faction.MONSTER);
                break;
            case MONSTER:
                this.enemy  = Arrays.asList(Faction.PLAYER);
                this.neutral = Arrays.asList(Faction.RAT, Faction.SNAKE);
                break;
            case RAT:
                this.enemy  = Arrays.asList(Faction.PLAYER, Faction.SNAKE);
                this.neutral = Arrays.asList(Faction.MONSTER);
                break;
            case SNAKE:
                this.enemy  = Arrays.asList(Faction.PLAYER,Faction.RAT);
                this.neutral = Arrays.asList(Faction.MONSTER);
                break;
        }


    }
     public enum Faction {PLAYER, MONSTER, RAT, SNAKE}
}
