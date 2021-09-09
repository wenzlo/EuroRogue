package EuroRogue.EventComponents;

import com.badlogic.ashley.core.Component;

public interface IEventComponent extends Component
{
    boolean isProcessed();
    void setProcessed(boolean bool);
}

