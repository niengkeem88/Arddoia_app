import { useMemo, useState } from 'react';
import { BarChart2, Check, Home, LayoutGrid, Plus, Trash2, X } from 'lucide-react';
import {
  addDays,
  calculateDailyStreak,
  calculateThirtyDayConsistency,
  calculateWeeklyGoalStreak,
  formatIsoDate,
  getMondayOfWeek,
  GOAL_META,
  HABIT_COLOR_ORDER,
  HABIT_COLORS,
  HABIT_ICON_ORDER,
  HabitColorId,
  HabitGoal,
  HabitIconId,
  HabitItem,
} from '../data/habitSeed';
import { HabitGlyph } from './HabitGlyph';

type TabId = 'today' | 'habits' | 'stats';

interface PhoneSimulatorProps {
  habits: HabitItem[];
  onToggleToday: (id: string) => void;
  onAddHabit: (name: string, icon: HabitIconId, color: HabitColorId, goal: HabitGoal) => void;
  onDeleteHabit: (id: string) => void;
}

const WEEKDAY_SHORT = ['M', 'T', 'W', 'T', 'F', 'S', 'S'];
const WEEKDAY_PLURAL = [
  'Mondays',
  'Tuesdays',
  'Wednesdays',
  'Thursdays',
  'Fridays',
  'Saturdays',
  'Sundays',
];

