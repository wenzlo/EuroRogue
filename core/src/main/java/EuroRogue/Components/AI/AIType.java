package EuroRogue.Components.AI;

public enum AIType
{
    DEFAULT_AI(AICmp.class),
    SNAKE_AI(AISnakeCmp.class),
    RAT_AI(AIRatCmp.class);

    public Class cls;

    AIType(Class cls)
    {
        this.cls = cls;
    }


}
