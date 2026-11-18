import { useMemo, useState } from 'react';
import { Check, Copy, Download, FileCode2, FolderOpen } from 'lucide-react';
import { ANDROID_SOURCE_FILES, AndroidSourceFile } from '../data/androidSources';

const GROUPS: AndroidSourceFile['group'][] = [
  'App & UI',
  'Theme & ViewModel',
  'Data & Room',
  'Gradle & Manifest',
];

export function KotlinSourceExplorer() {
  const [selectedPath, setSelectedPath] = useState<string>(ANDROID_SOURCE_FILES[0].path);
  const [copied, setCopied] = useState(false);

  const selectedFile = useMemo(
    () =>
      ANDROID_SOURCE_FILES.find((f) => f.path === selectedPath) ?? ANDROID_SOURCE_FILES[0],
    [selectedPath]
  );

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(selectedFile.content);
      setCopied(true);
      window.setTimeout(() => setCopied(false), 1800);
    } catch {
      // Fallback ignored if clipboard permissions are restricted
    }
  };

  const handleDownloadFile = () => {
    const blob = new Blob([selectedFile.content], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = selectedFile.name.split('/').pop() ?? 'Source.kt';
    a.click();
    URL.revokeObjectURL(url);
  };

  const lines = useMemo(() => selectedFile.content.split('\n'), [selectedFile.content]);

  return (
    <div className="flex h-[760px] flex-col overflow-hidden rounded-[24px] border border-[#232823] bg-[#111411]">
      {/* File header */}
      <div className="flex flex-wrap items-center justify-between gap-3 border-b border-[#232823] bg-[#151915] px-5 py-3.5">
        <div className="min-w-0">
          <div className="flex items-center gap-2 text-sm font-semibold text-[#F3F5F2]">
            <FileCode2 size={16} className="shrink-0 text-[#9ED5A2]" />
            <span className="truncate font-mono">{selectedFile.path}</span>
          </div>
          <p className="mt-0.5 text-xs text-[#A2A8A1]">
            Package com.ar.arddoia · {lines.length} lines · Android API 35 (Kotlin 2.0 + Compose)
          </p>
        </div>

        <div className="flex items-center gap-2 shrink-0">
          <button
            type="button"
            onClick={handleCopy}
            className="flex items-center gap-1.5 rounded-lg border border-[#2d332d] bg-[#1a1f1a] px-3 py-1.5 text-xs font-medium text-[#F3F5F2] transition-colors hover:bg-[#232923] whitespace-nowrap cursor-pointer"
          >
            {copied ? <Check size={14} className="text-[#9ED5A2]" /> : <Copy size={14} />}
            <span>{copied ? 'Copied' : 'Copy file'}</span>
          </button>
          <button
            type="button"
            onClick={handleDownloadFile}
            className="flex items-center gap-1.5 rounded-lg bg-[#336339] px-3 py-1.5 text-xs font-medium text-white transition-colors hover:bg-[#3e7745] whitespace-nowrap cursor-pointer"
          >
            <Download size={14} />
            <span>Download</span>
          </button>
        </div>
      </div>

      {/* Split sidebar + code pane */}
      <div className="grid min-h-0 flex-1 grid-cols-1 md:grid-cols-[248px_1fr]">
        {/* File Tree */}
        <aside className="overflow-y-auto border-b border-[#232823] bg-[#0e110e] p-3 md:border-r md:border-b-0">
          {GROUPS.map((group) => {
            const files = ANDROID_SOURCE_FILES.filter((f) => f.group === group);
            return (
              <div key={group} className="mb-4 last:mb-0">
                <div className="mb-1.5 flex items-center gap-1.5 px-2 text-[11px] font-semibold text-[#A2A8A1]">
                  <FolderOpen size={12} />
                  <span>{group}</span>
                </div>
                <div className="space-y-0.5">
                  {files.map((file) => {
                    const active = file.path === selectedFile.path;
                    return (
                      <button
                        key={file.path}
                        type="button"
                        onClick={() => setSelectedPath(file.path)}
                        className={`flex w-full items-center justify-between rounded-lg px-2.5 py-1.5 text-left text-xs transition-colors cursor-pointer ${
                          active
                            ? 'bg-[#283D2A] font-medium text-[#9ED5A2]'
                            : 'text-[#C6CBC5] hover:bg-[#181B18] hover:text-white'
                        }`}
                      >
                        <span className="truncate font-mono">{file.name}</span>
                      </button>
                    );
                  })}
                </div>
              </div>
            );
          })}
        </aside>

        {/* Code Viewport */}
        <div className="overflow-auto bg-[#0c0e0c] p-4 font-mono text-[12.5px] leading-[1.6]">
          <table className="w-full border-collapse">
            <tbody>
              {lines.map((line, idx) => (
                <tr key={idx} className="hover:bg-white/[0.03]">
                  <td className="w-10 pr-4 text-right text-[11px] text-[#5c635c] select-none tabular-nums align-top">
                    {idx + 1}
                  </td>
                  <td className="whitespace-pre text-[#E6EAE5]">{line || ' '}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