export function PhoneSimulator({
  habits,
  onToggleToday,
  onAddHabit,
  onDeleteHabit,
}: PhoneSimulatorProps) {
  const [activeTab, setActiveTab] = useState<TabId>('today');
  const [sheetOpen, setSheetOpen] = useState(false);

  // Add Habit sheet form state
  const [newName, setNewName] = useState('');
  const [newIcon, setNewIcon] = useState<HabitIconId>('WATER_DROP');
  const [newColor, setNewColor] = useState<HabitColorId>('TEAL');
  const [newGoal, setNewGoal] = useState<HabitGoal>('EVERY_DAY');

  const today = useMemo(() => new Date(), []);
  const todayIso = useMemo(() => formatIsoDate(today), [today]);

  const formattedTodayHeader = useMemo(() => {
    const weekday = today.toLocaleDateString('en-GB', { weekday: 'long' });
    const day = today.getDate();
    const month = today.toLocaleDateString('en-GB', { month: 'long' });
    return `${weekday}, ${day} ${month}`;
  }, [today]);

  const currentMonday = useMemo(() => getMondayOfWeek(today), [today]);

  const enrichedHabits = useMemo(() => {
    return habits.map((habit) => {
      const doneSet = new Set(habit.completedDates);
      const isDoneToday = doneSet.has(todayIso);
      const dayStreak = calculateDailyStreak(doneSet, today);
      let weekCount = 0;
      for (let i = 0; i < 7; i++) {
        const d = addDays(currentMonday, i);
        if (d > today) break;
        if (doneSet.has(formatIsoDate(d))) weekCount++;
      }
      const weekStreak = calculateWeeklyGoalStreak(
        doneSet,
        GOAL_META[habit.goal].targetPerWeek,
        today
      );
      const consistency = calculateThirtyDayConsistency(doneSet, habit.goal, today);

      const todaySubtitle =
        habit.goal === 'EVERY_DAY'
          ? `${dayStreak}-day streak`
          : `${weekCount} of ${GOAL_META[habit.goal].targetPerWeek} this week`;

      const habitsMetricNumber = habit.goal === 'EVERY_DAY' ? dayStreak : weekStreak;
      const habitsMetricUnit =
        habit.goal === 'EVERY_DAY'
          ? habitsMetricNumber === 1
            ? 'day'
            : 'days'
          : habitsMetricNumber === 1
            ? 'week'
            : 'weeks';

      return {
        habit,
        doneSet,
        isDoneToday,
        dayStreak,
        weekStreak,
        consistency,
        todaySubtitle,
        habitsMetricNumber,
        habitsMetricUnit,
      };
    });
  }, [habits, today, todayIso, currentMonday]);

  const todoHabits = useMemo(
    () => enrichedHabits.filter((h) => !h.isDoneToday),
    [enrichedHabits]
  );
  const doneHabits = useMemo(
    () => enrichedHabits.filter((h) => h.isDoneToday),
    [enrichedHabits]
  );

  const topStreakHabit = useMemo(() => {
    if (enrichedHabits.length === 0) return null;
    return [...enrichedHabits].sort((a, b) => b.dayStreak - a.dayStreak)[0];
  }, [enrichedHabits]);

  const weekDots = useMemo(() => {
    return WEEKDAY_SHORT.map((label, idx) => {
      const d = addDays(currentMonday, idx);
      const iso = formatIsoDate(d);
      const isToday = iso === todayIso;
      const isFuture = d > today && !isToday;
      const anyCompleted =
        !isFuture && enrichedHabits.some((h) => h.doneSet.has(iso));
      return { label, isToday, isFuture, anyCompleted };
    });
  }, [currentMonday, today, todayIso, enrichedHabits]);

  // 20-week x 7-day calendar matrix
  const twentyWeekColumns = useMemo(() => {
    const firstMonday = addDays(currentMonday, -19 * 7);
    const cols: Array<Array<{ iso: string; isToday: boolean } | null>> = [];
    for (let w = 0; w < 20; w++) {
      const col: Array<{ iso: string; isToday: boolean } | null> = [];
      const weekMon = addDays(firstMonday, w * 7);
      for (let d = 0; d < 7; d++) {
        const cellDate = addDays(weekMon, d);
        const iso = formatIsoDate(cellDate);
        if (cellDate > today && iso !== todayIso) {
          col.push(null);
        } else {
          col.push({ iso, isToday: iso === todayIso });
        }
      }
      cols.push(col);
    }
    return cols;
  }, [currentMonday, today, todayIso]);

  const statsSummary = useMemo(() => {
    if (enrichedHabits.length === 0) {
      return {
        onTrack: 0,
        checkIns: 0,
        bestStreak: 0,
        weekdayRatios: [0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2],
        strongestIndex: 6,
        consistentSorted: [],
      };
    }

    const onTrack = Math.round(
      enrichedHabits.reduce((sum, h) => sum + h.consistency, 0) / enrichedHabits.length
    );

    const last30Set = new Set<string>();
    const weekdayCounts = [0, 0, 0, 0, 0, 0, 0];
    for (let i = 0; i < 30; i++) {
      const d = addDays(today, -i);
      const iso = formatIsoDate(d);
      last30Set.add(iso);
      const monIdx = d.getDay() === 0 ? 6 : d.getDay() - 1;
      for (const h of enrichedHabits) {
        if (h.doneSet.has(iso)) {
          weekdayCounts[monIdx]++;
        }
      }
    }

    let checkIns = 0;
    for (const h of enrichedHabits) {
      for (const iso of last30Set) {
        if (h.doneSet.has(iso)) checkIns++;
      }
    }

    const bestStreak = Math.max(...enrichedHabits.map((h) => h.dayStreak));
    const maxWeekday = Math.max(1, ...weekdayCounts);
    const weekdayRatios = weekdayCounts.map((c) => Math.max(0.18, c / maxWeekday));
    let strongestIndex = 0;
    weekdayCounts.forEach((c, idx) => {
      if (c >= weekdayCounts[strongestIndex]) strongestIndex = idx;
    });

    const consistentSorted = [...enrichedHabits].sort(
      (a, b) => b.consistency - a.consistency
    );

    return {
      onTrack,
      checkIns,
      bestStreak,
      weekdayRatios,
      strongestIndex,
      consistentSorted,
    };
  }, [enrichedHabits, today]);

  const completedCount = doneHabits.length;
  const totalCount = enrichedHabits.length;
  const remainingCount = Math.max(0, totalCount - completedCount);
  const progressRatio = totalCount > 0 ? completedCount / totalCount : 0;
  const ringRadius = 38;
  const ringCircumference = 2 * Math.PI * ringRadius;
  const ringOffset = ringCircumference * (1 - progressRatio);

  const handleSubmitNewHabit = () => {
    const trimmed = newName.trim();
    if (!trimmed) return;
    onAddHabit(trimmed, newIcon, newColor, newGoal);
    setNewName('');
    setNewIcon('WATER_DROP');
    setNewColor('TEAL');
    setNewGoal('EVERY_DAY');
    setSheetOpen(false);
  };

  return (
    <div className="relative mx-auto w-full max-w-[388px] rounded-[42px] border-[3px] border-[#262b26] bg-[#0F120E] p-2 shadow-2xl shadow-black/70">
      {/* Inner Android screen frame */}
      <div className="relative flex h-[760px] flex-col overflow-hidden rounded-[34px] bg-[#0F120E] text-[#F3F5F2]">
        {/* Simulated status bar */}
        <div className="flex items-center justify-between px-6 pt-3 pb-1 text-[11px] font-medium text-[#C8CEC7] select-none">
          <span className="tabular-nums">17:59</span>
          <div className="h-3.5 w-3.5 rounded-full bg-[#1b1f1b] border border-[#2d332d]" aria-hidden="true" />
          <span className="tabular-nums">95%</span>
        </div>

        {/* Scrollable screen body */}
        <div className="flex-1 overflow-y-auto px-4 pt-2 pb-5">
          {activeTab === 'today' && (
            <div className="space-y-3">
              {/* Top Header */}
              <div className="flex items-center justify-between pt-1 pb-1">
                <div>
                  <p className="text-[12px] text-[#A2A8A1]">{formattedTodayHeader}</p>
                  <h2 className="text-[28px] font-bold tracking-tight text-[#F3F5F2] leading-tight">
                    Today
                  </h2>
                </div>
                <button
                  type="button"
                  onClick={() => setSheetOpen(true)}
                  aria-label="New habit"
                  className="flex h-11 w-11 items-center justify-center rounded-full bg-[#37653C] text-white transition-transform active:scale-95 hover:bg-[#417647] cursor-pointer"
                >
                  <Plus size={20} strokeWidth={2.2} />
                </button>
              </div>

              {/* Hero Progress Card */}
              <div className="rounded-[22px] bg-[#336339] px-4 py-4">
                <div className="flex items-center gap-4">
                  {/* Circular Progress Ring */}
                  <div className="relative flex h-[92px] w-[92px] shrink-0 items-center justify-center">
                    <svg className="h-[92px] w-[92px] -rotate-90" viewBox="0 0 92 92">
                      <circle
                        cx="46"
                        cy="46"
                        r={ringRadius}
                        fill="none"
                        stroke="#47774D"
                        strokeWidth="8.5"
                      />
                      <circle
                        cx="46"
                        cy="46"
                        r={ringRadius}
                        fill="none"
                        stroke="#9ED5A2"
                        strokeWidth="8.5"
                        strokeLinecap="round"
                        strokeDasharray={ringCircumference}
                        strokeDashoffset={ringOffset}
                        className="transition-all duration-300"
                      />
                    </svg>
                    <div className="absolute inset-0 flex flex-col items-center justify-center">
                      <span className="text-[20px] font-bold leading-none text-white tabular-nums">
                        {completedCount}/{totalCount}
                      </span>
                      <span className="mt-0.5 text-[11px] text-white/80">done</span>
                    </div>
                  </div>

                  {/* Right summary + week dots */}
                  <div className="min-w-0 flex-1">
                    <p className="text-[18px] font-bold leading-tight text-white">
                      {totalCount === 0
                        ? 'No habits'
                        : remainingCount === 0
                          ? 'All done!'
                          : `${remainingCount} to go`}
                    </p>
                    <p className="mt-0.5 truncate text-[12px] text-white/75">
                      {topStreakHabit && topStreakHabit.dayStreak > 0
                        ? `${topStreakHabit.habit.name} · ${topStreakHabit.dayStreak}-day streak`
                        : 'Tap a habit to check in'}
                    </p>

                    <div className="mt-3 flex items-center justify-between gap-1">
                      {weekDots.map((dot, idx) => (
                        <div key={idx} className="flex flex-col items-center gap-1">
                          <span
                            className={`h-4 w-4 rounded-full ${
                              dot.isToday
                                ? `border-[1.5px] border-[#9ED5A2] ${
                                    dot.anyCompleted ? 'bg-[#9ED5A2]/50' : 'bg-transparent'
                                  }`
                                : dot.anyCompleted
                                  ? 'bg-[#9ED5A2]/90'
                                  : 'bg-[#47774D]/70'
                            }`}
                          />
                          <span
                            className={`text-[10px] ${
                              dot.isToday ? 'font-bold text-white' : 'text-white/70'
                            }`}
                          >
                            {dot.label}
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>

              {/* To do section */}
              {todoHabits.length > 0 && (
                <div className="pt-2">
                  <p className="mb-2 text-[13px] font-semibold text-[#F3F5F2] tabular-nums">
                    To do · {todoHabits.length}
                  </p>
                  <div className="space-y-2.5">
                    {todoHabits.map(({ habit, todaySubtitle }) => {
                      const color = HABIT_COLORS[habit.color];
                      return (
                        <button
                          key={habit.id}
                          type="button"
                          onClick={() => onToggleToday(habit.id)}
                          className="flex w-full items-center gap-3 rounded-[16px] bg-[#181B18] px-3.5 py-3 text-left transition-colors hover:bg-[#1E221E] active:scale-[0.99] cursor-pointer"
                        >
                          <div
                            className="flex h-10 w-10 shrink-0 items-center justify-center rounded-[11px]"
                            style={{ backgroundColor: color.bgTint }}
                          >
                            <HabitGlyph icon={habit.icon} color={color.hex} size={18} />
                          </div>
                          <div className="min-w-0 flex-1">
                            <p className="truncate text-[14px] font-semibold text-[#F3F5F2]">
                              {habit.name}
                            </p>
                            <p className="text-[12px] text-[#A2A8A1]">{todaySubtitle}</p>
                          </div>
                          <div
                            className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full border-[1.5px]"
                            style={{ borderColor: `${color.hex}75` }}
                          >
                            <Check size={16} color={`${color.hex}55`} strokeWidth={2.3} />
                          </div>
                        </button>
                      );
                    })}
                  </div>
                </div>
              )}

              {/* Done section */}
              {doneHabits.length > 0 && (
                <div className="pt-2">
                  <p className="mb-2 text-[13px] font-semibold text-[#F3F5F2] tabular-nums">
                    Done · {doneHabits.length}
                  </p>
                  <div className="space-y-2.5">
                    {doneHabits.map(({ habit, todaySubtitle }) => {
                      const color = HABIT_COLORS[habit.color];
                      return (
                        <button
                          key={habit.id}
                          type="button"
                          onClick={() => onToggleToday(habit.id)}
                          className="flex w-full items-center gap-3 rounded-[16px] bg-[#181B18] px-3.5 py-3 text-left transition-colors hover:bg-[#1E221E] active:scale-[0.99] cursor-pointer"
                        >
                          <div
                            className="flex h-10 w-10 shrink-0 items-center justify-center rounded-[11px]"
                            style={{ backgroundColor: color.bgTint }}
                          >
                            <HabitGlyph icon={habit.icon} color={color.hex} size={18} />
                          </div>
                          <div className="min-w-0 flex-1">
                            <p className="truncate text-[14px] font-semibold text-[#F3F5F2]">
                              {habit.name}
                            </p>
                            <p className="text-[12px] text-[#A2A8A1]">{todaySubtitle}</p>
                          </div>
                          <div
                            className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-white"
                            style={{ backgroundColor: color.hex }}
                          >
                            <Check size={16} color="#FFFFFF" strokeWidth={2.6} />
                          </div>
                        </button>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>
          )}

          {activeTab === 'habits' && (
            <div className="space-y-3">
              <div className="flex items-center justify-between pt-1 pb-1">
                <div>
                  <p className="text-[12px] text-[#A2A8A1]">Last 20 weeks</p>
                  <h2 className="text-[28px] font-bold tracking-tight text-[#F3F5F2] leading-tight">
                    Habits
                  </h2>
                </div>
                <button
                  type="button"
                  onClick={() => setSheetOpen(true)}
                  aria-label="New habit"
                  className="flex h-11 w-11 items-center justify-center rounded-full bg-[#37653C] text-white transition-transform active:scale-95 hover:bg-[#417647] cursor-pointer"
                >
                  <Plus size={20} strokeWidth={2.2} />
                </button>
              </div>

              <div className="space-y-3">
                {enrichedHabits.map(
                  ({ habit, doneSet, habitsMetricNumber, habitsMetricUnit }) => {
                    const color = HABIT_COLORS[habit.color];
                    return (
                      <div
                        key={habit.id}
                        className="rounded-[18px] bg-[#181B18] p-3.5"
                      >
                        <div className="flex items-center gap-2.5">
                          <div
                            className="flex h-9 w-9 shrink-0 items-center justify-center rounded-[10px]"
                            style={{ backgroundColor: color.bgTint }}
                          >
                            <HabitGlyph icon={habit.icon} color={color.hex} size={17} />
                          </div>
                          <div className="min-w-0 flex-1">
                            <p className="truncate text-[14px] font-semibold text-[#F3F5F2]">
                              {habit.name}
                            </p>
                            <p className="text-[11px] text-[#A2A8A1]">
                              {GOAL_META[habit.goal].label}
                            </p>
                          </div>
                          <div className="flex items-center gap-2">
                            <span className="text-right tabular-nums">
                              <span className="text-[15px] font-bold text-[#F3F5F2]">
                                {habitsMetricNumber}
                              </span>{' '}
                              <span className="text-[12px] text-[#A2A8A1]">
                                {habitsMetricUnit}
                              </span>
                            </span>
                            <button
                              type="button"
                              onClick={() => onDeleteHabit(habit.id)}
                              title={`Remove ${habit.name}`}
                              className="rounded-md p-1 text-[#A2A8A1]/50 hover:bg-[#252925] hover:text-[#F25F5C] transition-colors cursor-pointer"
                            >
                              <Trash2 size={13} />
                            </button>
                          </div>
                        </div>

                        {/* 20-week x 7-day grid */}
                        <div className="mt-3 grid grid-cols-20 gap-[3px]">
                          {twentyWeekColumns.map((col, wIdx) => (
                            <div key={wIdx} className="flex flex-col gap-[3px]">
                              {col.map((cell, dIdx) => {
                                if (!cell) {
                                  return <div key={dIdx} className="aspect-square w-full" />;
                                }
                                const isDone = doneSet.has(cell.iso);
                                return (
                                  <button
                                    key={dIdx}
                                    type="button"
                                    onClick={() => cell.isToday && onToggleToday(habit.id)}
                                    title={cell.iso}
                                    className={`aspect-square w-full rounded-[3px] transition-colors ${
                                      cell.isToday ? 'cursor-pointer' : 'cursor-default'
                                    }`}
                                    style={{
                                      backgroundColor: isDone ? color.hex : '#252925',
                                      outline: cell.isToday ? `1.5px solid ${color.hex}` : 'none',
                                      outlineOffset: '-1px',
                                    }}
                                  />
                                );
                              })}
                            </div>
                          ))}
                        </div>
                      </div>
                    );
                  }
                )}
              </div>
            </div>
          )}

          {activeTab === 'stats' && (
            <div className="space-y-3">
              <div className="pt-1 pb-1">
                <p className="text-[12px] text-[#A2A8A1]">Last 30 days</p>
                <h2 className="text-[28px] font-bold tracking-tight text-[#F3F5F2] leading-tight">
                  Stats
                </h2>
              </div>

              {/* 3 KPI cards */}
              <div className="grid grid-cols-3 gap-2.5">
                <div className="rounded-[16px] bg-[#181B18] px-3 py-3.5">
                  <p className="text-[22px] font-bold text-[#F3F5F2] tabular-nums">
                    {statsSummary.onTrack}%
                  </p>
                  <p className="mt-0.5 text-[11px] text-[#A2A8A1]">On track</p>
                </div>
                <div className="rounded-[16px] bg-[#181B18] px-3 py-3.5">
                  <p className="text-[22px] font-bold text-[#F3F5F2] tabular-nums">
                    {statsSummary.checkIns}
                  </p>
                  <p className="mt-0.5 text-[11px] text-[#A2A8A1]">Check-ins</p>
                </div>
                <div className="rounded-[16px] bg-[#181B18] px-3 py-3.5">
                  <p className="text-[22px] font-bold text-[#F3F5F2] tabular-nums">
                    {statsSummary.bestStreak}
                  </p>
                  <p className="mt-0.5 text-[11px] text-[#A2A8A1]">Best streak</p>
                </div>
              </div>

              {/* All habits multi-shade heatmap */}
              <div className="rounded-[18px] bg-[#181B18] p-4">
                <p className="text-[14px] font-semibold text-[#F3F5F2]">All habits</p>
                <p className="text-[11px] text-[#A2A8A1]">Share of habits done each day</p>

                <div className="mt-3 grid grid-cols-20 gap-[3px]">
                  {twentyWeekColumns.map((col, wIdx) => (
                    <div key={wIdx} className="flex flex-col gap-[3px]">
                      {col.map((cell, dIdx) => {
                        if (!cell) {
                          return <div key={dIdx} className="aspect-square w-full" />;
                        }
                        const count = enrichedHabits.filter((h) => h.doneSet.has(cell.iso)).length;
                        const share = enrichedHabits.length > 0 ? count / enrichedHabits.length : 0;
                        let bg = '#252925';
                        if (share > 0 && share < 0.35) bg = '#39593C';
                        else if (share >= 0.35 && share < 0.65) bg = '#538357';
                        else if (share >= 0.65 && share < 0.9) bg = '#77B27C';
                        else if (share >= 0.9) bg = '#9ED5A2';

                        return (
                          <div
                            key={dIdx}
                            className="aspect-square w-full rounded-[3px]"
                            style={{
                              backgroundColor: bg,
                              outline: cell.isToday ? '1.5px solid #9ED5A2' : 'none',
                              outlineOffset: '-1px',
                            }}
                          />
                        );
                      })}
                    </div>
                  ))}
                </div>
              </div>

              {/* By weekday */}
              <div className="rounded-[18px] bg-[#181B18] p-4">
                <p className="text-[14px] font-semibold text-[#F3F5F2]">By weekday</p>
                <p className="text-[11px] text-[#A2A8A1]">
                  Strongest on {WEEKDAY_PLURAL[statsSummary.strongestIndex]}
                </p>

                <div className="mt-4 grid h-[112px] grid-cols-7 items-end gap-2.5 px-1">
                  {statsSummary.weekdayRatios.map((ratio, idx) => {
                    const isStrongest = idx === statsSummary.strongestIndex;
                    return (
                      <div key={idx} className="flex h-full flex-col items-center justify-end gap-1.5">
                        <div className="flex w-full flex-1 items-end justify-center">
                          <div
                            className="w-5 rounded-[7px] transition-all duration-300"
                            style={{
                              height: `${Math.round(ratio * 100)}%`,
                              backgroundColor: isStrongest ? '#9ED5A2' : '#425944',
                            }}
                          />
                        </div>
                        <span className="text-[11px] text-[#A2A8A1]">{WEEKDAY_SHORT[idx]}</span>
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Most consistent */}
              <div className="rounded-[18px] bg-[#181B18] p-4">
                <p className="mb-3 text-[14px] font-semibold text-[#F3F5F2]">Most consistent</p>
                <div className="space-y-3">
                  {statsSummary.consistentSorted.map(({ habit, consistency }) => (
                    <div key={habit.id}>
                      <div className="flex items-center justify-between text-[13px]">
                        <span className="truncate text-[#F3F5F2]">{habit.name}</span>
                        <span className="ml-2 text-[#A2A8A1] tabular-nums">{consistency}%</span>
                      </div>
                      <div className="mt-1.5 h-1.5 w-full overflow-hidden rounded-full bg-[#252925]">
                        <div
                          className="h-full rounded-full transition-all duration-300"
                          style={{
                            width: `${consistency}%`,
                            backgroundColor: HABIT_COLORS[habit.color].hex,
                          }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Bottom Navigation Bar */}
        <div className="grid grid-cols-3 bg-[#131613] px-2 pt-2 pb-3 border-t border-white/[0.04]">
          {(
            [
              { id: 'today', label: 'Today', Icon: Home },
              { id: 'habits', label: 'Habits', Icon: LayoutGrid },
              { id: 'stats', label: 'Stats', Icon: BarChart2 },
            ] as const
          ).map(({ id, label, Icon }) => {
            const selected = activeTab === id;
            return (
              <button
                key={id}
                type="button"
                onClick={() => setActiveTab(id)}
                className="flex flex-col items-center justify-center gap-1 py-1 cursor-pointer"
              >
                <span
                  className={`flex h-7 w-14 items-center justify-center rounded-full transition-colors ${
                    selected ? 'bg-[#334434] text-[#F3F5F2]' : 'text-[#A2A8A1]'
                  }`}
                >
                  <Icon size={17} strokeWidth={selected ? 2.2 : 1.9} />
                </span>
                <span
                  className={`text-[11px] ${
                    selected ? 'font-semibold text-[#F3F5F2]' : 'text-[#A2A8A1]'
                  }`}
                >
                  {label}
                </span>
              </button>
            );
          })}
        </div>

        {/* Gesture pill */}
        <div className="flex justify-center bg-[#131613] pb-2">
          <div className="h-1 w-24 rounded-full bg-[#424742]" />
        </div>

        {/* Modal Bottom Sheet: New habit */}
        {sheetOpen && (
          <div className="absolute inset-0 z-30 flex flex-col justify-end bg-black/65">
            <div className="rounded-t-[26px] bg-[#181B18] px-5 pt-5 pb-6 shadow-2xl">
              <div className="mb-4 flex items-center justify-between">
                <h3 className="text-[20px] font-bold text-[#F3F5F2]">New habit</h3>
                <button
                  type="button"
                  onClick={() => setSheetOpen(false)}
                  aria-label="Close"
                  className="rounded-full p-1 text-[#A2A8A1] hover:bg-[#252925] hover:text-white cursor-pointer"
                >
                  <X size={18} />
                </button>
              </div>

              <input
                type="text"
                value={newName}
                onChange={(e) => setNewName(e.target.value)}
                placeholder="Name"
                className="w-full rounded-[8px] border border-[#5a605a] bg-transparent px-3.5 py-2.5 text-[14px] text-[#F3F5F2] placeholder:text-[#A2A8A1] focus:border-[#25B8A8] focus:outline-none"
              />

              <p className="mt-4 mb-2 text-[12px] font-medium text-[#A2A8A1]">Icon</p>
              <div className="grid grid-cols-6 gap-2">
                {HABIT_ICON_ORDER.map((iconId) => {
                  const selected = iconId === newIcon;
                  const activeHex = HABIT_COLORS[newColor].hex;
                  return (
                    <button
                      key={iconId}
                      type="button"
                      onClick={() => setNewIcon(iconId)}
                      className="flex h-10 items-center justify-center rounded-[10px] transition-colors cursor-pointer"
                      style={{
                        backgroundColor: selected ? `${activeHex}24` : '#222622',
                        border: selected ? `1.5px solid ${activeHex}` : '1.5px solid transparent',
                      }}
                    >
                      <HabitGlyph
                        icon={iconId}
                        color={selected ? '#FFFFFF' : '#E3E6E2'}
                        size={17}
                      />
                    </button>
                  );
                })}
              </div>

              <p className="mt-4 mb-2.5 text-[12px] font-medium text-[#A2A8A1]">Colour</p>
              <div className="flex items-center justify-between">
                {HABIT_COLOR_ORDER.map((colorId) => {
                  const selected = colorId === newColor;
                  const hex = HABIT_COLORS[colorId].hex;
                  return (
                    <button
                      key={colorId}
                      type="button"
                      onClick={() => setNewColor(colorId)}
                      aria-label={colorId}
                      className="flex h-8 w-8 items-center justify-center rounded-full cursor-pointer"
                      style={{
                        border: selected ? '2px solid #FFFFFF' : '2px solid transparent',
                      }}
                    >
                      <span
                        className="rounded-full"
                        style={{
                          width: selected ? 22 : 26,
                          height: selected ? 22 : 26,
                          backgroundColor: hex,
                        }}
                      />
                    </button>
                  );
                })}
              </div>

              <p className="mt-4 mb-2 text-[12px] font-medium text-[#A2A8A1]">Goal</p>
              <div className="grid grid-cols-3 gap-2">
                {(['EVERY_DAY', 'FIVE_DAYS_A_WEEK', 'THREE_DAYS_A_WEEK'] as HabitGoal[]).map(
                  (goalId) => {
                    const selected = goalId === newGoal;
                    return (
                      <button
                        key={goalId}
                        type="button"
                        onClick={() => setNewGoal(goalId)}
                        className={`rounded-[9px] px-2 py-2 text-[11px] font-medium whitespace-nowrap transition-colors cursor-pointer ${
                          selected
                            ? 'bg-[#283D2A] text-[#9ED5A2]'
                            : 'border border-[#323732] text-[#A2A8A1] hover:text-[#F3F5F2]'
                        }`}
                      >
                        {GOAL_META[goalId].label}
                      </button>
                    );
                  }
                )}
              </div>

              <button
                type="button"
                onClick={handleSubmitNewHabit}
                disabled={!newName.trim()}
                className="mt-5 w-full rounded-full bg-[#A6D9A8] py-3 text-[14px] font-semibold text-[#0E2912] transition-opacity hover:opacity-95 disabled:opacity-50 cursor-pointer"
              >
                Add habit
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
