# Saitama App - Requirements Document v1.0

## Table of Contents
1. [Overview](#overview)
2. [Architecture Changes](#architecture-changes)
3. [Homepage](#homepage)
4. [Weight Tracker](#weight-tracker)
5. [Cigarette Tracker](#cigarette-tracker)
6. [Navigation](#navigation)
7. [Data Models](#data-models)
8. [Wireframes](#wireframes)

---

## Overview

### Current State
Single-screen weight tracking app with calendar view, 7-day average, and 6-month trend graph.

### Target State
Two-page app with:
- **Homepage**: Dashboard with Weight and Cigarette tracking tiles
- **Detail Pages**: Dedicated pages for Weight and Cigarette tracking with full features

### Tech Stack (No Changes)
- Kotlin + Jetpack Compose (Material 3)
- Room Database
- MVVM Architecture
- Kzitonwose Calendar library

---

## Architecture Changes

### Navigation Structure
```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│                      HOMEPAGE                           │
│                                                         │
│  ┌─────────────────┐      ┌─────────────────┐          │
│  │  WEIGHT TILE    │      │   CIG TILE      │          │
│  │                 │      │                 │          │
│  └────────┬────────┘      └────────┬────────┘          │
│           │                        │                    │
│           ▼                        ▼                    │
│  ┌─────────────────┐      ┌─────────────────┐          │
│  │ WEIGHT DETAIL   │      │  CIG DETAIL     │          │
│  │     PAGE        │      │     PAGE        │          │
│  └─────────────────┘      └─────────────────┘          │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### App Launch
- App opens to **Homepage** (not current weight screen)
- No bottom navigation bar
- Navigation via tile taps only

---

## Homepage

### Layout
- Two tiles of **equal height**
- Tiles fill the entire screen proportionally
- Design should not look stretched or oversized
- Consistent spacing and padding

### Weight Tile

#### With Data
| Element | Description |
|---------|-------------|
| Title | "WEIGHT" |
| Navigation | ">>" button (top-right) |
| Average | "7-day avg: XX.XX kg" (last 7 entries, NOT calendar days) |
| Today's Weight | Editable input field, pre-filled if exists |
| Trend Indicator | Arrow (↑/↓) + difference from last recorded entry |
| Trend Color | Theme color (gold/orange) - neutral, not green/red |

#### Behavior
- **Tap tile** (except input area): Navigate to Weight Detail page
- **Tap >> button**: Navigate to Weight Detail page
- **Input field**: Does NOT navigate, allows weight entry
- **Auto-save**: Saves after 1 second pause in typing
- **Auto-update**: Overwrites existing entry without confirmation
- **Validation**: Only save valid numbers (0-999.99, up to 2 decimals)

#### No Data State
- Message: "Let's begin your journey!"
- Input field: "Enter your weight: [____] kg"
- No trend indicator shown

### Cigarette Tile

#### With Target Set
| Element | Description |
|---------|-------------|
| Title | "CIGARETTES" |
| Navigation | ">>" button (top-right) |
| Status | "Today: X / Y" (current count / target) |
| Message | Severity-based motivational message (random from pool) |
| Counter | [-] [ count ] [+] buttons |

#### Without Target Set
| Element | Description |
|---------|-------------|
| Title | "CIGARETTES" |
| Navigation | ">>" button (top-right) |
| Nudge | "⚠️ Set your daily target →" (tappable) |
| Status | "Today: X" (no target shown) |
| Counter | [-] [ count ] [+] buttons |

#### Behavior
- **Tap tile** (except counter): Navigate to Cig Detail page
- **Tap >> button**: Navigate to Cig Detail page
- **Tap nudge**: Navigate to Cig Detail page WITH target dialog open
- **Counter buttons**: Instant save on tap
- **[-] button**: Disabled when count = 0
- **[+] button**: Disabled when count = 99
- **No target**: Treat as target = 0 for all calculations

#### Severity Messages

| Level | Condition | Example Messages |
|-------|-----------|------------------|
| Safe | count < 50% of target | "Great start! Keep it up!", "You're doing amazing!", "Stay on track!" |
| Caution | count 50-99% of target | "You can light one, stay mindful", "Going well, be aware", "Pace yourself" |
| At Limit | count = target | "You've hit your limit!", "Hold steady!", "That's your quota for today" |
| Over | count > target | "Resist! You're stronger than you know", "Stay strong, tomorrow is new", "You've got this, pause and breathe" |
| Zero-Zero | target = 0, count = 0 | "Stay strong, you can do it!", "You're crushing it!", "Keep going!" |

---

## Weight Tracker

### Weight Detail Page

#### Layout (Same as Current App + Back Button)
```
┌─────────────────────────────────────┐
│ ← Back                              │
├─────────────────────────────────────┤
│  ┌───────────────────────────────┐  │
│  │     AVERAGE WEIGHT CARD       │  │
│  │     7-day avg: 75.42 kg       │  │
│  │     (Last 7 entries)          │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │     WEIGHT TREND GRAPH        │  │
│  │     (Last 6 months)           │  │
│  │     Current | Change | Range  │  │
│  │     ~~~~~~~~~~~~~~~~~~~~~~~~  │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │     CALENDAR                  │  │
│  │     (3-year lookback)         │  │
│  │     Tap date to enter weight  │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
```

#### Changes from Current
| Change | Description |
|--------|-------------|
| Add back button | Top-left, navigates to Homepage |
| Update average logic | Use last 7 ENTRIES (not 7 calendar days) |
| Keep everything else | Graph, calendar, entry dialog unchanged |

### Weight Entry Dialog (Unchanged)
- Date display
- Text input field for weight
- "kg" suffix
- Save / Cancel buttons
- Delete button (if entry exists)

---

## Cigarette Tracker

### Cig Detail Page

#### Layout
```
┌─────────────────────────────────────┐
│ ← Back                              │
├─────────────────────────────────────┤
│  ┌───────────────────────────────┐  │
│  │     TARGET SELECTOR           │  │
│  │                               │  │
│  │  0 ────●──────────────── 10   │  │
│  │         5                     │  │
│  │                     [Save]    │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │     CIG TREND GRAPH           │  │
│  │     (Last 6 months)           │  │
│  │     Y: count, X: dates        │  │
│  │     NO stats box              │  │
│  │     ~~~~~~~~~~~~~~~~~~~~~~~~  │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │     CALENDAR                  │  │
│  │     (3-year lookback)         │  │
│  │     Green: count ≤ target     │  │
│  │     Red: count > target       │  │
│  │     Normal: no entry          │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
```

### Target Selector
| Element | Description |
|---------|-------------|
| Type | Slider/Seekbar |
| Range | 0 to 10 |
| Display | Shows current selected value |
| Save | Requires "Save" button tap to persist |
| Default | No default, user must set |

### Cig Trend Graph
| Element | Description |
|---------|-------------|
| Y-axis | Cigarette count |
| X-axis | Dates |
| Time range | Last 6 months |
| Stats | NONE (unlike weight graph) |
| Style | Same line + gradient as weight graph |

### Cig Calendar
| Element | Description |
|---------|-------------|
| Lookback | 3 years |
| Navigation | Horizontal month swiping |
| Day colors | Green (≤ target), Red (> target), Normal (no entry) |
| Day content | Show count number if entry exists |
| Tap action | Opens counter dialog |

### Cig Entry Dialog (Counter Style)
```
┌─────────────────────────────────┐
│                                 │
│       December 19, 2025         │
│                                 │
│       [-]    [ 5 ]    [+]       │
│                                 │
│              [Done]             │
└─────────────────────────────────┘
```

| Element | Description |
|---------|-------------|
| Date | Display selected date |
| Counter | Current count value |
| [-] button | Decrement, disabled at 0 |
| [+] button | Increment, disabled at 99 |
| Range | 0 - 99 |
| Save | Instant on +/- tap |
| Done | Closes dialog |

---

## Navigation

### Transitions
- **Homepage → Detail Pages**: Slide left
- **Detail Pages → Homepage**: Slide right
- **Back button**: Top-left on detail pages

### Navigation Triggers

| From | Action | To |
|------|--------|----|
| Homepage | Tap Weight tile (non-input area) | Weight Detail |
| Homepage | Tap Weight >> button | Weight Detail |
| Homepage | Tap Cig tile (non-counter area) | Cig Detail |
| Homepage | Tap Cig >> button | Cig Detail |
| Homepage | Tap "Set target" nudge | Cig Detail + Target dialog open |
| Weight Detail | Tap back button | Homepage |
| Weight Detail | System back | Homepage |
| Cig Detail | Tap back button | Homepage |
| Cig Detail | System back | Homepage |

---

## Data Models

### Existing (Modified)

#### WeightEntry (No Change)
```kotlin
@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey val date: Date,
    val weight: Double,
    val lastUpdated: Date = Date()
)
```

#### WeightRepository (Modified)
```kotlin
// CHANGE: getWeightsForLastSevenDays() → getLastSevenEntries()
// Old: Returns entries from last 7 calendar days
// New: Returns last 7 entries regardless of date
```

### New

#### CigEntry
```kotlin
@Entity(tableName = "cig_entries")
data class CigEntry(
    @PrimaryKey val date: Date,
    val count: Int,  // 0-99
    val lastUpdated: Date = Date()
)
```

#### CigTarget
```kotlin
@Entity(tableName = "cig_target")
data class CigTarget(
    @PrimaryKey val id: Int = 1,  // Single row
    val target: Int,  // 0-10
    val lastUpdated: Date = Date()
)
```

### Database Changes
- Add `cig_entries` table
- Add `cig_target` table
- Increment database version
- Add migration

---

## Wireframes

### Homepage
```
╔══════════════════════════════════════════════════════════════╗
║                                                              ║
║  ┌────────────────────────────────────────────────────────┐  ║
║  │                                                    >>  │  ║
║  │  WEIGHT                                                │  ║
║  │  ──────────────────────────────────────────────────    │  ║
║  │                                                        │  ║
║  │                  7-day avg                             │  ║
║  │                 ┌────────┐                             │  ║
║  │                 │ 75.42  │ kg                          │  ║
║  │                 └────────┘                             │  ║
║  │                                                        │  ║
║  │  Today                                                 │  ║
║  │  ┌──────────────┐                                      │  ║
║  │  │    72.5      │ kg                    ↓ 0.3 kg       │  ║
║  │  └──────────────┘                                      │  ║
║  │                                                        │  ║
║  └────────────────────────────────────────────────────────┘  ║
║                                                              ║
║  ┌────────────────────────────────────────────────────────┐  ║
║  │                                                    >>  │  ║
║  │  CIGARETTES                                            │  ║
║  │  ──────────────────────────────────────────────────    │  ║
║  │                                                        │  ║
║  │                  Today                                 │  ║
║  │                 ┌──────┐                               │  ║
║  │                 │ 3/5  │                               │  ║
║  │                 └──────┘                               │  ║
║  │                                                        │  ║
║  │        "You're doing great, stay strong!"              │  ║
║  │                                                        │  ║
║  │              ┌───┐ ┌───┐ ┌───┐                         │  ║
║  │              │ - │ │ 3 │ │ + │                         │  ║
║  │              └───┘ └───┘ └───┘                         │  ║
║  │                                                        │  ║
║  └────────────────────────────────────────────────────────┘  ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
```

### Homepage - No Data States
```
╔══════════════════════════════════════════════════════════════╗
║                                                              ║
║  ┌────────────────────────────────────────────────────────┐  ║
║  │                                                    >>  │  ║
║  │  WEIGHT                                                │  ║
║  │  ──────────────────────────────────────────────────    │  ║
║  │                                                        │  ║
║  │            Let's begin your journey!                   │  ║
║  │                                                        │  ║
║  │  Enter your weight                                     │  ║
║  │  ┌──────────────┐                                      │  ║
║  │  │              │ kg                                   │  ║
║  │  └──────────────┘                                      │  ║
║  │                                                        │  ║
║  └────────────────────────────────────────────────────────┘  ║
║                                                              ║
║  ┌────────────────────────────────────────────────────────┐  ║
║  │                                                    >>  │  ║
║  │  CIGARETTES                                            │  ║
║  │  ──────────────────────────────────────────────────    │  ║
║  │                                                        │  ║
║  │         ⚠️ Set your daily target →                     │  ║
║  │                                                        │  ║
║  │                  Today                                 │  ║
║  │                 ┌──────┐                               │  ║
║  │                 │  0   │                               │  ║
║  │                 └──────┘                               │  ║
║  │                                                        │  ║
║  │              ┌───┐ ┌───┐ ┌───┐                         │  ║
║  │              │ - │ │ 0 │ │ + │                         │  ║
║  │              └───┘ └───┘ └───┘                         │  ║
║  │                                                        │  ║
║  └────────────────────────────────────────────────────────┘  ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
```

### Weight Detail Page
```
╔══════════════════════════════════════════════════════════════╗
║  ← Back                                                      ║
║                                                              ║
║  ┌────────────────────────────────────────────────────────┐  ║
║  │                                                        │  ║
║  │                   Average Weight                       │  ║
║  │                                                        │  ║
║  │                 ┌──────────────┐                       │  ║
║  │                 │    75.42    │ kg                     │  ║
║  │                 └──────────────┘                       │  ║
║  │                    Last 7 days                         │  ║
║  │                                                        │  ║
║  └────────────────────────────────────────────────────────┘  ║
║                                                              ║
║  ┌────────────────────────────────────────────────────────┐  ║
║  │  Weight Trend                           Last 6 months  │  ║
║  │                                                        │  ║
║  │   Current      Change       Range                      │  ║
║  │   72.5 kg      -2.3 kg      70.1-77.8                  │  ║
║  │                                                        │  ║
║  │   78 │                                                 │  ║
║  │      │    ·  ·                                         │  ║
║  │   75 │  ·      ·  ·                                    │  ║
║  │      │            ·  ·  ·                              │  ║
║  │   72 │                    ·  ·                         │  ║
║  │      └────────────────────────────                     │  ║
║  │       Jun    Jul    Aug    Sep    Oct    Nov    Dec    │  ║
║  └────────────────────────────────────────────────────────┘  ║
║                                                              ║
║  ┌────────────────────────────────────────────────────────┐  ║
║  │                  December 2025                         │  ║
║  │                                                        │  ║
║  │   Su   Mo   Tu   We   Th   Fr   Sa                     │  ║
║  │        1    2    3    4    5    6                      │  ║
║  │        ✓    ✓    ✓         ✓    ✓                      │  ║
║  │                                                        │  ║
║  │   7    8    9    10   11   12   13                     │  ║
║  │   ✓    ✓    ✓    ✓    ✓    ✓    ✓                      │  ║
║  │                                                        │  ║
║  │   14   15   16   17   18  [19]  20                     │  ║
║  │   ✓    ✓    ✓    ✓    ✓              (future)         │  ║
║  │                                                        │  ║
║  │   21   22   23   24   25   26   27                     │  ║
║  │                  (future dates)                        │  ║
║  │                                                        │  ║
║  └────────────────────────────────────────────────────────┘  ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
```

### Weight Entry Dialog
```
         ┌─────────────────────────────────┐
         │                                 │
         │       December 19, 2025         │
         │                                 │
         │   ┌───────────────────────┐     │
         │   │        72.5           │ kg  │
         │   └───────────────────────┘     │
         │                                 │
         │                                 │
         │   Delete      Cancel    Save    │
         │   (red)                         │
         │                                 │
         └─────────────────────────────────┘
```

### Cig Detail Page
```
╔══════════════════════════════════════════════════════════════╗
║  ← Back                                                      ║
║                                                              ║
║  ┌────────────────────────────────────────────────────────┐  ║
║  │                                                        │  ║
║  │                   Daily Target                         │  ║
║  │                                                        │  ║
║  │     0 ─────────────●───────────────────────────── 10   │  ║
║  │                    5                                   │  ║
║  │                                                        │  ║
║  │                                          ┌──────────┐  │  ║
║  │                                          │   Save   │  │  ║
║  │                                          └──────────┘  │  ║
║  │                                                        │  ║
║  └────────────────────────────────────────────────────────┘  ║
║                                                              ║
║  ┌────────────────────────────────────────────────────────┐  ║
║  │  Cigarette Trend                        Last 6 months  │  ║
║  │                                                        │  ║
║  │   10 │                                                 │  ║
║  │      │                                                 │  ║
║  │    8 │       ·                                         │  ║
║  │      │    ·     ·                                      │  ║
║  │    6 │  ·          ·                                   │  ║
║  │      │                ·   ·                            │  ║
║  │    4 │                       ·   ·   ·                 │  ║
║  │      │                                                 │  ║
║  │    2 │                                                 │  ║
║  │      └────────────────────────────────                 │  ║
║  │       Jun    Jul    Aug    Sep    Oct    Nov    Dec    │  ║
║  └────────────────────────────────────────────────────────┘  ║
║                                                              ║
║  ┌────────────────────────────────────────────────────────┐  ║
║  │                  December 2025                         │  ║
║  │                                                        │  ║
║  │   Su   Mo   Tu   We   Th   Fr   Sa                     │  ║
║  │        1    2    3    4    5    6                      │  ║
║  │        3    5    4    2    6    3                      │  ║
║  │       (G)  (G)  (G)  (G)  (R)  (G)                     │  ║
║  │                                                        │  ║
║  │   7    8    9    10   11   12   13                     │  ║
║  │   4    3    5    7    4    3    5                      │  ║
║  │  (G)  (G)  (G)  (R)  (G)  (G)  (G)                     │  ║
║  │                                                        │  ║
║  │   14   15   16   17   18  [19]  20                     │  ║
║  │   4    3    5    4    3              (future)          │  ║
║  │  (G)  (G)  (G)  (G)  (G)                               │  ║
║  │                                                        │  ║
║  │   (G) = Green (≤ target)                               │  ║
║  │   (R) = Red (> target)                                 │  ║
║  │                                                        │  ║
║  └────────────────────────────────────────────────────────┘  ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
```

### Cig Entry Dialog
```
         ┌─────────────────────────────────┐
         │                                 │
         │       December 19, 2025         │
         │                                 │
         │                                 │
         │      ┌───┐  ┌─────┐  ┌───┐      │
         │      │ - │  │  5  │  │ + │      │
         │      └───┘  └─────┘  └───┘      │
         │                                 │
         │                                 │
         │           ┌────────┐            │
         │           │  Done  │            │
         │           └────────┘            │
         │                                 │
         └─────────────────────────────────┘
```

### Cig Target Dialog (Opened from Homepage Nudge)
```
         ┌─────────────────────────────────┐
         │                                 │
         │       Set Your Daily Target     │
         │                                 │
         │  0 ───────────●─────────── 10   │
         │               5                 │
         │                                 │
         │                                 │
         │    Cancel              Save     │
         │                                 │
         └─────────────────────────────────┘
```

---

## Implementation Phases

### Phase 1: Foundation
- [ ] Set up navigation component
- [ ] Create Homepage scaffold
- [ ] Add new database tables (CigEntry, CigTarget)
- [ ] Database migration

### Phase 2: Weight Tracker Updates
- [ ] Update average calculation (7 entries vs 7 days)
- [ ] Add back button to Weight Detail page
- [ ] Create Weight tile for Homepage

### Phase 3: Cigarette Tracker
- [ ] Create CigDao, CigRepository, CigViewModel
- [ ] Create Cig Detail page (target, graph, calendar)
- [ ] Create Cig tile for Homepage
- [ ] Implement severity messages

### Phase 4: Polish
- [ ] Navigation transitions (slide)
- [ ] Edge cases and error handling
- [ ] UI refinements

---

## Approval Checklist

Please review and confirm:

- [ ] Homepage layout and tile design
- [ ] Weight tile behavior (auto-save, trend indicator)
- [ ] Cig tile behavior (instant save, severity messages)
- [ ] Weight Detail page (same as current + back button)
- [ ] Cig Detail page (target slider, graph, colored calendar)
- [ ] Navigation flow
- [ ] Data models

---

**Document Version**: 1.0
**Created**: December 19, 2025
**Status**: Awaiting Approval
