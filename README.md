# EuroRogue
ASCII Roguelike with Euro Strategy Board Game Influences
____________________________________________________________________
--Run the RunEuroRogue.bat file in the bin folder.

  Requires Java - https://www.java.com/download/ie_manual.jsp

EuroRogue is a WIP traditional ASCII Roguelike with some Euro board game mechanics mixed in.
The current iteration focuses mainly on the combat systems and plays much like a very basic single dungeon roguelike. 
I am still in "Systems Before Content" mode so there are only a handful of abilities, weapons and armor available.

Other than rats, there are no defined enemy types yet, just randomly rolled characters similar to the player, but with less stat points.
Random enemies total stat points increase with depth. You must sacrifice Mana/Energy to buy stat increases to keep up.
You gain Mana/Energy by killing enemies.
__________________________________________________________________________________________________________
Legend-
	
	@ = your Character
	B = enemy
	r = rat
	> = Stairs to next level of the dungeon
	+ = Closed Door
	/ = Open Door
	. = Stone floor tiles
	" = Moss tiles
	, = Shallow water tiles
	~ = Deep Water tiles
	: = Bridge tiles
	§ = Shrines - Convert mana, Get New Skills, Enchant Weapons
	Box Drawing Chars = walls
	! = Noise Alert icon. You heard a noise from this position.
	
	anything else is an item that can be picked up, equipped etc... Torch/Weapon/Armor/Mana/ƒood
__________________________________________________________________________________________________________
Controls-

	Movement
	
		8 Way Numpad Movement
		4 Way Arrow Keys Movement 
		8 Way VI Key Movement

		Numpad_5 / SPACE BAR  = Short Rest - Replenishes Acttive Mana

    Actions

	    UI HotKeys are variable = ?)
	
		Abilities/Scrolls  - ?) Perform Ability/Use Scroll if Available

		Inventory  - ?) Equip/Unequip Item
			   - Shift + ?) Drop Item
					
        g)   -> Grab or Pickup Item
        c)   -> Make Camp(Long Rest, Heal) / Resume Playing toggle
        >)   -> Descend Stairs i.e. Shift + .)
	
	TAB) -> Cycle Enemy Target Selection.

	Aimed Abilities - Aim Mode

	    8 Way Numpad to adjust aim.
	    Numpad_5 = cast spell.
	
	-Dev Cheats!!!-
	[)   -> Generate a new random Dungeon
	v)   -> Switch game Focus to nearest Enemy. You now control that enemy instead.
__________________________________________________________________________________________________________
Start Menu-

	A default Character name is already entered.
	Use BackSpace to clear it and enter a different one.
	The character name is the game seed and determines the Character build, enemies and dungeon level generation.
	UI HotKey to start game. 
	
	"1) New Game"      -> pres num_row 1 to start the game.
	"2) New Character" -> pres num_row 2 to re-roll your Character Stats and Starting Items.

__________________________________________________________________________________________________________	
Abilities-
		
	-You will start the game with randomized stats and Prepared Abilites.
	-Only Prepared Abilites will be available as actions/reactions. Abilites can Prepared/Unprepared
		in the Camp Menu which is accessed by performing the Make Camp action - c).
	-Each Ability has a Prepare Cost which is payed out of the Spent Mana Pool. You must
		have enough open slots in the Attuned Mana/Energy Pool to accomodate the Prepare Cost.
	-In order to perform an Ability, you must be able to pay the Casting Cost from your 
		Active Mana/Energy Pool.
	
	-Abilities are gained by picking up Scrolls(%). If you pick up an ability scroll that you have not learned, 
	 you will learn it. Otherwise it will be available as a one time use Action/Reaction that will consume the scroll.

__________________________________________________________________________________________________________
Combat/Movement-
	
	Actions-
		On your turn you can select an available action using the designated UI HotKey or Movement key. 
		The action will be delayed a certain number of "Ticks" based on your Character Stats.
		After the Action is performed, you immediately take another turn.
		
	Reactions-
		Reactions are performed automaticaly and instantly in response to an action. For instance, if
		the Dodge ability is available, it will fire in response to an incoming melee attack avoiding
		some or all of the damage. Reaction abilities can only be used once per short rest.
	
	Actions and Reactions will be avialable if you have the Mana/Energy to pay for them in your active mana row
	and a viable target for the action/reaction is within range.

	Actions/Scrolls and Reactions will be dithered if they are not available.

