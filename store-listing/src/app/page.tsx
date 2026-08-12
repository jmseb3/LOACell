"use client";

import { toPng } from "html-to-image";
import React, { useEffect, useMemo, useRef, useState } from "react";

type Device = "iphone" | "android" | "feature-graphic";
type Size = { label: string; w: number; h: number };
type FrameProps = { src: string; alt: string; style?: React.CSSProperties };
type FrameComponent = (props: FrameProps) => React.JSX.Element;
type SlideProps = { cW: number; cH: number };
type SlideDef = { id: string; render: (props: SlideProps) => React.JSX.Element };

const IPHONE_SIZES: readonly Size[] = [
  { label: '6.9"', w: 1320, h: 2868 },
  { label: '6.5"', w: 1284, h: 2778 },
  { label: '6.3"', w: 1206, h: 2622 },
  { label: '6.1"', w: 1125, h: 2436 },
];
const ANDROID_SIZES: readonly Size[] = [{ label: "Phone", w: 1080, h: 1920 }];
const FEATURE_SIZES: readonly Size[] = [{ label: "Feature Graphic", w: 1024, h: 500 }];

const THEME = {
  primary: "#415F91",
  primaryDeep: "#243E69",
  primaryContainer: "#D6E3FF",
  onPrimaryContainer: "#001B3E",
  surface: "#F8F9FF",
  surfaceBlue: "#EEF3FF",
  ink: "#151A23",
  muted: "#5E6675",
  white: "#FFFFFF",
} as const;

const IMAGE_PATHS = [
  "/mockup.png",
  "/app-icon.png",
  ...["01_home", "02_room", "03_room_user", "04_room_party", "05_party_1", "06_calendar"].flatMap(
    (name) => [
      `/screenshots/apple/iphone/ko/${name}.png`,
      `/screenshots/android/phone/ko/${name}.png`,
    ],
  ),
];

const imageCache: Record<string, string> = {};

function img(path: string) {
  return imageCache[path] || path;
}

async function preloadAllImages() {
  await Promise.all(
    IMAGE_PATHS.map(async (path) => {
      const response = await fetch(path);
      if (!response.ok) throw new Error(`이미지를 불러오지 못했습니다: ${path}`);
      const blob = await response.blob();
      imageCache[path] = await new Promise<string>((resolve, reject) => {
        const reader = new FileReader();
        reader.onloadend = () => resolve(reader.result as string);
        reader.onerror = () => reject(reader.error);
        reader.readAsDataURL(blob);
      });
    }),
  );
}

const MK_W = 1022;
const MK_H = 2082;
const SC_L = (52 / MK_W) * 100;
const SC_T = (46 / MK_H) * 100;
const SC_W = (918 / MK_W) * 100;
const SC_H = (1990 / MK_H) * 100;
const SC_RX = (126 / 918) * 100;
const SC_RY = (126 / 1990) * 100;
const MK_RATIO = MK_W / MK_H;

function phoneW(cW: number, cH: number, clamp = 0.82) {
  return Math.min(clamp, 0.72 * (cH / cW) * MK_RATIO);
}

function IPhone({ src, alt, style }: FrameProps) {
  return (
    <div style={{ position: "relative", aspectRatio: `${MK_W}/${MK_H}`, ...style }}>
      <div
        style={{
          position: "absolute",
          zIndex: 10,
          overflow: "hidden",
          left: `${SC_L}%`,
          top: `${SC_T}%`,
          width: `${SC_W}%`,
          height: `${SC_H}%`,
          borderRadius: `${SC_RX}% / ${SC_RY}%`,
          background: THEME.surface,
        }}
      >
        <img
          src={src}
          alt={alt}
          style={{ display: "block", width: "100%", height: "100%", objectFit: "cover", objectPosition: "top" }}
          draggable={false}
        />
      </div>
      <img
        src={img("/mockup.png")}
        alt=""
        style={{ display: "block", position: "relative", zIndex: 2, width: "100%", height: "100%" }}
        draggable={false}
      />
    </div>
  );
}

