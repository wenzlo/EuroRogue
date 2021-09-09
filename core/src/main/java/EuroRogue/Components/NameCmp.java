package EuroRogue.Components;

import com.badlogic.ashley.core.Component;

public class NameCmp implements Component {
    public String name;
    public NameCmp(){}
    public NameCmp (String name)
    {
        this.name=name;
    }
}
