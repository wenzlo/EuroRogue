package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

public class ObjectCmp implements Component
{
     ObjectType objectType;
     boolean traversable;


     public ObjectCmp(ObjectType type)
     {
          this.objectType = type;
          this.traversable = type.traversable;
     }
}