function AndroidPhone({ src, alt, style }: FrameProps) {
  return (
    <div style={{ position: "relative", aspectRatio: "9/19.5", ...style }}>
      <div
        style={{
          width: "100%",
          height: "100%",
          borderRadius: "8% / 4%",
          background: "linear-gradient(160deg, #31343A 0%, #111216 100%)",
          boxShadow: "inset 0 0 0 1px rgba(255,255,255,.12), 0 14px 54px rgba(8,18,38,.36)",
          position: "relative",
          overflow: "hidden",
        }}
      >
        <div
          style={{
            position: "absolute",
            top: "1.45%",
            left: "50%",
            transform: "translateX(-50%)",
            width: "3%",
            height: "1.38%",
            borderRadius: "50%",
            background: "#08090B",
            zIndex: 20,
          }}
        />
        <div
          style={{
            position: "absolute",
            left: "3.5%",
            top: "2%",
            width: "93%",
            height: "96%",
            borderRadius: "5.5% / 2.6%",
            overflow: "hidden",
            background: "#000",
          }}
        >
          <img
            src={src}
            alt={alt}
            style={{ display: "block", width: "100%", height: "100%", objectFit: "cover", objectPosition: "top" }}
            draggable={false}
          />
        </div>
      </div>
    </div>
  );
}

function Glow({ cW, color = "rgba(112,151,222,.35)", style }: { cW: number; color?: string; style?: React.CSSProperties }) {
  return (
    <div
      style={{
        position: "absolute",
        width: cW * 0.72,
        height: cW * 0.72,
        borderRadius: "50%",
        background: color,
        filter: `blur(${cW * 0.08}px)`,
        ...style,
      }}
    />
  );
}

function Caption({ cW, label, headline, inverse = false, align = "left", style }: {
  cW: number;
  label: string;
  headline: React.ReactNode;
  inverse?: boolean;
  align?: "left" | "center" | "right";
  style?: React.CSSProperties;
}) {
  const color = inverse ? THEME.white : THEME.ink;
  return (
    <div style={{ position: "absolute", zIndex: 12, textAlign: align, ...style }}>
      <div
        style={{
          color: inverse ? "rgba(255,255,255,.68)" : THEME.primary,
          fontSize: cW * 0.027,
          fontWeight: 700,
          letterSpacing: "0.14em",
          marginBottom: cW * 0.035,
        }}
      >
        {label}
      </div>
      <div
        style={{
          color,
          fontSize: cW * 0.087,
          fontWeight: 800,
          lineHeight: 1.08,
          letterSpacing: "-0.045em",
          wordBreak: "keep-all",
        }}
      >
        {headline}
      </div>
    </div>
  );
}

