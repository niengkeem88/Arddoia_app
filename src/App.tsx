import { useState } from 'react';
import {
  buildInitialHabits,
  formatIsoDate,
  HabitColorId,
  HabitGoal,
  HabitIconId,
  HabitItem,
} from './data/habitSeed';
import { PhoneSimulator } from './components/PhoneSimulator';
import { KotlinSourceExplorer } from './components/KotlinSourceExplorer';

type MobilePane = 'preview' | 'source';

export default function App() {
  const [habits, setHabits] = useState<HabitItem[]>(() => buildInitialHabits(new Date()));
  const [mobilePane, setMobilePane] = useState<MobilePane>('preview');

  const handleToggleToday = (habitId: string) => {
    const todayIso = formatIsoDate(new Date());
    setHabits((prev) =>
      prev.map((h) => {
        if (h.id !== habitId) return h;
        const exists = h.completedDates.includes(todayIso);
        return {
          ...h,
          completedDates: exists
            ? h.completedDates.filter((d) => d !== todayIso)
            : [...h.completedDates, todayIso],
        };
      })
    );
  };

  const handleAddHabit = (
    name: string,
    icon: HabitIconId,
    color: HabitColorId,
    goal: HabitGoal
  ) => {
    const newHabit: HabitItem = {
      id: `habit_${Date.now()}`,
      name,
      icon,
      color,
      goal,
      completedDates: [],
    };
    setHabits((prev) => [...prev, newHabit]);
  };

  const handleDeleteHabit = (habitId: string) => {
    setHabits((prev) => prev.filter((h) => h.id !== habitId));
  };

  const handleResetDemo = () => {
    setHabits(buildInitialHabits(new Date()));
  };

  return (
    <div className="min-h-screen bg-[#090C09] text-[#F3F5F2]">
      {/* 3-zone Top Bar Contract */}
      <header className="border-b border-[#1E231E] bg-[#0B0E0B]">
        <div className="mx-auto flex max-w-7xl items-center justify-between gap-8 px-6 py-4">
          <a
            href="#top"
            className="text-lg font-bold tracking-tight text-[#F3F5F2] whitespace-nowrap shrink-0"
          >
            Arddoia
          </a>

          <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-[#A2A8A1]">
            <a
              href="#preview"
              className="hover:text-[#F3F5F2] transition-colors whitespace-nowrap shrink-0"
            >
              Interactive Preview
            </a>
            <a
              href="#source"
              className="hover:text-[#F3F5F2] transition-colors whitespace-nowrap shrink-0"
            >
              Kotlin Source
            </a>
            <a
              href="#specs"
              className="hover:text-[#F3F5F2] transition-colors whitespace-nowrap shrink-0"
            >
              SDK & Package
            </a>
          </nav>

          <div className="flex items-center gap-3 shrink-0">
            <button
              type="button"
              onClick={handleResetDemo}
              className="rounded-lg bg-[#336339] px-4 py-2 text-xs font-medium text-white transition-colors hover:bg-[#3e7745] whitespace-nowrap shrink-0 cursor-pointer"
            >
              Reset Demo Data
            </button>
          </div>
        </div>
      </header>

      <main id="top" className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:py-10">
        {/* Intro summary with unboxed metadata */}
        <section id="specs" className="mb-8 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <h1 className="text-2xl font-bold tracking-tight text-[#F3F5F2] sm:text-3xl text-balance">
              Arddoia Kotlin & Jetpack Compose Project
            </h1>
            <p className="mt-1.5 max-w-2xl text-sm text-[#A2A8A1]">
              Full clean-room Android implementation with Room persistence, Material 3 dark theme,
              20-week heatmaps, and 30-day statistics.
            </p>
          </div>
          <div className="flex flex-wrap items-center gap-2 text-xs text-[#A2A8A1] tabular-nums">
            <span className="font-mono text-[#9ED5A2]">com.ar.arddoia</span>
            <span aria-hidden="true">·</span>
            <span>compileSdk 35</span>
            <span aria-hidden="true">·</span>
            <span>targetSdk 35</span>
            <span aria-hidden="true">·</span>
            <span>Kotlin 2.0.21</span>
            <span aria-hidden="true">·</span>
            <span>Jetpack Compose</span>
          </div>
        </section>

        {/* Mobile pane switcher */}
        <div className="mb-5 flex items-center gap-1 rounded-xl bg-[#141814] p-1 lg:hidden">
          <button
            type="button"
            onClick={() => setMobilePane('preview')}
            className={`flex-1 rounded-lg py-2 text-xs font-medium transition-colors cursor-pointer ${
              mobilePane === 'preview'
                ? 'bg-[#283D2A] text-[#9ED5A2]'
                : 'text-[#A2A8A1] hover:text-white'
            }`}
          >
            Interactive Preview
          </button>
          <button
            type="button"
            onClick={() => setMobilePane('source')}
            className={`flex-1 rounded-lg py-2 text-xs font-medium transition-colors cursor-pointer ${
              mobilePane === 'source'
                ? 'bg-[#283D2A] text-[#9ED5A2]'
                : 'text-[#A2A8A1] hover:text-white'
            }`}
          >
            Kotlin Source Files
          </button>
        </div>

        {/* 2-Column Desktop Studio */}
        <div className="grid grid-cols-1 items-start gap-8 lg:grid-cols-[400px_1fr]">
          <section
            id="preview"
            className={mobilePane === 'preview' ? 'block' : 'hidden lg:block'}
          >
            <PhoneSimulator
              habits={habits}
              onToggleToday={handleToggleToday}
              onAddHabit={handleAddHabit}
              onDeleteHabit={handleDeleteHabit}
            />
          </section>

          <section
            id="source"
            className={mobilePane === 'source' ? 'block' : 'hidden lg:block'}
          >
            <KotlinSourceExplorer />
          </section>
        </div>
      </main>
    </div>
  );
}
