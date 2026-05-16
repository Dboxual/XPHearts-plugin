# XPHearts — Claude Context

## Project purpose

XPHearts is a Paper 1.21.11 plugin that ties player health to XP level. Additional hearts are earned by leveling up. On top of that it adds: grindstone XP bottling, an XP multiplier system via the Soul Bound Ledger (a WRITABLE_BOOK charm), and rotten flesh → leather smelting. Built for a private SMP, Geyser/Bedrock compatible, no hard dependencies.

---

## Build

**Gradle does not work on this machine** — Java 25 is installed and Gradle 8.x/9.x cannot run under it. All builds use manual javac.

```bash
PROJ="/Users/davidkocaj/Desktop/Claude/XPHearts-plugin"
CACHE="$HOME/.gradle/caches/modules-2/files-2.1"

CP="\
$CACHE/io.papermc.paper/paper-api/1.21.11-R0.1-SNAPSHOT/20961a0e95ea65d0af88dffc9b1a90421b4dfeca/paper-api-1.21.11-R0.1-SNAPSHOT.jar:\
$CACHE/me.clip/placeholderapi/2.11.6/d9ad7a4c2759a6cc5c824cf56e5d06f12333f88/placeholderapi-2.11.6.jar:\
$CACHE/net.kyori/adventure-text-serializer-legacy/4.26.1/3267b14ac7fa167b97beb8c114d87d83609847af/adventure-text-serializer-legacy-4.26.1.jar:\
$CACHE/net.kyori/adventure-key/4.26.1/6ded614dc07cc6c2da418a8e907ee42325badcda/adventure-key-4.26.1.jar:\
$CACHE/net.kyori/adventure-api/4.26.1/907ea365968cae9bdd84d19f2c258f65cf5f12a4/adventure-api-4.26.1.jar:\
$CACHE/net.kyori/adventure-text-serializer-json/4.26.1/7f98d4d9105254b1567379629a52236f5ebaf215/adventure-text-serializer-json-4.26.1.jar:\
$CACHE/net.kyori/adventure-text-serializer-plain/4.26.1/cce5ad32da24b824edc8518535431059340873d5/adventure-text-serializer-plain-4.26.1.jar:\
$CACHE/net.kyori/examination-api/1.3.0/8a2d185275307f1e2ef2adf7152b9a0d1d44c30b/examination-api-1.3.0.jar:\
$CACHE/com.google.guava/guava/33.3.1-jre/852f8b363da0111e819460021ca693cacca3e8db/guava-33.3.1-jre.jar:\
$CACHE/org.jetbrains/annotations/26.0.2/c7ce3cdeda3d18909368dfe5977332dfad326c6d/annotations-26.0.2.jar:\
$CACHE/net.md-5/bungeecord-chat/1.21-R0.2-deprecated+build.21/a87a9222a1dcfa429b4a06264899f65313a4ed5c/bungeecord-chat-1.21-R0.2-deprecated+build.21.jar"

find "$PROJ/src/main/java" -name "*.java" > "$PROJ/build/sources.txt"
rm -rf "$PROJ/build/classes" && mkdir -p "$PROJ/build/classes"
javac --release 21 -cp "$CP" -d "$PROJ/build/classes" @"$PROJ/build/sources.txt"

VERSION="1.4.0"
OUT="$PROJ/build/releases/$VERSION"
mkdir -p "$OUT"
jar cf "$OUT/xphearts-${VERSION}.jar" \
  -C "$PROJ/build/classes" . \
  -C "$PROJ/src/main/resources" plugin.yml \
  -C "$PROJ/src/main/resources" config.yml
```

All dependency jars are in the Gradle cache at `~/.gradle/caches/modules-2/files-2.1/`. The `build.gradle.kts` exists for IDE dependency resolution only — not used for compilation.

**Output:** `build/releases/{version}/xphearts-{version}.jar`

---

## Java / Paper target

- Java: 21 (`--release 21`)
- Paper API: 1.21.11
- `api-version: '1.21'` in plugin.yml

---

## Key design notes

**Soul Bound Ledger (charm):**
- Material: `WRITABLE_BOOK`
- Identified purely by PDC key `charm_id` = `"xp_multiplier_charm"` — never by material or display name
- Players MAY open the book editor (UI is allowed); saves and signing are blocked by `PlayerEditBookEvent` at HIGHEST priority
- The book can never become a `WRITTEN_BOOK` — cancelling `PlayerEditBookEvent` keeps all custom name, lore, and PDC data intact
- When fully charged, right-clicking in the offhand cancels the interact event (prevents the book editor from opening during consume)
- Old EMERALD charm items auto-migrate to WRITABLE_BOOK via `migrateToLedger()` on first interaction

**CharmListener structure:**
- `onMobDeath` — applies XP multiplier and charges the ledger
- `onRightClick` at HIGHEST — handles token application (offhand) and fully-charged consume (offhand only); non-charged ledger right-clicks are not cancelled
- `onPlayerEditBook` at HIGHEST — cancels all book edits/signings when the player holds a ledger in either hand

**Withdraw token:**
- Material: `NETHER_STAR`
- PDC key `token_id` = `"xp_multiplier_token"`, amount stored in `token_amount` (DOUBLE)
- Apply by placing in offhand and right-clicking

**Version bumping:**
- Update `VERSION` constant in `XPHearts.java`
- Update `version` in `build.gradle.kts`
- Update `version` in `plugin.yml`

---

## Current version

1.4.0

---

## Known issues / next TODOs

- Gradle cannot run on Java 25 — the build.gradle.kts exists for IDE only. Builds must use the manual javac command above. If a new machine has a compatible JDK (21 or lower) in the toolchain, Gradle can be restored.
- No known gameplay bugs in v1.4.0.
- PlaceholderAPI placeholders are registered but only active if PAPI is installed.