function makeSlides(Frame: FrameComponent, basePath: string): SlideDef[] {
  const screen = (name: string) => img(`/${basePath}/${name}.png`);
  return [
    {
      id: "hero",
      render: ({ cW, cH }) => {
        const width = phoneW(cW, cH) * 100;
        return (
          <div style={{ width: "100%", height: "100%", position: "relative", overflow: "hidden", background: `linear-gradient(165deg, ${THEME.surface} 0%, ${THEME.surfaceBlue} 58%, ${THEME.primaryContainer} 100%)` }}>
            <Glow cW={cW} style={{ top: -cW * 0.2, right: -cW * 0.18 }} />
            <img src={img("/app-icon.png")} alt="LoaCell" style={{ position: "absolute", zIndex: 12, top: cW * 0.08, left: cW * 0.08, width: cW * 0.105, height: cW * 0.105, borderRadius: cW * 0.024, boxShadow: `0 ${cW * 0.014}px ${cW * 0.04}px rgba(35,62,105,.16)` }} draggable={false} />
            <Caption cW={cW} label="LOACELL" headline={<>레이드 준비를<br />한곳에서 끝내세요</>} style={{ top: cW * 0.23, left: "8%", width: "84%" }} />
            <Frame src={screen("01_home")} alt="LoaCell 홈" style={{ position: "absolute", zIndex: 6, bottom: 0, left: "50%", width: `${width}%`, transform: "translateX(-50%) translateY(12%)" }} />
          </div>
        );
      },
    },
    {
      id: "raid-list",
      render: ({ cW, cH }) => {
        const width = phoneW(cW, cH, 0.76) * 100;
        return (
          <div style={{ width: "100%", height: "100%", position: "relative", overflow: "hidden", background: `linear-gradient(155deg, ${THEME.primaryContainer} 0%, #F3F6FF 52%, #FFFFFF 100%)` }}>
            <div style={{ position: "absolute", top: cW * 0.08, right: cW * 0.07, color: THEME.primary, fontSize: cW * 0.2, lineHeight: 1, opacity: 0.12, fontWeight: 900 }}>02</div>
            <Caption cW={cW} label="레이드 목록" headline={<>필요한 레이드만<br />빠르게 골라보세요</>} style={{ top: cW * 0.1, left: "8%", width: "84%" }} />
            <Frame src={screen("02_room")} alt="레이드 목록" style={{ position: "absolute", zIndex: 5, bottom: 0, right: "-7%", width: `${width}%`, transform: "translateY(10%) rotate(1.8deg)", transformOrigin: "bottom right" }} />
            <div style={{ position: "absolute", zIndex: 9, left: "6%", bottom: "27%", padding: `${cW * 0.025}px ${cW * 0.04}px`, color: THEME.onPrimaryContainer, background: "rgba(255,255,255,.88)", border: `1px solid rgba(65,95,145,.15)`, borderRadius: cW * 0.04, boxShadow: `0 ${cW * 0.02}px ${cW * 0.06}px rgba(35,62,105,.14)`, fontSize: cW * 0.033, fontWeight: 700 }}>필터로 빠르게</div>
          </div>
        );
      },
    },
    {
      id: "members",
      render: ({ cW, cH }) => {
        const width = phoneW(cW, cH, 0.75) * 100;
        return (
          <div style={{ width: "100%", height: "100%", position: "relative", overflow: "hidden", background: `radial-gradient(circle at 82% 18%, #476DA8 0%, ${THEME.primaryDeep} 32%, #101B2E 100%)` }}>
            <Glow cW={cW} color="rgba(214,227,255,.18)" style={{ top: cW * 0.08, right: -cW * 0.2 }} />
            <Caption cW={cW} inverse label="참여자" headline={<>함께할 파티원을<br />한눈에 확인하세요</>} style={{ top: cW * 0.1, left: "8%", width: "84%" }} />
            <Frame src={screen("03_room_user")} alt="참여자 목록" style={{ position: "absolute", zIndex: 6, bottom: 0, left: "-6%", width: `${width}%`, transform: "translateY(11%) rotate(-2deg)", transformOrigin: "bottom left" }} />
            <div style={{ position: "absolute", right: "7%", bottom: "30%", zIndex: 8, display: "grid", gap: cW * 0.025 }}>
              {["대표 캐릭터", "아이템 레벨", "보유 캐릭터"].map((text) => <div key={text} style={{ padding: `${cW * 0.024}px ${cW * 0.035}px`, borderRadius: 999, background: "rgba(255,255,255,.12)", border: "1px solid rgba(255,255,255,.18)", color: THEME.white, fontSize: cW * 0.029, fontWeight: 700 }}>{text}</div>)}
            </div>
          </div>
        );
      },
    },
    {
      id: "raid-party",
      render: ({ cW, cH }) => {
        const width = phoneW(cW, cH, 0.77) * 100;
        return (
          <div style={{ width: "100%", height: "100%", position: "relative", overflow: "hidden", background: `linear-gradient(180deg, #FFFFFF 0%, ${THEME.surfaceBlue} 68%, ${THEME.primaryContainer} 100%)` }}>
            <Caption cW={cW} label="공대 구성" headline={<>공대 구성을<br />빈틈없이 채우세요</>} align="center" style={{ top: cW * 0.095, left: "7%", width: "86%" }} />
            <div style={{ position: "absolute", left: "5%", top: "29%", display: "grid", gap: cW * 0.02, zIndex: 9 }}>
              {["1파티", "2파티"].map((text, index) => <div key={text} style={{ width: cW * 0.15, height: cW * 0.15, borderRadius: cW * 0.04, display: "grid", placeItems: "center", color: index === 0 ? THEME.white : THEME.primary, background: index === 0 ? THEME.primary : THEME.primaryContainer, fontSize: cW * 0.03, fontWeight: 800, boxShadow: `0 ${cW * 0.012}px ${cW * 0.035}px rgba(35,62,105,.12)` }}>{text}</div>)}
            </div>
            <Frame src={screen("04_room_party")} alt="공대 구성" style={{ position: "absolute", zIndex: 6, bottom: 0, left: "50%", width: `${width}%`, transform: "translateX(-43%) translateY(12%)" }} />
          </div>
        );
      },
    },
    {
      id: "party-slots",
      render: ({ cW, cH }) => {
        const width = phoneW(cW, cH, 0.73) * 100;
        return (
          <div style={{ width: "100%", height: "100%", position: "relative", overflow: "hidden", background: `linear-gradient(150deg, ${THEME.primaryContainer} 0%, #E8EFFF 46%, #FAFBFF 100%)` }}>
            <div style={{ position: "absolute", width: cW * 0.55, height: cW * 0.55, border: `${cW * 0.025}px solid rgba(65,95,145,.12)`, borderRadius: "50%", right: -cW * 0.18, top: cW * 0.06 }} />
            <Caption cW={cW} label="파티 편성" headline={<>파티 편성을<br />빠르게 끝내세요</>} style={{ top: cW * 0.105, left: "8%", width: "84%" }} />
            <Frame src={screen("05_party_1")} alt="파티 편성" style={{ position: "absolute", zIndex: 6, bottom: 0, right: "2%", width: `${width}%`, transform: "translateY(9%)" }} />
            <div style={{ position: "absolute", left: "6%", bottom: "23%", zIndex: 9, width: cW * 0.2, height: cW * 0.2, borderRadius: "50%", display: "grid", placeItems: "center", background: THEME.primary, color: THEME.white, fontSize: cW * 0.09, fontWeight: 400, boxShadow: `0 ${cW * 0.025}px ${cW * 0.07}px rgba(35,62,105,.28)` }}>+</div>
          </div>
        );
      },
    },
    {
      id: "calendar",
      render: ({ cW, cH }) => {
        const width = phoneW(cW, cH, 0.75) * 100;
        return (
          <div style={{ width: "100%", height: "100%", position: "relative", overflow: "hidden", background: `linear-gradient(145deg, #101B2E 0%, ${THEME.primaryDeep} 58%, #456AA2 100%)` }}>
            <Caption cW={cW} inverse label="일정 조율" headline={<>겹치는 시간을<br />바로 찾아보세요</>} style={{ top: cW * 0.1, left: "8%", width: "84%" }} />
            <div style={{ position: "absolute", left: "5%", bottom: "31%", zIndex: 9, display: "flex", gap: cW * 0.018 }}>
              {["수", "목", "금", "토", "일"].map((day, index) => <div key={day} style={{ width: cW * 0.095, height: cW * 0.095, borderRadius: cW * 0.026, display: "grid", placeItems: "center", color: index === 3 ? THEME.onPrimaryContainer : "rgba(255,255,255,.78)", background: index === 3 ? THEME.primaryContainer : "rgba(255,255,255,.10)", border: "1px solid rgba(255,255,255,.14)", fontSize: cW * 0.03, fontWeight: 800 }}>{day}</div>)}
            </div>
            <Frame src={screen("06_calendar")} alt="레이드 캘린더" style={{ position: "absolute", zIndex: 6, bottom: 0, right: "-8%", width: `${width}%`, transform: "translateY(13%) rotate(1.5deg)", transformOrigin: "bottom right" }} />
          </div>
        );
      },
    },
  ];
}

