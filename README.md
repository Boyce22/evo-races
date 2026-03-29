# EvoRaces - Dynamic Race Evolution System for Minecraft

EvoRaces is a revolutionary Minecraft mod that transforms gameplay by introducing a deep, dynamic race and evolution system. Unlike traditional race mods that offer static bonuses, EvoRaces creates an immersive RPG-like experience where your race evolves based on how you play, creating unique progression paths and meaningful choices.

## 🎯 Core Philosophy

**"Your Playstyle Defines Your Evolution"** - EvoRaces moves beyond static race bonuses to create a living, breathing system where your actions, environment, and choices shape your character's development. Every decision matters, and every playthrough can lead to different evolutionary paths.

## 🧬 Core Systems

### 1. **Custom Attribute System**
Instead of modifying vanilla attributes, EvoRaces introduces a completely custom attribute framework:

- **Vitality** - Maximum health and regeneration rate
- **Strength** - Melee damage and carry capacity  
- **Agility** - Movement speed, dodge chance, and attack speed
- **Intellect** - Magic power, spell effectiveness, and special abilities
- **Resilience** - Defense against damage and status effects

*Built on Fabric's extensible API, allowing seamless integration and future expansion.*

### 2. **Dynamic Evolution System (The Heart of Replayability)**
This is where EvoRaces becomes truly addictive. Your race evolves based on multiple factors:

#### **Evolution Triggers:**
- **Experience Progression** - Traditional leveling with race-specific milestones
- **Action-Based Evolution** - Killing specific mobs, using certain weapons, or performing unique actions
- **Environmental Influence** - Evolution based on biomes (Nether vs Overworld vs End)
- **Lunar Cycles** - Full moon buffs and transformations (classic RPG style)
- **Playstyle Alignment** - Your combat style shapes your evolution path

#### **Example Evolution Paths:**
- **Elf** → **High Elf** (enhanced magic, nature affinity)
- **Elf** → **Dark Elf** (stealth, poison mastery, shadow magic)
- **Orc** → **Warlord** (brutal strength, intimidation aura)
- **Orc** → **Shaman** (spiritual connection, elemental totems)
- **Human** → **Knight** (defensive mastery, leadership)
- **Human** → **Rogue** (versatility, adaptability)

### 3. **Meaningful Disadvantages & Trade-offs**
Most race mods fail here by offering only bonuses. EvoRaces ensures every advantage comes with a real cost:

#### **Race-Specific Drawbacks:**
- **Orc:**
  - ✅ +50% Melee Damage
  - ❌ -30% Movement Speed  
  - ❌ 2x Hunger Consumption
  - ❌ Poor magic affinity

- **Vampire:**
  - ✅ Night Vision & Life Steal
  - ✅ Rapid Health Regeneration
  - ❌ Sunlight Damage (scales with exposure)
  - ❌ Requires blood consumption
  - ❌ Fire vulnerability

- **Dwarf:**
  - ✅ 3x Mining Speed
  - ✅ Underground navigation bonus
  - ❌ -40% Movement Speed on surface
  - ❌ Poor swimming ability
  - ❌ Sunlight sensitivity (gradual debuff)

- **Undead:**
  - ✅ No need to breathe
  - ✅ Zombies ignore you
  - ❌ Healing potions cause damage
  - ❌ Villagers flee from you
  - ❌ Holy water/enchantments deal extra damage

### 4. **Active Ability System**
Each race gains unique active abilities with cooldowns:

#### **Race Abilities:**
- **Elf** - *Nature's Embrace* (area heal + regeneration)
- **Orc** - *Berserker Rage* (temporary damage boost, defense reduction)
- **Vampire** - *Shadow Step* (short-range teleport + invisibility)
- **Dwarf** - *Stone Skin* (damage reduction + knockback immunity)
- **Human** - *Adaptive Tactics* (temporary stat boost based on situation)

#### **Implementation:**
- Custom keybinds via Fabric API
- Cooldown management through NBT data
- Visual effects and sound feedback
- Progressive ability unlocking through evolution

### 5. **World Interaction & Immersion**
EvoRaces makes the world feel alive by changing how it reacts to you:

#### **NPC Reactions:**
- Villagers offer different trades based on race
- Iron Golems may attack hostile races
- Certain mobs become allies or enemies
- Priest villagers offer race-specific blessings/curses

