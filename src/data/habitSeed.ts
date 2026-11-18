export type HabitGoal = 'EVERY_DAY' | 'FIVE_DAYS_A_WEEK' | 'THREE_DAYS_A_WEEK';

export type HabitIconId =
  | 'WATER_DROP'
  | 'BOOK'
  | 'MOON'
  | 'RUN'
  | 'LEAF'
  | 'DUMBBELL'
  | 'EDIT'
  | 'MUSIC'
  | 'HEART'
  | 'SUN'
  | 'BOLT'
  | 'STAR';

export type HabitColorId =
  | 'GREEN'
  | 'BLUE'
  | 'AMBER'
  | 'CORAL'
  | 'PURPLE'
  | 'TEAL'
  | 'PINK'
  | 'ORANGE';

export const HABIT_COLORS: Record<HabitColorId, { hex: string; bgTint: string }> = {
  GREEN: { hex: '#2FB364', bgTint: 'rgba(47, 179, 100, 0.16)' },
  BLUE: { hex: '#4F8EF7', bgTint: 'rgba(79, 142, 247, 0.16)' },
  AMBER: { hex: '#F5A623', bgTint: 'rgba(245, 166, 35, 0.16)' },
  CORAL: { hex: '#F25F5C', bgTint: 'rgba(242, 95, 92, 0.16)' },
  PURPLE: { hex: '#9D6BFF', bgTint: 'rgba(157, 107, 255, 0.16)' },
  TEAL: { hex: '#25B8A8', bgTint: 'rgba(37, 184, 168, 0.16)' },
  PINK: { hex: '#EE5CA5', bgTint: 'rgba(238, 92, 165, 0.16)' },
  ORANGE: { hex: '#F37324', bgTint: 'rgba(243, 115, 36, 0.16)' },
};

export const HABIT_COLOR_ORDER: HabitColorId[] = [
  'GREEN',
  'BLUE',
  'AMBER',
  'CORAL',
  'PURPLE',
  'TEAL',
  'PINK',
  'ORANGE',
];

export const HABIT_ICON_ORDER: HabitIconId[] = [
  'WATER_DROP',
  'BOOK',
  'MOON',
  'RUN',
  'LEAF',
  'DUMBBELL',
  'EDIT',
  'MUSIC',
  'HEART',
  'SUN',
  'BOLT',
  'STAR',
];

export const GOAL_META: Record<HabitGoal, { targetPerWeek: number; label: string }> = {
  EVERY_DAY: { targetPerWeek: 7, label: 'Every day' },
  FIVE_DAYS_A_WEEK: { targetPerWeek: 5, label: '5 days a week' },
  THREE_DAYS_A_WEEK: { targetPerWeek: 3, label: '3 days a week' },
};

export interface HabitItem {
  id: string;
  name: string;
  icon: HabitIconId;
  color: HabitColorId;
  goal: HabitGoal;
  completedDates: string[]; // YYYY-MM-DD
}

