# The Runes

The language. **Every Latin word here is translated** — you never need to know Latin to
read this document, and neither does a player.

- **How the symbols are drawn:** [`GLYPH_SPEC.md`](GLYPH_SPEC.md)
- **How recipes use them:** [`RITUALS.md`](RITUALS.md)
- **Why things are the way they are:** [`DECISIONS.md`](DECISIONS.md)

---

## 1. Three words, used precisely

| Term | Means |
|---|---|
| **Glyph** | **one symbol**, one tile. `IGNIS` (*fire*) is a glyph. Glyphs are **discovered** in the world. |
| **Rune word** | an **ordered sequence of two or more glyphs** naming one thing. `IGNIS · GERMEN` (*fire-seed*) is a rune word. Rune words are **guessed** in the codex. |
| **Inscription** | a full ritual, written as rune words in formula order (§3.2). |
| **The Runes** | the whole system — glyphs, words and grammar together. |

**Order is meaningful.** `IGNIS · GERMEN` and `GERMEN · IGNIS` are different words. A
reversed guess simply fails.

---

## 2. The lexicon: 26 axes, 52 runes

**Every rune has an opposite.** The lexicon is not a list of words but a list of **axes** —
each one a dimension of the world, each with two opposed poles.

Three things follow, and they are the whole reason for the design:

1. **The axis is itself a concept.** `PLENVM`/`VACVVM` is *fullness*. `NOVVM`/`SENEX` is
   *age*. Learn one pole and you have half-learned the other, which makes the language far
   cheaper to teach than 52 words would suggest.
2. **Description replaces naming.** There is no rune for "stairs". A thing is *located* by
   naming its poles — which is how 52 runes describe far more than 52 things.
3. **Coining is disciplined.** A new rune is admissible only if it **completes an axis**.
   That is what stops the lexicon sprawling.

A pole marked **✦** may **head** a rune word (it names a *kind of thing*). The rest are
qualifiers only — see the head rule (§3.1).

### The World

| Rune | English | | Rune | English |
|---|---|---|---|---|
| `CAELVM` ✦ | heavens, sky, the above | ↔ | `INFERNVS` ✦ | hells, the fire below |
| `ORIGO` ✦ | source, home, the middle | ↔ | `FINIS` ✦ | end, edge, the beyond |
| `LVX` | light | ↔ | `TENEBRAE` | darkness |
| `ORDO` | order, law, pattern | ↔ | `CHAOS` | ruin, the unmade |
| `SOL` ✦ | sun, the day | ↔ | `LVNA` ✦ | moon, the night |

### The Elements

| Rune | English | | Rune | English |
|---|---|---|---|---|
| `IGNIS` ✦ | fire, heat | ↔ | `GELV` ✦ | ice, cold |
| `VNDA` ✦ | flow, liquid | ↔ | `SAXVM` ✦ | stone, solid |
| `VENTVS` ✦ | air, wind, breath | ↔ | `TERRA` ✦ | earth, ground |
| `PLENVM` | full, dense | ↔ | `VACVVM` ✦ | void, empty, hollow |

### The Living

| Rune | English | | Rune | English |
|---|---|---|---|---|
| `VITA` | living | ↔ | `MORS` | dead |
| `GERMEN` ✦ | seed, sprout, growth | ↔ | `TABES` ✦ | rot, decay, blight |
| `CARO` ✦ | flesh | ↔ | `OSSA` ✦ | bone |
| `HERBA` ✦ | green, leaf | ↔ | `LIGNVM` ✦ | wood, timber |
| `HOMO` ✦ | folk, the upright kind | ↔ | `BESTIA` ✦ | beast |

### Matter

| Rune | English | | Rune | English |
|---|---|---|---|---|
| `FERRVM` ✦ | iron, the working metal | ↔ | `AVRVM` ✦ | gold, the precious metal |
| `AES` ✦ | copper, the metal that ages | ↔ | `ADAMAS` ✦ | diamond, which cannot be marred |
| `GEMMA` ✦ | gem, crystal | ↔ | `PVLVIS` ✦ | dust, powder |
| `CANDIDVM` | refined, pure, shining | ↔ | `SORDES` | raw, dross, ore |

### Making