#### **Faction Interactions:**
- **Undead** - Zombies, skeletons, and phantoms are neutral
- **Nether-born** - Piglins offer better trades, hoglins are passive
- **Aquatic** - Guardians are friendly, dolphins provide bonuses
- **Fey** - Bees and forest creatures assist you

### 6. **Environmental Weaknesses System**
Immersion through environmental interaction:

#### **Race-Specific Vulnerabilities:**
- **Vampire** - Sunlight causes burning (intensity based on time of day)
- **Aquatic Races** - Dehydration on land (needs periodic water)
- **Fire Elementals** - Water causes damage and weakness
- **Mountain Dwellers** - Suffer in deep oceans/swamps
- **Dark-aligned** - Torchlight causes discomfort

### 7. **Playstyle-Driven Evolution (The Most Innovative Feature)**
Your race evolves based on HOW you play, not just what you kill:

#### **Evolution Paths Based on Playstyle:**
- **Archer Focus** → Evolves into marksman/sniper specialization
- **Magic Focus** → Becomes archmage/ritualist
- **Melee Focus** → Transforms into brute/berserker
- **Exploration Focus** → Develops into ranger/nomad
- **Building Focus** → Evolves into architect/engineer

#### **Hybrid Evolution:**
Mix playstyles to create unique hybrid races:
- **Spellsword** (Magic + Melee)
- **Shadow Archer** (Stealth + Archery)
- **Battlemage** (Defense + Magic)
- **Scout** (Speed + Exploration)

## 🛠️ Technical Implementation

### **Architecture:**
- Built on **Fabric API** for maximum compatibility
- **Modular design** - Easy to add new races/evolutions
- **Data-driven configuration** - JSON-based race definitions
- **Event-driven system** - Hooks into Minecraft's event bus

### **Key Technical Features:**
1. **Custom Capability System** - Race data persistence
2. **Network Synchronization** - Multiplayer compatibility
3. **Config GUI** - In-game race selection and customization
4. **Achievement System** - Race-specific challenges and rewards
5. **Compatibility Layer** - Works with other popular mods

## 🎮 Gameplay Impact

### **Early Game:**
- Race selection defines your starting strengths/weaknesses
- Basic abilities unlock initial playstyle options
- Environmental awareness becomes crucial

### **Mid Game:**
- Evolution choices create branching paths
- Race-specific quests and challenges appear
- World interaction changes based on evolution

### **Late Game:**
- Mastery of race abilities
- Unique end-game content for each race
- Race-specific boss fights and dungeons
- Prestige evolution paths

## 🔮 Future Roadmap

### **Phase 1 (Current):**
- Core race system (Human, Elf, Orc, Dwarf)
- Basic evolution mechanics
- Attribute system foundation

### **Phase 2:**
- Advanced races (Vampire, Werewolf, Demon)
- Skill trees for each race
- Race-specific structures and dungeons

### **Phase 3:**
- Cross-breeding mechanics (hybrid races)
- Race wars and faction system
- Custom dimension with race-specific content
- NPC companions of different races

### **Phase 4:**
- Magic system integration
- Economy system (race-based trading)
- Kingdom building with racial bonuses
- Epic questlines for each race

## 🤝 Community & Modding

EvoRaces is designed with modders in mind:

- **Public API** for adding custom races
- **Template system** for quick race creation
- **Event hooks** for custom evolution triggers
- **Resource pack support** for custom models/textures
- **Documentation** for third-party integration

## 📊 Balance Philosophy

1. **No Free Buffs** - Every advantage has a cost
2. **Meaningful Choices** - Evolution paths change gameplay significantly
3. **Progressive Power** - Power grows with player skill
4. **Counterplay** - Every race has weaknesses opponents can exploit
5. **Situational Excellence** - Races excel in specific scenarios

## 🎯 Design Goals

1. **Depth Over Breadth** - Fewer, more meaningful races
2. **Player Agency** - Your choices matter
3. **Emergent Gameplay** - Unplanned interactions create stories
4. **Replay Value** - Different races = different games
5. **Immersion** - The world reacts to who you are

---

**EvoRaces isn't just another race mod - it's a complete gameplay overhaul that makes every playthrough unique, every choice meaningful, and every evolution a story worth telling.**

*"Become more than your origins. Evolve beyond your limits."*