const FEATURE_SLIDE: SlideDef = {
  id: "feature-graphic",
  render: ({ cW, cH }) => (
    <div style={{ width: "100%", height: "100%", position: "relative", overflow: "hidden", background: `linear-gradient(135deg, #101B2E 0%, ${THEME.primaryDeep} 52%, #5578AE 100%)` }}>
      <Glow cW={cW} color="rgba(214,227,255,.22)" style={{ right: -cW * 0.15, top: -cW * 0.16 }} />
      <div style={{ position: "absolute", left: cW * 0.065, top: "50%", transform: "translateY(-50%)", display: "flex", alignItems: "center", gap: cW * 0.026, zIndex: 8 }}>
        <img src={img("/app-icon.png")} alt="LoaCell" style={{ width: cW * 0.13, height: cW * 0.13, borderRadius: cW * 0.025, boxShadow: `0 ${cW * 0.014}px ${cW * 0.04}px rgba(0,0,0,.24)` }} draggable={false} />
        <div>
          <div style={{ color: THEME.white, fontSize: cW * 0.055, fontWeight: 900, letterSpacing: "-0.04em", lineHeight: 1 }}>LoaCell</div>
          <div style={{ color: "rgba(255,255,255,.74)", fontSize: cW * 0.025, fontWeight: 700, marginTop: cW * 0.013 }}>레이드 준비를 한곳에서 끝내세요</div>
        </div>
      </div>
      <AndroidPhone src={img("/screenshots/android/phone/ko/04_room_party.png")} alt="공대 구성" style={{ position: "absolute", zIndex: 5, width: cW * 0.19, right: cW * 0.075, top: cH * 0.08, transform: "rotate(5deg)" }} />
      <div style={{ position: "absolute", zIndex: 6, right: cW * 0.235, bottom: cH * 0.08, padding: `${cW * 0.012}px ${cW * 0.022}px`, borderRadius: 999, background: THEME.primaryContainer, color: THEME.onPrimaryContainer, fontSize: cW * 0.018, fontWeight: 800 }}>일정 · 참여자 · 공대 구성</div>
    </div>
  ),
};