| Rune | English | | Rune | English |
|---|---|---|---|---|
| `OPVS` | wrought by hand | ↔ | `NATVM` | natural, found so |
| `NOVVM` | new, fresh | ↔ | `SENEX` | old, aged, weathered |
| `TOTVM` | whole, uncut | ↔ | `FRACTVM` | cut, broken, worked |
| `TEGMEN` ✦ | a covering — wax, hide, shell | ↔ | `NVDVM` | bare, exposed |

### Form

| Rune | English | | Rune | English |
|---|---|---|---|---|
| `PORTA` ✦ | gate, a way through | ↔ | `VALLVM` ✦ | wall, rampart, hold |
| `GRADVS` ✦ | step, stair | ↔ | `AEQVVM` ✦ | flat, level |

### Will

| Rune | English | | Rune | English |
|---|---|---|---|---|
| `HOSTIS` | hostile, foe | ↔ | `MITIS` | tame, gentle, friend |
| `VNICVM` | one, single | ↔ | `GREX` ✦ | throng, swarm, flock |

### 2.1 Words the language does *not* need

Two axes were cut because another axis already did their work:

| Cut | Say instead |
|---|---|
| `VAS` / `MOLES` — vessel / solid mass | a vessel **is** a made void: `OPVS · VACVVM` |
| `ACIES` / `SCVTVM` — blade / guard | a blade **is** worked metal: `FRACTVM · FERRVM`; armour **is** covering metal: `TEGMEN · FERRVM` |

That is the axis system working as intended: **if a concept can be described, it does not
need naming.**

### 2.2 Five runes are second choices

Five poles are not the obvious Latin, because the obvious word **collided under the mark
rule** with a rune already in the lexicon (`GLYPH_SPEC.md` §3.0). Words must sit at least
12 px apart; these did not:

| Wanted | English | Collided with | At | Ships as |
|---|---|---|---|---|
| `TVRBA` | crowd, throng | `TERRA` (*earth*) | **0 px — identical** | `GREX` |
| `PVRVM` | pure, unmixed | `PORTA` (*gate*) | **0 px — identical** | `CANDIDVM` |
| `SANVM` | whole, sound | `SENEX` (*old*) | **0 px — identical** | `TOTVM` |
| `VETVS` | old, hoary | `VITA` (*living*) | 6 px | `SENEX` |
| `MVRVM` | wall | `MORS` (*dead*) | 6 px | `VALLVM` |

Each replacement is at least as good a word as what it replaced — `GREX` is a *flock*
rather than a *mob*, `CANDIDVM` is *shining-pure* rather than merely *unmixed*.

`MITIS`, `AEQVVM` and `VNICVM` were also second choices under the **old** mark rule.
Taking the mark from the first and third letters instead of the first and second freed
`AMICVS`, `PLANVM` and `VNVM` (18 px, 14 px and 16 px clear today). They were kept anyway,
on meaning: *tame* beats *friendly* for a temper pole, and *level* beats *flat*.

**A homograph is a language bug, and the fix is a synonym — never the art.**

---

## 3. Grammar

### 3.1 Inside a word: the head rule

A rune word is `QUALIFIER · HEAD`, and **the head is the last ✦ glyph in the sequence.**
Everything before it qualifies.

```
INFERNVS  ·  HOMO          hell · folk   →   "Hell-Folk"  (a Piglin)
    ↑          ↑
qualifier     head
```

This is head-final compounding — the same rule English uses in *black-bird* and Chinese in
火山 *fire-mountain* (volcano). Defining the head by **position** rather than by a
per-glyph rule is what lets it handle every order with no exceptions.

### 3.2 Across a ritual: the formula

An inscription is five clauses, always in this order:

```
VESSEL   /   OFFERING   /   HOUR   /   SUBJECT   /   ISSUE
 where         given        when       acted on      produced
```

**The formula is the biggest decipherment aid in the game.** A player who has decoded
nothing still knows the third word must be a *condition* and the last a *result*, purely
from position. Roman inscriptions worked the same way: a reader seeing `V.S.L.M.` knew the
shape of the sentence before reading a word of it.

Recipe schema and worked examples: [`RITUALS.md`](RITUALS.md).

---

## 4. The lexicon at work

Everything below is built from the 52. **Nothing was minted for any of it** — which is the
only real test of an axis language.

### 4.1 Creatures