export function formatIsoDate(date: Date): string {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

export function addDays(date: Date, delta: number): Date {
  const copy = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  copy.setDate(copy.getDate() + delta);
  return copy;
}

export function getMondayOfWeek(date: Date): Date {
  const copy = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  const day = copy.getDay(); // 0=Sun..6=Sat
  const diff = day === 0 ? -6 : 1 - day;
  copy.setDate(copy.getDate() + diff);
  return copy;
}

export function buildInitialHabits(today: Date): HabitItem[] {
  const currentMonday = getMondayOfWeek(today);

  const waterDates: string[] = [];
  for (let d = 1; d <= 14; d++) waterDates.push(formatIsoDate(addDays(today, -d)));
  for (let d = 16; d <= 139; d++) {
    if (d % 4 !== 0) waterDates.push(formatIsoDate(addDays(today, -d)));
  }

  const cookDates: string[] = [];
  if (formatIsoDate(today) !== formatIsoDate(currentMonday)) {
    cookDates.push(formatIsoDate(currentMonday));
  }
  for (let w = 1; w <= 19; w++) {
    const mon = addDays(currentMonday, -7 * w);
    [0, 1, 3, 4, 6].forEach((offset) => {
      if ((w + offset) % 6 !== 0) {
        cookDates.push(formatIsoDate(addDays(mon, offset)));
      }
    });
  }

  const vitaminDates: string[] = [];
  for (let d = 0; d <= 40; d++) vitaminDates.push(formatIsoDate(addDays(today, -d)));
  for (let d = 42; d <= 139; d++) {
    if (d % 5 !== 0) vitaminDates.push(formatIsoDate(addDays(today, -d)));
  }

  const yogaDates: string[] = [
    formatIsoDate(currentMonday),
    formatIsoDate(today),
  ];
  for (let w = 1; w <= 19; w++) {
    const mon = addDays(currentMonday, -7 * w);
    const offsets = w <= 8 ? [0, 2, 6] : [1, 4];
    offsets.forEach((offset) => yogaDates.push(formatIsoDate(addDays(mon, offset))));
  }

  const readDates: string[] = [];
  for (let d = 0; d <= 5; d++) readDates.push(formatIsoDate(addDays(today, -d)));
  for (let d = 7; d <= 139; d++) {
    if (d % 3 !== 0) readDates.push(formatIsoDate(addDays(today, -d)));
  }

  return [
    {
      id: 'water',
      name: 'Two litres of water',
      icon: 'WATER_DROP',
      color: 'BLUE',
      goal: 'EVERY_DAY',
      completedDates: Array.from(new Set(waterDates)),
    },
    {
      id: 'cook',
      name: 'Cook at home',
      icon: 'STAR',
      color: 'ORANGE',
      goal: 'FIVE_DAYS_A_WEEK',
      completedDates: Array.from(new Set(cookDates)),
    },
    {
      id: 'vitamins',
      name: 'Vitamins',
      icon: 'HEART',
      color: 'CORAL',
      goal: 'EVERY_DAY',
      completedDates: Array.from(new Set(vitaminDates)),
    },
    {
      id: 'yoga',
      name: 'Yoga',
      icon: 'LEAF',
      color: 'GREEN',
      goal: 'THREE_DAYS_A_WEEK',
      completedDates: Array.from(new Set(yogaDates)),
    },
    {
      id: 'read',
      name: 'Read before bed',
      icon: 'BOOK',
      color: 'AMBER',
      goal: 'EVERY_DAY',
      completedDates: Array.from(new Set(readDates)),
    },
  ];
}

export function calculateDailyStreak(completedSet: Set<string>, today: Date): number {
  let cursor = completedSet.has(formatIsoDate(today)) ? today : addDays(today, -1);
  let streak = 0;
  while (completedSet.has(formatIsoDate(cursor))) {
    streak++;
    cursor = addDays(cursor, -1);
  }
  return streak;
}

export function calculateWeeklyGoalStreak(
  completedSet: Set<string>,
  targetPerWeek: number,
  today: Date
): number {
  const currentMonday = getMondayOfWeek(today);
  let streak = 0;
  let currentCount = 0;
  for (let i = 0; i < 7; i++) {
    if (completedSet.has(formatIsoDate(addDays(currentMonday, i)))) currentCount++;
  }
  if (currentCount >= targetPerWeek) streak++;

  let weekCursor = addDays(currentMonday, -7);
  while (true) {
    let count = 0;
    for (let i = 0; i < 7; i++) {
      if (completedSet.has(formatIsoDate(addDays(weekCursor, i)))) count++;
    }
    if (count >= targetPerWeek) {
      streak++;
      weekCursor = addDays(weekCursor, -7);
    } else {
      break;
    }
  }
  return streak;
}

export function calculateThirtyDayConsistency(
  completedSet: Set<string>,
  goal: HabitGoal,
  today: Date
): number {
  let count = 0;
  for (let i = 0; i < 30; i++) {
    if (completedSet.has(formatIsoDate(addDays(today, -i)))) count++;
  }
  const expected = (GOAL_META[goal].targetPerWeek / 7) * 30;
  return Math.min(100, Math.round((count / expected) * 100));
}