function Preview({ slide, cW, cH, onExport }: { slide: SlideDef; cW: number; cH: number; onExport: () => void }) {
  const hostRef = useRef<HTMLDivElement>(null);
  const [scale, setScale] = useState(0.2);
  useEffect(() => {
    const host = hostRef.current;
    if (!host) return;
    const observer = new ResizeObserver(([entry]) => setScale(entry.contentRect.width / cW));
    observer.observe(host);
    return () => observer.disconnect();
  }, [cW]);
  return (
    <div>
      <div ref={hostRef} style={{ width: "100%", aspectRatio: `${cW}/${cH}`, position: "relative", overflow: "hidden", borderRadius: 18, boxShadow: "0 12px 32px rgba(22,36,62,.13)", background: "white" }}>
        <div style={{ position: "absolute", width: cW, height: cH, transform: `scale(${scale})`, transformOrigin: "top left" }}>{slide.render({ cW, cH })}</div>
      </div>
      <button onClick={onExport} style={{ marginTop: 10, width: "100%", minHeight: 40, border: "1px solid #D9DFEA", borderRadius: 10, background: "white", color: THEME.primaryDeep, cursor: "pointer", fontSize: 13, fontWeight: 700 }}>이 장면 내보내기</button>
    </div>
  );
}

async function captureSlide(el: HTMLElement, w: number, h: number) {
  const previous = { left: el.style.left, opacity: el.style.opacity, zIndex: el.style.zIndex };
  el.style.left = "0px";
  el.style.opacity = "1";
  el.style.zIndex = "-1";
  const options = { width: w, height: h, pixelRatio: 1, cacheBust: true };
  await toPng(el, options);
  const dataUrl = await toPng(el, options);
  el.style.left = previous.left;
  el.style.opacity = previous.opacity;
  el.style.zIndex = previous.zIndex;
  return dataUrl;
}

function download(dataUrl: string, filename: string) {
  const anchor = document.createElement("a");
  anchor.href = dataUrl;
  anchor.download = filename;
  anchor.click();
}

