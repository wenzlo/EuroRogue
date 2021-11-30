package EuroRogue;

import EuroRogue.Components.AI.AIType;

public enum MobType {

    DEFAULT(AIType.DEFAULT_AI),
    PLAYER(AIType.DEFAULT_AI),
    SNAKE(AIType.SNAKE_AI),
    RAT(AIType.RAT_AI);

    public AIType aiType;

    MobType(AIType aiType)
    {
        this.aiType = aiType;
    }

}