| Thing | Rune word | Reads |
|---|---|---|
| Enderman | `FINIS · HOMO` | End-Folk |
| Piglin | `INFERNVS · HOMO` | Hell-Folk |
| Illager | `HOSTIS · HOMO` | Foe-Folk |
| Villager | `MITIS · HOMO` | Friend-Folk |
| Zombie | `MORS · HOMO` | Dead-Folk |
| Skeleton | `OSSA · HOMO` | Bone-Folk |
| Ender Dragon | `FINIS · BESTIA` | End-Beast |
| Bee | `VNICVM · GREX` | One-of-the-Throng |

**Opposites come free** — Illager and Villager are the *same word* with the pole swapped.
**Heads travel:** `HOMO` (*folk*) heads six of these, so decoding one gives you most of
the rest.

**Silence is meaningful.** The Enderman carries **no** temper pole — neither `HOSTIS`
(*foe*) nor `MITIS` (*friend*) — because it is neither until you look at it. An unmarked
axis is a statement.

### 4.2 Materials

| Thing | Rune word | Reads |
|---|---|---|
| Iron Ore | `SAXVM · FERRVM` | Stone-Iron |
| Raw Iron | `SORDES · FERRVM` | Dross-Iron |
| Iron Ingot | `CANDIDVM · FERRVM` | Pure-Iron |
| Gold Ingot | `CANDIDVM · AVRVM` | Pure-Gold |
| Copper Block | `OPVS · AES` | Wrought-Copper |
| Bone Meal | `OSSA · PVLVIS` | Bone-Dust |
| Rotten Flesh | `TABES · CARO` | Rot-Flesh |
| Glowstone Dust | `LVX · PVLVIS` | Light-Dust |

One axis pair plus one material gives the whole ore → ingot chain, for every metal, with no
new vocabulary.

### 4.3 Places and structures

| Thing | Rune word | Reads |
|---|---|---|
| The Overworld | `ORIGO` | the Middle |
| The Nether | `INFERNVS` | the Hells |
| The End | `FINIS` | the Beyond |
| Nether Fortress | `INFERNVS · VALLVM` | Hell-Hold |
| End City | `FINIS · GREX · VALLVM` | End Throng-Hold |
| Bee Nest | `GREX · VACVVM` | Throng-Hollow |
| Beehive | `OPVS · GREX · VACVVM` | Wrought Throng-Hollow |

**Compounds are literal.** A city *is* a throng's wall, so End City needs no word for
"city". A hive *is* a made hollow for a swarm — and dropping `OPVS` (*wrought*) turns the
hive into the natural nest, which is exactly the distinction Minecraft draws.

### 4.4 Made things

| Thing | Rune word | Reads |
|---|---|---|
| Iron Sword | `FRACTVM · FERRVM` | Worked-Iron |
| Iron Chestplate | `TEGMEN · FERRVM` | Covering-Iron |
| Chorus Fruit | `FINIS · GERMEN` | End-Seed |
| Popped Chorus Fruit | `IGNIS · FINIS · GERMEN` | Fire End-Seed |
| Stone Stairs | `FRACTVM · GRADVS` | Cut-Step |
| Torch | `IGNIS · LIGNVM` | Fire-Wood |
| Chest | `OPVS · LIGNVM · VACVVM` | Wrought Wood-Hollow |

### 4.5 The longest word in the language

**Weathered Waxed Cut Copper Stairs** carries five facts — sealed, aged, copper, cut,
stepped. It is one rune word, five glyphs long:

```
TEGMEN  ·  SENEX  ·  AES  ·  FRACTVM  ·  GRADVS
 sealed     aged    copper    cut        step
                                          └── head: the last ✦
```

The head rule needs no extension to read it. `AES` (*copper*) is ✦ and could have headed
the word, but `GRADVS` (*step*) comes after it, so the thing is a **stair** — of worked,
waxed, weathered copper. Move `AES` to the end and the same five glyphs name a *metal*
instead. **Position does all the work, at any length.**

What the length costs is real but is a *budget*, not a grammar problem: the codex holds
20 glyphs for a whole inscription (D8/D9), so a five-glyph name leaves 11 for the other
four clauses. Long names are affordable, but not free — which is the pressure that keeps
most words at two or three glyphs without a rule forbidding more.

