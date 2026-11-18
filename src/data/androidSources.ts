import settingsGradle from '../../android/settings.gradle.kts?raw';
import rootBuildGradle from '../../android/build.gradle.kts?raw';
import gradleProperties from '../../android/gradle.properties?raw';
import libsVersions from '../../android/gradle/libs.versions.toml?raw';
import appBuildGradle from '../../android/app/build.gradle.kts?raw';
import manifestXml from '../../android/app/src/main/AndroidManifest.xml?raw';
import stringsXml from '../../android/app/src/main/res/values/strings.xml?raw';
import themesXml from '../../android/app/src/main/res/values/themes.xml?raw';
import arddoiaAppKt from '../../android/app/src/main/java/com/ar/arddoia/ArddoiaApp.kt?raw';
import mainActivityKt from '../../android/app/src/main/java/com/ar/arddoia/MainActivity.kt?raw';
import habitModelsKt from '../../android/app/src/main/java/com/ar/arddoia/data/model/HabitModels.kt?raw';
import arddoiaDbKt from '../../android/app/src/main/java/com/ar/arddoia/data/local/ArddoiaDatabase.kt?raw';
import habitRepoKt from '../../android/app/src/main/java/com/ar/arddoia/data/repository/HabitRepository.kt?raw';
import themeKt from '../../android/app/src/main/java/com/ar/arddoia/ui/theme/Theme.kt?raw';
import viewModelKt from '../../android/app/src/main/java/com/ar/arddoia/ui/viewmodel/ArddoiaViewModel.kt?raw';
import iconMapperKt from '../../android/app/src/main/java/com/ar/arddoia/ui/components/HabitIconMapper.kt?raw';
import addSheetKt from '../../android/app/src/main/java/com/ar/arddoia/ui/components/AddHabitBottomSheet.kt?raw';
import todayScreenKt from '../../android/app/src/main/java/com/ar/arddoia/ui/screens/TodayScreen.kt?raw';
import habitsScreenKt from '../../android/app/src/main/java/com/ar/arddoia/ui/screens/HabitsScreen.kt?raw';
import statsScreenKt from '../../android/app/src/main/java/com/ar/arddoia/ui/screens/StatsScreen.kt?raw';
import arddoiaRootKt from '../../android/app/src/main/java/com/ar/arddoia/ui/ArddoiaRoot.kt?raw';

export interface AndroidSourceFile {
  path: string;
  name: string;
  group: 'App & UI' | 'Data & Room' | 'Theme & ViewModel' | 'Gradle & Manifest';
  language: 'kotlin' | 'toml' | 'xml';
  content: string;
}

export const ANDROID_SOURCE_FILES: AndroidSourceFile[] = [
  {
    path: 'app/src/main/java/com/ar/arddoia/MainActivity.kt',
    name: 'MainActivity.kt',
    group: 'App & UI',
    language: 'kotlin',
    content: mainActivityKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/ArddoiaApp.kt',
    name: 'ArddoiaApp.kt',
    group: 'App & UI',
    language: 'kotlin',
    content: arddoiaAppKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/ui/ArddoiaRoot.kt',
    name: 'ArddoiaRoot.kt',
    group: 'App & UI',
    language: 'kotlin',
    content: arddoiaRootKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/ui/screens/TodayScreen.kt',
    name: 'TodayScreen.kt',
    group: 'App & UI',
    language: 'kotlin',
    content: todayScreenKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/ui/screens/HabitsScreen.kt',
    name: 'HabitsScreen.kt',
    group: 'App & UI',
    language: 'kotlin',
    content: habitsScreenKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/ui/screens/StatsScreen.kt',
    name: 'StatsScreen.kt',
    group: 'App & UI',
    language: 'kotlin',
    content: statsScreenKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/ui/components/AddHabitBottomSheet.kt',
    name: 'AddHabitBottomSheet.kt',
    group: 'App & UI',
    language: 'kotlin',
    content: addSheetKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/ui/components/HabitIconMapper.kt',
    name: 'HabitIconMapper.kt',
    group: 'App & UI',
    language: 'kotlin',
    content: iconMapperKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/ui/viewmodel/ArddoiaViewModel.kt',
    name: 'ArddoiaViewModel.kt',
    group: 'Theme & ViewModel',
    language: 'kotlin',
    content: viewModelKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/ui/theme/Theme.kt',
    name: 'Theme.kt',
    group: 'Theme & ViewModel',
    language: 'kotlin',
    content: themeKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/data/model/HabitModels.kt',
    name: 'HabitModels.kt',
    group: 'Data & Room',
    language: 'kotlin',
    content: habitModelsKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/data/local/ArddoiaDatabase.kt',
    name: 'ArddoiaDatabase.kt',
    group: 'Data & Room',
    language: 'kotlin',
    content: arddoiaDbKt,
  },
  {
    path: 'app/src/main/java/com/ar/arddoia/data/repository/HabitRepository.kt',
    name: 'HabitRepository.kt',
    group: 'Data & Room',
    language: 'kotlin',
    content: habitRepoKt,
  },
  {
    path: 'app/build.gradle.kts',
    name: 'app/build.gradle.kts',
    group: 'Gradle & Manifest',
    language: 'kotlin',
    content: appBuildGradle,
  },
  {
    path: 'gradle/libs.versions.toml',
    name: 'libs.versions.toml',
    group: 'Gradle & Manifest',
    language: 'toml',
    content: libsVersions,
  },
  {
    path: 'app/src/main/AndroidManifest.xml',
    name: 'AndroidManifest.xml',
    group: 'Gradle & Manifest',
    language: 'xml',
    content: manifestXml,
  },
  {
    path: 'settings.gradle.kts',
    name: 'settings.gradle.kts',
    group: 'Gradle & Manifest',
    language: 'kotlin',
    content: settingsGradle,
  },
  {
    path: 'build.gradle.kts',
    name: 'build.gradle.kts',
    group: 'Gradle & Manifest',
    language: 'kotlin',
    content: rootBuildGradle,
  },
  {
    path: 'gradle.properties',
    name: 'gradle.properties',
    group: 'Gradle & Manifest',
    language: 'toml',
    content: gradleProperties,
  },
  {
    path: 'app/src/main/res/values/strings.xml',
    name: 'strings.xml',
    group: 'Gradle & Manifest',
    language: 'xml',
    content: stringsXml,
  },
  {
    path: 'app/src/main/res/values/themes.xml',
    name: 'themes.xml',
    group: 'Gradle & Manifest',
    language: 'xml',
    content: themesXml,
  },
];
