package EuroRogue.Components;

public enum EquipmentSlot
{
   /* HEAD("H"),
    NECK("N"),
    LEFT_HAND("LH"),*/
    LEFT_HAND_WEAP("LW"),
    //RIGHT_HAND("RH"),
    RIGHT_HAND_WEAP("RW"),
    CHEST("CH");
    //LEGS("L"),
    //FEET("F");

    public String abr;

    EquipmentSlot(String abr)
    {
        this.abr = abr;
    }


}
