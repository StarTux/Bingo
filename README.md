# Bingo

Minecraft Survial Scavenger Hunt (Bingo) plugin.

## Abstract

Each player gets 25 items to collect out of a predefined list of
materials. Said items are arranged in a 5x5 grid. Collecting one
complete row, column, or diagonal of 5 items will win you bingo, which
adds you to the member list (`/ml`) and unlocks the Bingo title
(`/titles`).

## Commands

- `/bingo` opens the player GUI
- `/bingoadmin` has some admin commands

## Permissions

- `bingo.bingo` - Use `/bingo`
- `bingo.admin` - Use `/bingoadmin`

## Dependencies

### Hard Dependencies

- **Core** with its DefaultFont
- **Mytems** to display the checked checkmark item

### Soft Dependencies

These are used via commands:

- **Title** to unlock the title
- **MemberList** to remember participating players