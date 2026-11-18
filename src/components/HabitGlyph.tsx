import {
  Bookmark,
  Dumbbell,
  Flame,
  Heart,
  Leaf,
  Moon,
  Music,
  Pencil,
  Star,
  Sun,
  Zap,
  Droplet,
  Footprints,
} from 'lucide-react';
import { HabitIconId } from '../data/habitSeed';

interface HabitGlyphProps {
  icon: HabitIconId;
  color?: string;
  size?: number;
}

export function HabitGlyph({ icon, color = 'currentColor', size = 20 }: HabitGlyphProps) {
  switch (icon) {
    case 'WATER_DROP':
      return <Droplet size={size} color={color} fill={color} strokeWidth={1.8} />;
    case 'BOOK':
      return <Bookmark size={size} color={color} fill={color} strokeWidth={1.8} />;
    case 'MOON':
      return <Moon size={size} color={color} fill={color} strokeWidth={1.8} />;
    case 'RUN':
      return <Footprints size={size} color={color} strokeWidth={2} />;
    case 'LEAF':
      return <Leaf size={size} color={color} fill={color} strokeWidth={1.8} />;
    case 'DUMBBELL':
      return <Dumbbell size={size} color={color} strokeWidth={2.2} />;
    case 'EDIT':
      return <Pencil size={size} color={color} strokeWidth={2} />;
    case 'MUSIC':
      return <Music size={size} color={color} strokeWidth={2.2} />;
    case 'HEART':
      return <Heart size={size} color={color} fill={color} strokeWidth={1.8} />;
    case 'SUN':
      return <Sun size={size} color={color} strokeWidth={2} />;
    case 'BOLT':
      return <Zap size={size} color={color} fill={color} strokeWidth={1.8} />;
    case 'STAR':
      return <Star size={size} color={color} fill={color} strokeWidth={1.8} />;
    default:
      return <Flame size={size} color={color} />;
  }
}
