# Design — LoaCell

## Genre

Modern-minimal. The interface prioritises fast scanning and confident action during raid preparation.

## App structure

- Main rooms: concise workbench list with a clear empty state.
- Raid schedule: contextual toolbar, then high-contrast schedule cards.
- Room context: compact metadata panel above task content.

## Theme

- Preserve the existing Material 3 blue palette and system light/dark mode.
- Use surface containers for hierarchy; reserve primary blue for selected and primary actions.
- Completed raids use a quieter container, never colour alone, and always retain a state icon.

## Typography and spacing

- Display and body: Roboto, using the Material 3 type scale.
- 4dp spacing scale through `LoaCellSpace`.
- Cards: 16dp radius; compact controls maintain at least 48dp touch targets.

## Motion and feedback

- State changes are restrained and use Material components' standard motion.
- Successful completion is silent because the state is visible in the list.
- Focus and content descriptions remain visible for assistive technology.

## CTA voice

- Primary: filled blue action for creating or entering.
- Secondary: tonal or outlined controls for view and filter changes.