> An earlier draft capped rune words at three glyphs and argued that this was a virtue —
> that a language which could name that in one word would be one where nothing was a
> word. **That argument was wrong**, and it was wrong in the way design arguments usually
> are: it defended a limit by inventing a principle for it. German, Finnish and Sanskrit
> all name exactly this kind of thing in one compound. The head rule already handled free
> length; the cap was never carrying it.

The whole oxidation ladder falls out of two axes, with no vocabulary added:

| | bare | waxed |
|---|---|---|
| **fresh** | `NOVVM · AES` | `TEGMEN · NOVVM · AES` |
| **weathered** | `SENEX · AES` | `TEGMEN · SENEX · AES` |

### 4.6 The thirty-item test

The claim being tested is a strong one: **52 runes describe anything in Minecraft.** So the
lexicon was run against thirty things picked to spread across mobs, blocks, materials,
tools, structures and biomes — including several chosen specifically because they looked
hard. **Nothing was minted.** Twenty-seven came out clean; three did not, and the three
are worth more than the twenty-seven.

**Creatures**

| Thing | Rune word | Reads |
|---|---|---|
| Creeper | `CHAOS · HERBA · BESTIA` | Ruin Green-Beast |
| Blaze | `IGNIS · BESTIA` | Fire-Beast |
| Ghast | `INFERNVS · VENTVS · BESTIA` | Hell Air-Beast |
| Slime | `VNDA · BESTIA` | Flow-Beast |
| Wolf, wild | `HOSTIS · BESTIA` | Foe-Beast |
| Wolf, tamed | `MITIS · BESTIA` | Friend-Beast |
| Axolotl | `VNDA · MITIS · BESTIA` | Water Friend-Beast |
| Warden | `TENEBRAE · HOSTIS · BESTIA` | Dark Foe-Beast |
| Allay | `VACVVM · MITIS · BESTIA` | Hollow Friend-Beast |
| Glow Squid | `LVX · VNDA · BESTIA` | Light Water-Beast |

Taming a wolf **flips one pole and changes nothing else** — the language says out loud what
the game means by taming. The Allay works because *incorporeal* was already on the board as
`VACVVM` (*hollow*); no spirit axis was needed.

**Blocks and materials**

| Thing | Rune word | Reads |
|---|---|---|
| Obsidian | `IGNIS · VNDA · SAXVM` | Fire Water-Stone |
| Netherrack | `INFERNVS · SAXVM` | Hell-Stone |
| Redstone Dust | `ORDO · PVLVIS` | Order-Dust |
| Prismarine | `VNDA · SAXVM` | Water-Stone |
| Sea Lantern | `VNDA · LVX · SAXVM` | Water Light-Stone |
| Amethyst Cluster | `GREX · GEMMA` | Throng-Gem |
| Sculk Catalyst | `MORS · GERMEN` | Death-Seed |
| Blaze Powder | `IGNIS · PVLVIS` | Fire-Dust |
| Ender Pearl | `FINIS · GEMMA` | End-Gem |
| Ancient Debris | `INFERNVS · SENEX · FERRVM` | Hell Old-Metal |

Obsidian's word **is its recipe** — fire meeting water in stone. `ORDO · PVLVIS` for
redstone is the one the axis system earns outright: redstone is not a red mineral, it is
*powdered law*, and no lexicon built by listing nouns would have reached it.

**Made things**

| Thing | Rune word | Reads |
|---|---|---|
| Diamond Pickaxe | `SAXVM · FRACTVM · ADAMAS` | Stone-Cutting Diamond |
| Iron Axe | `LIGNVM · FRACTVM · FERRVM` | Wood-Cutting Iron |
| Iron Shovel | `TERRA · FRACTVM · FERRVM` | Earth-Cutting Iron |
| Shield | `HOSTIS · TEGMEN` | Foe-Covering |
| Water Bucket | `VNDA · OPVS · VACVVM` | Water Wrought-Hollow |
| Bookshelf | `ORDO · LIGNVM` | Order-Wood |
| Lodestone | `ORIGO · SAXVM` | Home-Stone |

**The whole tool family fell out of one pattern** — `WHAT IT CUTS · FRACTVM · WHAT IT IS`
— without a rune for "tool", "pickaxe" or "blade". That is the strongest evidence the axis
design works: the tools name their *object*, so the material tier and the tool type are
independent, and every combination in the game is already spelled.

