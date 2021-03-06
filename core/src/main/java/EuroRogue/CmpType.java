package EuroRogue;

import EuroRogue.AbilityCmpSubSystems.Dodge;
import EuroRogue.AbilityCmpSubSystems.MagicMissile;
import EuroRogue.AbilityCmpSubSystems.MeleeAttack;
import EuroRogue.Components.AimingCmp;
import EuroRogue.Components.ArmorCmp;
import EuroRogue.Components.CharCmp;
import EuroRogue.Components.CodexCmp;
import EuroRogue.Components.DetectedCmp;
import EuroRogue.Components.EquipmentCmp;
import EuroRogue.Components.FOVCmp;
import EuroRogue.Components.FactionCmp;
import EuroRogue.Components.FocusCmp;
import EuroRogue.Components.FocusTargetCmp;
import EuroRogue.Components.GlyphsCmp;
import EuroRogue.Components.InventoryCmp;
import EuroRogue.Components.ItemCmp;
import EuroRogue.Components.LevelCmp;
import EuroRogue.Components.LightCmp;
import EuroRogue.Components.LightCmpTemp;
import EuroRogue.Components.LightingCmp;
import EuroRogue.Components.LogCmp;
import EuroRogue.Components.ManaCmp;
import EuroRogue.Components.ManaPoolCmp;
import EuroRogue.Components.MenuCmp;
import EuroRogue.Components.NameCmp;
import EuroRogue.Components.NoiseMapCmp;
import EuroRogue.Components.ObjectCmp;
import EuroRogue.Components.ParticleEffectsCmp;
import EuroRogue.Components.PositionCmp;
import EuroRogue.Components.ScrollCmp;
import EuroRogue.Components.ShrineCmp;
import EuroRogue.Components.StatsCmp;
import EuroRogue.Components.TickerCmp;
import EuroRogue.Components.UiBgLightingCmp;
import EuroRogue.Components.WeaponCmp;
import EuroRogue.Components.WindowCmp;
import EuroRogue.EventComponents.ActionEvt;
import EuroRogue.EventComponents.AnimateGlyphEvt;
import EuroRogue.EventComponents.CampEvt;
import EuroRogue.EventComponents.CodexEvt;
import EuroRogue.EventComponents.DamageEvent;
import EuroRogue.EventComponents.DayNightCycleEvt;
import EuroRogue.EventComponents.DeathEvt;
import EuroRogue.EventComponents.FrozenEvt;
import EuroRogue.EventComponents.GameStateEvt;
import EuroRogue.EventComponents.ItemEvt;
import EuroRogue.EventComponents.LevelEvt;
import EuroRogue.EventComponents.LogEvt;
import EuroRogue.EventComponents.MakeCampEvt;
import EuroRogue.EventComponents.MoveEvt;
import EuroRogue.EventComponents.RestEvt;
import EuroRogue.EventComponents.ShrineEvt;
import EuroRogue.EventComponents.StatEvt;
import EuroRogue.EventComponents.StatusEffectEvt;
import EuroRogue.EventComponents.StorageEvt;

public enum CmpType
{
    ACTION_EVT(ActionEvt.class),
    AIMING(AimingCmp.class),
    ANIM_GLYPH_EVT(AnimateGlyphEvt.class),
    ARMOR(ArmorCmp.class),
    CAMP_EVT(CampEvt.class),
    CHAR(CharCmp.class),
    CODEX(CodexCmp.class),
    CODEX_EVT(CodexEvt.class),
    DAYNIGHTCYCLE_EVT(DayNightCycleEvt.class),
    DAMAGE_EVT(DamageEvent.class),
    DEATH_EVT(DeathEvt.class),
    DETECTED(DetectedCmp.class),
    DODGE(Dodge.class),
    EQUIPMENT(EquipmentCmp.class),
    INVENTORY(InventoryCmp.class),
    FACTION(FactionCmp.class),
    FOCUS(FocusCmp.class),
    FOCUS_TARGET(FocusTargetCmp.class),
    FOV(FOVCmp.class),
    FROZEN_EVT(FrozenEvt.class),
    GAMESTATE_EVT(GameStateEvt.class),
    GLYPH(GlyphsCmp.class),
    ITEM(ItemCmp.class),
    ITEM_EVT(ItemEvt.class),
    LEVEL(LevelCmp.class),
    LEVEL_EVT(LevelEvt.class),
    LIGHT(LightCmp.class),
    LIGHT_TEMP(LightCmpTemp.class),
    LIGHTING(LightingCmp.class),
    LOG(LogCmp.class),
    LOG_EVT(LogEvt.class),
    MAGIC_MISSILE(MagicMissile.class),
    MAKE_CAMP_EVT(MakeCampEvt.class),
    MANA(ManaCmp.class),
    MANA_POOL(ManaPoolCmp.class),
    MELEE(MeleeAttack.class),
    MENU(MenuCmp.class),
    MOVE_EVT(MoveEvt.class),
    NAME(NameCmp.class),
    NOISE_MAP(NoiseMapCmp.class),
    OBJECT(ObjectCmp.class),
    POSITION(PositionCmp.class),
    PARTICLES(ParticleEffectsCmp.class),
    REST_EVT(RestEvt.class),
    SCROLL(ScrollCmp.class),
    SHRINE(ShrineCmp.class),
    SHRINE_EVT(ShrineEvt.class),
    STATS(StatsCmp.class),
    STAT_EVT(StatEvt.class),
    STATUS_EFFECT_EVT(StatusEffectEvt.class),
    STORAGE_EVT(StorageEvt.class),
    TICKER(TickerCmp.class),
    UIBG_LIGHTING(UiBgLightingCmp.class),
    WEAPON(WeaponCmp.class),
    WINDOW(WindowCmp.class);

    public Class type;

    CmpType(Class type)
    {
        this.type=type;
    }
}