__________________________________________________________________________________________________________
Stats-
	
	Core Stats -
		
		HP- 
			Your remaining health / Max Health

		Spirit -
		    Equal to your total mana.'
		    Improves all stat modifiers by 1% per point.
		    Do everything better/faster!
		
		Strength-
			Increases base HP by 4/point
			Increases Carrying Capacity
			Increases Attack Power
			Increases Weapon Damage bonus on strength based weapons
			Decreases Stat penalites on strength based armor - Plate/Mail
			Affects power/range/duration of stregnth based abilites
			Increases base HP by 4/point
			
		Dexterity-
			Increases base HP by 4/point
			Decreases movement delay - (ttMove)
			Decreases Physical Attack delay - (ttMelee)
			Decreases Equip/Unequip Item delay
			Decreases Drop/Pickup Item delay
			Increases Weapon Damage bonus on dexterity based weapons
			Decreases Stat penalites on dexterity based armor - Leather/Mail
			Affects power/range/duration of Dexterity based abilites
			Increases base HP by 4/point
			
		Constitution-
			Increases base HP by 12/point
			Decreases Short Rest delay - (ttRest)
			Affects power/range/duration of Constitution based abilites
			
		Perception-
			Increases base HP by 4/point
			Decreases Spell Casting delay - (ttCast)
			Increases Night Vision range
			Increases Noise Detection range
			Decreases minimum detectable light level
			Affects power/range/duration of Perception based abilites
			
		Intelligence-
			Increases base HP by 4/point
			Increases Attuned Mana capacity (Prepare more, or more powerful abilites)
			Increases Spell Power
			Affects power/range/duration of Intelligence based abilites
			
	Speed Stats-
	
		ttMove  - movement delay (this is also used for Equip/Unequip/Drop/Pickup delay)
		ttMelee - melee attack delay 
		ttCast  - spell casting delay
		ttRest  - short rest delay
		
	Resists-
		
		% for each Damage Type
		Positive % reduces incoming damage of that type
		Negative % increases incoming damage of that type
		
	Damage Stats-
		
		Weapon Damge - Melee Attack damage using currently equiped weapons
		Attack Power - Base physical damage based on strength
		Spell Power  - Max Spell Damage based on Intelligence
		Melee DPT    - Weapon Damage / ttMelee
		Spell DPT    - Spell Power / ttCast
		
__________________________________________________________________________________________________________
Mana/Energy Pool- 
	
	Contains Mana/Energy Tokens of various colors representing the different "Schools"
	of abilities Fire/Ice/Arcane/Physical etc. These are "Spent" to perform an ability.
	Mana/Energy tokens are dropped by enemies.
	
	■ ■ ■ ■ ■ ■  <- Active Mana/Energy Pool - Available to spend to perform Abilites
 
	■ ■ ■ ■ ■ ■	    Spent Mana/Energy Pool - Mana spent on abilities transfers here.
	■ ■ ■ ■ ■ ■  <- Mana/Energy randomly drawn from this pool to replenish the Active Row.
	■ ■		          Active Row is replenished by performing a Short Rest
 
	■ ■ ■ ■ ████ <- Attuned Mana/Energy Pool -
  	████████████    Mana slots available to "Attune" mana in order to Prepare an Ability.
			             Number of open slots is determined by Intelligence.
					
			A Short Rest will automatically be performed if your Active Mana is depleted or
			you can not pay the cost of performing any Ability or Scroll.
   
__________________________________________________________________________________________________________
Make Camp (Long Rest)- 
	
	-Making Camp is noisy and will alert nearby monsters.
	-If a monster is detected while making camp, the "Make Camp' action will be cancelled.
	-Making Camp applies the "Hungry" or "Starving" Status Effect to the Character.
	-Replenishes Health if the Character is not "Starving". This is only way to recover HP.
	
	-Allows Access to the Camp Menu Actions
	
		-Prepare/Unprepare Spells and Abilites
			Unprepared Spells will be dithered. Select UI HotKey to Prepare/Unprepare an ability.
			
		-Stat Increases
			Pay mana/energy tokens to increase Base Stats. These tokens are lost permanently.
			Stat increases you can not afford will be dithered.
			
		-Eat Food
			Only appears if you have Food Rations in your inventory.
			Removes Hungry (-25% MaxHp) or Starving (-50% MaxHp) Status Effect.
			If the Character is not Hungry or Starving, applies Well Fed status effect (+25% health).
			
__________________________________________________________________________________________________________
Status Effects -

	-Equipment Status Effects-
		Equiping Weapons or Armor will apply an associated Status Effect to the Character. These can effect Speed Stats,
		Damage Stats and Resists for basic equipment and apply any of the other effects for Magic Items.
	
	-Weapon Status Effects-
		Weapon specific effects. i.e. equiping a Staff increases melee range from 1 to 2
		Effects can be applied to the target as well i.e., Daggers apply the Bleeding effect. 
	
	-Stalking  - From Stalk ability. Enter stealth mode. 
		   - Decreases movement speed.
		   - Decreases noise level.
		   - Increases light level necessary to reveal you
		   - Map overlay highliting in red, all cells where enemies can detect you. 
	
	-Enlightened 
		   - Increases Spell Damage, ttMove, ttRest, ttMelee. 
			   
	-Enraged   - Increases Attack Power, ttRest, ttCast. 
			  
	-Hungry    - Decreases Max HP by 25%. If Character is already Hungry, Starving effect is applied.
	-Starving  - Decreases Max HP by 50%. Prevents hp regen when camping.
	-Well fed  - Increases Max HP by 25%.
	
	-Chilled   - Decreases Bludgeoning, Peircing, Slashing resistance.
			   - Increases Fire resistance, ttMove, ttMelee, ttRest
			   - If character is already chilled, applies Frozen status effect.
			  
			   
	-Frozen    - Increases Fire resistance
			   - Character is frozen for durration of effect or untill they take damage.
			   
	-Calescent - Decreases Fire and Aracane Resistance.
			   - Increaeses Ice resistance, ttCast and ttRest.
			   - If character is already Calescent, applies Burning status effect.
	
	-Burning   - Decreases ttMove
			   - Character will panick and run around in random directions for the durration of effect.
			   
	-Stagerred - Increases ttMove, ttMelee, ttRest, ttCast
	
	-Bleeding  - Any action taken except resting will cause damage for the duration of the effect.
	           - Stacks, increasing damage done when the target moves or performs an action.
	
	-Water Walking
	           - Character can traverse deep water tiles.
				   
_____________________________________________________________________________________________________				
	