**Places**

| Thing | Rune word | Reads |
|---|---|---|
| Deep Dark | `TENEBRAE · TERRA` | Dark-Earth |
| Woodland Mansion | `HOSTIS · HOMO · VALLVM` | Foe-Folk Hold |
| Nether Portal | `INFERNVS · PORTA` | Hell-Gate |

### 4.7 What the test broke

Three of the thirty failed outright and one collided; each named a defect rather than a
missing word.

**1. `VACVVM` could not head a word.** The Bee Nest, the Beehive and the Chest were already
written with `VACVVM` as head (§4.3, §4.4) while the lexicon had it unmarked — those three
words were ungrammatical and the Water Bucket exposed it. **A hollow is a kind of thing.**
`VACVVM` is now ✦.

**2. `TEGMEN`/`NVDVM` had no ✦ pole at all**, so wax, hide, shell and membrane could not be
named — only used as adjectives. Honeycomb (*the throng's wax*) was unwriteable.
**`TEGMEN` is now ✦**, which also shortens the Shield from three glyphs to two. Neither fix
minted a rune; both corrected a mis-marked one.

| Now writeable | Rune word | Reads |
|---|---|---|
| Honeycomb | `GREX · TEGMEN` | Throng-Wax |
| Turtle Shell | `VNDA · TEGMEN` | Water-Shell |
| Leather | `BESTIA · TEGMEN` | Beast-Hide |

**3. There is no axis of nourishment.** Bread, Cake and Cooked Beef all land on
`OPVS · GERMEN` / `IGNIS · CARO` with nothing to separate a prepared food from its
ingredient. This is the one gap a fix would have to *mint* for, so it is left open rather
than papered over — see Q11.

**4. `VNDA · SAXVM` was claimed by three blocks.** Prismarine, Pointed Dripstone and Clay
all read *water-stone*. One ordered sequence must resolve to exactly one thing (§5), so
two of the three have to move — and since rune words are free-length (D8), moving them
costs a qualifier rather than a rune:

| Thing | Rune word | Reads |
|---|---|---|
| Prismarine | `VNDA · SAXVM` | Water-Stone — got there first |
| Pointed Dripstone | `VNDA · GRADVS · SAXVM` | Water Step-Stone |
| Clay | `VNDA · TERRA · SAXVM` | Water Earth-Stone |

**A collision inside the language is a content bug**, caught at load by the same validator
that catches two glyphs rendering alike. It is worth counting as a failure of the test
anyway: the lexicon did not *prevent* it, the validator did.

**The score is 27 clean, 2 bugs found and fixed, 1 open.** A test where everything passes
would have meant the test was too easy.

---

## 5. Rune words as data

```jsonc
// data/epigraphy/rune_words/piglin.json
{
  "glyphs": ["epigraphy:infernus", "epigraphy:homo"],   // QUALIFIER · HEAD, ordered
  "means":  { "type": "entity", "value": "minecraft:piglin" },
  "reading": "Folk of the hells.",                      // shown once decoded
  "hint":    "Something that walks upright, where it burns."
}
```

Two validation rules matter most:

- **No two rune words may share the same ordered glyph sequence.** Codex submission has to
  be deterministic, and a reversed sequence must correctly *miss*.
- **The last glyph must be ✦ head-capable.** A word headed by a qualifier is a grammar
  error and should fail the datapack load rather than ship.

Authoring guide: [`AUTHORING.md`](AUTHORING.md).

---

## 6. What a player actually sees

A glyph reads differently depending on how much the player knows:

| Tier | The tile | The text |
|---|---|---|
| **0 · Unknown** | uncut stone | nothing |
| **1 · Sighted** | a shallow, unfilled scratch | nothing |
| **2 · Learned** | cut deep and inlaid | the glyph's literal gloss — *hell*, *folk* |
| **3 · Decoded** | unchanged | the word's true referent — *Piglin* |

So a player who has learned both glyphs of an undecoded word sees **"hell · folk"** and has
to make the leap themselves. **That leap is the game.**

**Colour never carries anything on its own.** Desaturate the whole atlas and every glyph
stays distinct — verified in the audit, not asserted. Identity lives in the shapes.