export default function ScreenshotsPage() {
  const [device, setDevice] = useState<Device>("iphone");
  const [sizeIndex, setSizeIndex] = useState(0);
  const [ready, setReady] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [exporting, setExporting] = useState<string | null>(null);
  const exportRefs = useRef<Array<HTMLDivElement | null>>([]);

  useEffect(() => {
    preloadAllImages().then(() => setReady(true)).catch((cause: unknown) => setError(cause instanceof Error ? cause.message : "이미지를 불러오지 못했습니다."));
  }, []);

  const sizes = device === "iphone" ? IPHONE_SIZES : device === "android" ? ANDROID_SIZES : FEATURE_SIZES;
  const size = sizes[sizeIndex] ?? sizes[0];
  const slides = useMemo(() => {
    if (device === "feature-graphic") return [FEATURE_SLIDE];
    return device === "iphone"
      ? makeSlides(IPhone, "screenshots/apple/iphone/ko")
      : makeSlides(AndroidPhone, "screenshots/android/phone/ko");
  }, [device, ready]);

  const exportOne = async (index: number) => {
    const el = exportRefs.current[index];
    if (!el) return;
    setExporting(`${index + 1}/${slides.length}`);
    try {
      const dataUrl = await captureSlide(el, size.w, size.h);
      download(dataUrl, `${String(index + 1).padStart(2, "0")}-${slides[index].id}-ko-${size.w}x${size.h}.png`);
    } finally {
      setExporting(null);
    }
  };

  const exportAll = async () => {
    try {
      for (let index = 0; index < slides.length; index += 1) {
        setExporting(`${index + 1}/${slides.length}`);
        const el = exportRefs.current[index];
        if (!el) continue;
        const dataUrl = await captureSlide(el, size.w, size.h);
        download(dataUrl, `${String(index + 1).padStart(2, "0")}-${slides[index].id}-ko-${size.w}x${size.h}.png`);
        await new Promise((resolve) => setTimeout(resolve, 300));
      }
    } finally {
      setExporting(null);
    }
  };

  if (error) return <main style={{ padding: 32, color: "#A42323" }}>{error}</main>;
  if (!ready) return <main style={{ padding: 32, color: THEME.primaryDeep }}>스토어 이미지를 준비하고 있어요…</main>;

  return (
    <main style={{ minHeight: "100vh", background: "#F1F3F7", position: "relative", overflowX: "hidden" }}>
      <header style={{ position: "sticky", top: 0, zIndex: 50, display: "flex", alignItems: "center", background: "rgba(255,255,255,.94)", borderBottom: "1px solid #E2E6ED", backdropFilter: "blur(16px)" }}>
        <div style={{ flex: 1, minWidth: 0, display: "flex", alignItems: "center", gap: 10, padding: "10px 16px", overflowX: "auto" }}>
          <strong style={{ color: THEME.ink, whiteSpace: "nowrap", fontSize: 14 }}>LoaCell · 스토어 스크린샷</strong>
          <div style={{ display: "flex", gap: 4, padding: 4, background: "#EFF1F5", borderRadius: 9, flexShrink: 0 }}>
            {(["iphone", "android", "feature-graphic"] as Device[]).map((value) => <button key={value} onClick={() => { setDevice(value); setSizeIndex(0); }} style={{ padding: "6px 13px", border: 0, borderRadius: 7, background: device === value ? "white" : "transparent", color: device === value ? THEME.primary : "#687080", boxShadow: device === value ? "0 1px 4px rgba(20,30,50,.1)" : "none", cursor: "pointer", fontSize: 12, fontWeight: 700, whiteSpace: "nowrap" }}>{value === "iphone" ? "iPhone" : value === "android" ? "Android" : "Feature Graphic"}</button>)}
          </div>
          {sizes.length > 1 && <select value={sizeIndex} onChange={(event) => setSizeIndex(Number(event.target.value))} style={{ padding: "6px 10px", border: "1px solid #D9DFE8", borderRadius: 8, background: "white", color: THEME.ink, fontSize: 12 }}>{sizes.map((entry, index) => <option key={entry.label} value={index}>{entry.label} — {entry.w}×{entry.h}</option>)}</select>}
          <span style={{ color: "#7B8391", fontSize: 12, whiteSpace: "nowrap" }}>KO · {size.w}×{size.h}</span>
        </div>
        <div style={{ flexShrink: 0, padding: "10px 16px", borderLeft: "1px solid #E2E6ED" }}>
          <button onClick={exportAll} disabled={Boolean(exporting)} style={{ padding: "8px 18px", border: 0, borderRadius: 8, background: exporting ? "#9AB0D2" : THEME.primary, color: "white", cursor: exporting ? "default" : "pointer", fontSize: 12, fontWeight: 700, whiteSpace: "nowrap" }}>{exporting ? `내보내는 중 ${exporting}` : "모두 내보내기"}</button>
        </div>
      </header>

      <section style={{ maxWidth: device === "feature-graphic" ? 1100 : 1440, margin: "0 auto", padding: "28px 24px 48px", display: "grid", gridTemplateColumns: device === "feature-graphic" ? "1fr" : "repeat(auto-fit, minmax(250px, 1fr))", gap: 24 }}>
        {slides.map((slide, index) => <Preview key={slide.id} slide={slide} cW={size.w} cH={size.h} onExport={() => exportOne(index)} />)}
      </section>

      <div aria-hidden="true" style={{ position: "absolute", left: -9999, top: 0 }}>
        {slides.map((slide, index) => <div key={slide.id} ref={(element) => { exportRefs.current[index] = element; }} style={{ position: "absolute", left: -9999, top: 0, width: size.w, height: size.h }}>{slide.render({ cW: size.w, cH: size.h })}</div>)}
      </div>
    </main>
  );
}
